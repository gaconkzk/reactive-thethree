package org.gaconkzk.reactive.thethree.web.handler

import mixit.TheFliesProperties
import mixit.model.*
import mixit.repository.TalkRepository
import mixit.repository.UserRepository
import mixit.util.*
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*
import org.springframework.web.util.UriUtils
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime


@Component
class TalkHandler(val repository: TalkRepository,
                  val userRepository: UserRepository,
                  val markdownConverter: MarkdownConverter,
                  val properties: TheFliesProperties) {

    fun findByEventView(year: Int, req: ServerRequest, topic: String? = null) = ok().render("talks", mapOf(
            Pair("talks", repository
                    .findByEvent(year.toString(), topic)
                    .collectList()
                    .then { talks -> userRepository
                            .findMany(talks.flatMap(Talk::speakerIds))
                            .collectMap(User::login)
                            .map { speakers -> talks.map { it.toDto(req.language(), it.speakerIds.mapNotNull { speakers[it] }, markdownConverter) } }
                    }),
            Pair("year", year),
            Pair("title", when(topic) { null -> "talks.title.html|$year" else -> "talks.title.html.$topic|$year" }),
            Pair("baseUri", UriUtils.encode(properties.baseUri, StandardCharsets.UTF_8)),
            Pair("topic", topic)
    ))



    fun findOneView(year: Int, req: ServerRequest) = repository.findByEventAndSlug(year.toString(), req.pathVariable("slug")).then { talk ->
        userRepository.findMany(talk.speakerIds).collectList().then { speakers ->
        ok().render("talk", mapOf(
                Pair("talk", talk.toDto(req.language(), speakers!!, markdownConverter)),
                Pair("speakers", speakers.map { it.toDto(req.language(), markdownConverter) }),
                Pair("title", "talk.html.title|${talk.title}"),
                Pair("baseUri", UriUtils.encode(properties.baseUri, StandardCharsets.UTF_8))))
    }}

    fun findOne(req: ServerRequest) = ok().json().body(repository.findOne(req.pathVariable("login")))

    fun findByEventId(req: ServerRequest) =
            ok().json().body(repository.findByEvent(req.pathVariable("year")))

    fun redirectFromId(req: ServerRequest) = repository.findOne(req.pathVariable("id")).then { s ->
        permanentRedirect("${properties.baseUri}/${s.event}/${s.slug}")
    }

    fun redirectFromSlug(req: ServerRequest) = repository.findBySlug(req.pathVariable("slug")).then { s ->
        permanentRedirect("${properties.baseUri}/${s.event}/${s.slug}")
    }

    fun planning(req: ServerRequest) = ok().render("planning")
}

class TalkDto(
        val id: String?,
        val slug: String,
        val format: TalkFormat,
        val event: String,
        val title: String,
        val summary: String,
        val speakers: List<User>,
        val language: String,
        val addedAt: LocalDateTime,
        val description: String?,
        val topic: String?,
        val video: String?,
        val room: String?,
        val start: String?,
        val end: String?,
        val date: String?
)

fun Talk.toDto(lang: Language, speakers: List<User>, markdownConverter: MarkdownConverter) = TalkDto(
        id, slug, format, event, title,
        markdownConverter.toHTML(summary), speakers, language.name.toLowerCase(), addedAt,
        markdownConverter.toHTML(description), topic,
        video, "rooms.${room?.name?.toLowerCase()}" , start?.formatTalkTime(lang), end?.formatTalkTime(lang),
        start?.formatTalkDate(lang)
)
