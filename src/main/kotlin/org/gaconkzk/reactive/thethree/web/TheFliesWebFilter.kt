package org.gaconkzk.reactive.thethree.web

import org.gaconkzk.reactive.thethree.TheFliesProperties
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.ACCEPT_LANGUAGE
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.net.URI
import java.util.*


class TheFliesWebFilter(val properties: TheFliesProperties) : WebFilter {

    private val redirectDoneAttribute = "redirectDone"

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain) =
            if (exchange.request.headers.host?.hostString?.endsWith("theflies.xyz") ?: false) {
                val response = exchange.response
                response.statusCode = HttpStatus.PERMANENT_REDIRECT
                response.headers.location = URI("${properties.baseUri}${exchange.request.uri.path}")
                Mono.empty()
            } else if (exchange.request.uri.path.startsWith("/admin")) {
                exchange.session.then { session ->
                    if (session.attributes["username"] != null) {
                        chain.filter(exchange)
                    } else {
                        val response = exchange.response
                        response.statusCode = HttpStatus.TEMPORARY_REDIRECT
                        response.headers.location = URI("${properties.baseUri}/login")
                        Mono.empty()
                    }
                }
            } else if (exchange.request.uri.path.startsWith("/en/"))
                chain.filter(exchange.mutate().request(exchange.request.mutate()
                        .path(exchange.request.uri.path.substring(3))
                        .header(ACCEPT_LANGUAGE, "en").build()).build())
            else if (exchange.request.uri.path == "/" &&
                    (exchange.request.headers.acceptLanguageAsLocale ?: Locale("vi", "VN")).language != "vn" &&
                    !isSearchEngineCrawler(exchange)) {
                val response = exchange.response
                exchange.session.then { session ->
                    if (session.attributes[redirectDoneAttribute] == true)
                        chain.filter(exchange.mutate().request(exchange.request.mutate().header(ACCEPT_LANGUAGE, "vn").build()).build())
                    else {
                        response.statusCode = HttpStatus.TEMPORARY_REDIRECT
                        response.headers.location = URI("${properties.baseUri}/en/")
                        session.attributes[redirectDoneAttribute] = true
                        session.save()
                    }
                }
            } else
                chain.filter(exchange.mutate().request(exchange.request.mutate().header(ACCEPT_LANGUAGE, "vn").build()).build())

    private fun isSearchEngineCrawler(exchange: ServerWebExchange): Boolean {
        val userAgent = exchange.request.headers.getFirst(HttpHeaders.USER_AGENT) ?: ""
        val bots = arrayOf("Google", "Bingbot", "Qwant", "Bingbot", "Slurp", "DuckDuckBot", "Baiduspider")
        return bots.any { userAgent.contains(it) }
    }
}

