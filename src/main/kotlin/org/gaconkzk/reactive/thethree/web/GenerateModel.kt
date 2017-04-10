package org.gaconkzk.reactive.thethree.web

import com.samskivert.mustache.Mustache
import org.springframework.context.MessageSource
import org.springframework.web.server.WebSession
import org.springframework.web.util.UriUtils
import java.util.*

fun generateModel(baseUri: String, path: String, locale: Locale?, session: WebSession, messageSource: MessageSource) = mutableMapOf<String, Any>().apply {
        val username = session.getAttribute<String>("username")
        if (username.isPresent) {
            this["username"] = username.get()
            if (username.get() == "theflies") this["admin"] = true
        }
        if (locale != null) {
            this["locale"] = locale.toString()
            this["localePrefix"] = if (locale.language == "en") "/en" else ""
            this["en"] = locale.language == "en"
            this["vn"] = locale.language == "vn"
            this["switchLangUrl"] = if (locale.language == "en") path else "/en" + path
            this["uri"] = "$baseUri$path"
        }
        this["i18n"] = Mustache.Lambda { frag, out ->
            val tokens = frag.execute().split("|")
            out.write(messageSource.getMessage(tokens[0], tokens.slice(IntRange(1, tokens.size - 1)).toTypedArray(), locale))
        }
        this["urlEncode"] = Mustache.Lambda { frag, out -> out.write(UriUtils.encodePathSegment(frag.execute(), "UTF-8")) }
}.toMap()
