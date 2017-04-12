package org.gaconkzk.reactive.thethree.web.handler

import org.gaconkzk.reactive.thethree.TheFliesProperties
import org.gaconkzk.reactive.thethree.util.seeOther
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyExtractors.toFormData
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok


@Component
class AuthenticationHandler(val properties: TheFliesProperties) {

    fun loginView(req: ServerRequest) = ok().render("login")

    fun login(req: ServerRequest) = req.body(toFormData()).flatMap { data ->
        req.session().flatMap { session ->
            val formData = data.toSingleValueMap()
            if (formData["username"] == properties.admin.username && formData["password"] == properties.admin.password) {
                session.attributes["username"] =  data.toSingleValueMap()["username"]
                seeOther("${properties.baseUri}/admin")
            }
            else ok().render("login-error")
        }
    }
}
