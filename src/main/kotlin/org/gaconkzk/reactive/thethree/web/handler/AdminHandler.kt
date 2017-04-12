package org.gaconkzk.reactive.thethree.web.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.gaconkzk.reactive.thethree.TheFliesProperties
import org.gaconkzk.reactive.thethree.model.Language.ENGLISH
import org.gaconkzk.reactive.thethree.model.Language.VIETNAMESE
import org.gaconkzk.reactive.thethree.model.Link
import org.gaconkzk.reactive.thethree.model.Role
import org.gaconkzk.reactive.thethree.model.Role.STAFF
import org.gaconkzk.reactive.thethree.model.Role.USER
import org.gaconkzk.reactive.thethree.model.User
import org.gaconkzk.reactive.thethree.repository.UserRepository
import org.gaconkzk.reactive.thethree.util.MarkdownConverter
import org.gaconkzk.reactive.thethree.util.seeOther
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono


@Component
class AdminHandler(val userRepository: UserRepository,
                   val markdownConverter: MarkdownConverter,
                   val properties: TheFliesProperties,
                   val objectMapper: ObjectMapper) {

    fun admin(req: ServerRequest) = ok().render("admin", mapOf(Pair("title", "admin.title")))

    fun adminUsers(req: ServerRequest) = ok().render("admin-users", mapOf(Pair("users", userRepository.findAll()), Pair("title", "admin.users.title")))

    fun createUser(req: ServerRequest): Mono<ServerResponse> = this.adminUser()

    fun editUser(req: ServerRequest): Mono<ServerResponse> =
            userRepository.findOne(req.pathVariable("login"))
                    .flatMap(this::adminUser)

    fun adminDeleteUser(req: ServerRequest): Mono<ServerResponse> =
            req.body(BodyExtractors.toFormData()).flatMap {
                val formData = it.toSingleValueMap()
                userRepository
                        .deleteOne(formData["login"]!!)
                        .then { seeOther("${properties.baseUri}/admin/users") }
            }

    private fun adminUser(user: User = User("", "", "", "")) = ok().render("admin-user", mapOf(
            Pair("user", user),
            Pair("description-vn", user.description[VIETNAMESE]),
            Pair("description-en", user.description[ENGLISH]),
            Pair("roles", listOf(
                    Pair(USER, USER == user.role),
                    Pair(STAFF, STAFF == user.role)
            )),
            Pair("links", user.links.toJson())

    ))

    fun adminSaveUser(req: ServerRequest) : Mono<ServerResponse> {
        return req.body(BodyExtractors.toFormData()).flatMap {
            val formData = it.toSingleValueMap()
            val user = User(
                    login = formData["login"]!!,
                    firstname = formData["firstname"]!!,
                    lastname = formData["lastname"]!!,
                    email = if (formData["email"] == "") null else formData["email"],
                    emailHash = if (formData["emailHash"] == "") null else formData["emailHash"],
                    photoUrl = if (formData["photoUrl"] == "") { if (formData["emailHash"] == "") "/images/png/mxt-icon--default-avatar.png" else null } else { if (formData["emailHash"] == "") formData["photoUrl"] else null },
                    company = if (formData["company"] == "") null else formData["company"],
                    description = mapOf(Pair(VIETNAMESE, formData["description-fr"]!!), Pair(ENGLISH, formData["description-en"]!!)),
                    role = Role.valueOf(formData["role"]!!),
                    links =  formData["links"]!!.toLinks(),
                    legacyId = if (formData["legacyId"] == "") null else formData["legacyId"]!!.toLong()
            )
            userRepository.save(user).then { seeOther("${properties.baseUri}/admin/users") }
        }
    }

    private fun Any.toJson() = objectMapper.writeValueAsString(this).replace("\"", "&quot;")

    private fun String.toLinks() = objectMapper.readValue<List<Link>>(this)

}


