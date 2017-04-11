package org.gaconkzk.reactive.thethree.web.handler

import org.gaconkzk.reactive.thethree.model.Role
import org.gaconkzk.reactive.thethree.repository.UserRepository
import org.gaconkzk.reactive.thethree.util.MarkdownConverter
import org.gaconkzk.reactive.thethree.util.language
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import java.util.*


@Component
class GlobalHandler(val userRepository: UserRepository,
                    val markdownConverter: MarkdownConverter) {

    fun findAboutView(req: ServerRequest) = userRepository.findByRole(Role.STAFF).collectList().then { u ->
        val users = u.map { it.toDto(req.language(), markdownConverter) }
        Collections.shuffle(users)
        ok().render("about", mapOf(Pair("staff", users), Pair("title", "about.title")))
    }

    fun mixteenView(req: ServerRequest) = ok().render("mixteen")

    fun faqView(req: ServerRequest) = ok().render("faq")

    fun comeToMixitView(req: ServerRequest) = ok().render("come")

    fun homeView(req: ServerRequest) = ok().render("home")
}

