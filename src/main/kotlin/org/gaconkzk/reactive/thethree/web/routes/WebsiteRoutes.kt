package mixit.web

import org.gaconkzk.reactive.thethree.TheFliesProperties
import org.gaconkzk.reactive.thethree.web.generateModel
import org.gaconkzk.reactive.thethree.web.handler.AdminHandler
import org.gaconkzk.reactive.thethree.web.handler.AuthenticationHandler
import org.gaconkzk.reactive.thethree.web.handler.GlobalHandler
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED
import org.springframework.http.MediaType.TEXT_HTML
import org.springframework.web.reactive.function.server.RenderingResponse
import org.springframework.web.reactive.function.server.RouterFunctions.resources
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.toMono


@Configuration
class WebsiteRoutes(val adminHandler: AdminHandler,
                    val authenticationHandler: AuthenticationHandler,
                    val globalHandler: GlobalHandler,
                    val messageSource: MessageSource,
                    val properties: TheFliesProperties) {


    @Bean
    @DependsOn("databaseInitializer")
    fun websiteRouter() = router {
        accept(TEXT_HTML).nest {
            GET("/", globalHandler::homeView)
            GET("/about", globalHandler::findAboutView)
            GET("/faq", globalHandler::faqView)

            // Authentication
            GET("/login", authenticationHandler::loginView)

            "/admin".nest {
                GET("/", adminHandler::admin)
                DELETE("/")
                GET("/users", adminHandler::adminUsers)
                GET("/users/edit/{login}", adminHandler::editUser)
                GET("/users/create", adminHandler::createUser)
            }
        }

        contentType(APPLICATION_FORM_URLENCODED).nest {
            POST("/login", authenticationHandler::login)
            //POST("/ticketing", ticketingHandler::submit)
            "/admin".nest {
                POST("/users", adminHandler::adminSaveUser)
                POST("/users/delete", adminHandler::adminDeleteUser)
            }
        }
    }.filter { request, next ->
        val locale = request.headers().asHttpHeaders().acceptLanguageAsLocale
        val session = request.session().block()
        val path = request.uri().path
        val model = generateModel(properties.baseUri!!, path, locale, session, messageSource)
        next.handle(request).then {
            response ->
            if (response is RenderingResponse)
                RenderingResponse
                        .from(response)
                        .modelAttributes(model)
                        .build()
            else response.toMono()
        }
    }

    @Bean
    @DependsOn("websiteRouter")
    fun resourceRouter() = resources("/**", ClassPathResource("static/"))

}

