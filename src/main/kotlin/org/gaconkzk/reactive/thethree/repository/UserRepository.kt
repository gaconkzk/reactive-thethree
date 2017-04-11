package org.gaconkzk.reactive.thethree.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.gaconkzk.reactive.thethree.model.Role
import org.gaconkzk.reactive.thethree.model.User
import org.gaconkzk.reactive.thethree.util.*
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono


@Repository
class UserRepository(val template: ReactiveMongoTemplate) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun initData() {
        val objectMapper: ObjectMapper = Jackson2ObjectMapperBuilder.json().build()
        if (count().block() == 0L) {
            val usersResource = ClassPathResource("data/users.json")
            val users: List<User> = objectMapper.readValue(usersResource.inputStream)
            users.forEach { save(it).block() }
            logger.info("Users data initialization complete")
        }
    }

    fun count() = template.count<User>()

    fun findByRole(role: Role) =
            template.find<User>(Query(where("role").`is`(role)))

    fun findOneByRole(login: String, role: Role) =
        template.findOne<User>(Query(where("role").`in`(role).and("_id").`is`(login)))


    fun findAll() = template.findAll<User>()

    fun findOne(login: String) = template.findById<User>(login)

    fun findMany(logins: List<String>) = template.find<User>(Query(where("_id").`in`(logins)))

    fun findByLegacyId(id: Long) =
            template.findOne<User>(Query(where("legacyId").`is`(id)))

    fun deleteAll() = template.remove<User>(Query())

    fun deleteOne(login: String) = template.remove<User>(Query(where("_id").`is`(login)))

    fun save(user: User) = template.save(user)

    fun save(user: Mono<User>) = template.save(user)

}
