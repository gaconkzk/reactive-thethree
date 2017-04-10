package org.gaconkzk.reactive.thethree.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.gaconkzk.reactive.thethree.model.Talk
import org.gaconkzk.reactive.thethree.util.*
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.domain.Sort.Order
import org.springframework.data.domain.Sort.by
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux


@Repository
class TalkRepository(val template: ReactiveMongoTemplate) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun initData() {
        val objectMapper: ObjectMapper = Jackson2ObjectMapperBuilder.json().build()
        if (count().block() == 0L) {
            listOf(2012, 2013, 2014, 2015, 2016, 2017).forEach { year ->
                val talksResource = ClassPathResource("data/talks_$year.json")
                val talks: List<Talk> = objectMapper.readValue(talksResource.inputStream)
                talks.forEach { save(it).block() }
            }
            logger.info("Talks data initialization complete")
        }
    }

    fun count() = template.count<Talk>()

    fun findByEvent(eventId: String, topic: String? = null): Flux<Talk> {
        val criteria = where("event").`is`(eventId)
        if (topic != null) criteria.and("topic").`is`(topic)
        return template.find<Talk>(Query(criteria).with(by(Order(ASC, "start"))))
    }


    fun findAll(): Flux<Talk> = template.find<Talk>(Query().with(by(Order(ASC, "start"))))

    fun findOne(id: String) = template.findById<Talk>(id)

    fun findBySlug(slug: String) =
            template.findOne<Talk>(Query(where("slug").`is`(slug)))

    fun findByEventAndSlug(eventId: String, slug: String) =
            template.findOne<Talk>(Query(where("slug").`is`(slug).and("event").`is`(eventId)))

    fun deleteAll() = template.remove<Talk>(Query())

    fun deleteOne(id: String) = template.remove<Talk>(Query(where("_id").`is`(id)))

    fun save(talk: Talk) = template.save(talk)

}
