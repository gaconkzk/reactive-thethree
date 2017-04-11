package mixit.controller

import org.gaconkzk.reactive.thethree.TheFliesProperties
import org.gaconkzk.reactive.thethree.util.permanentRedirect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.TEXT_HTML
import org.springframework.web.reactive.function.server.router

@Configuration
class RedirectRoutes(val properties: TheFliesProperties) {

    val GOOGLE_DRIVE_URI = "https://drive.google.com/open"

    @Bean
    fun redirectRouter() = router {
        accept(TEXT_HTML).nest {
            (GET("/member/{login}")
                    or GET("/profile/{login}")
                    or GET("/member/sponsor/{login}")
                    or GET("/member/member/{login}")) { permanentRedirect("${properties.baseUri}/user/${it.pathVariable("login")}") }
            GET("/sponsors/") { permanentRedirect("$${properties.baseUri}/sponsors") }

            GET("/about/") { permanentRedirect("${properties.baseUri}/about") }
            GET("/home") { permanentRedirect("${properties.baseUri}/") }

        }
    }

}



