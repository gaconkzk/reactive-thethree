package org.gaconkzk.reactive.thethree.model

import org.gaconkzk.reactive.thethree.util.toSlug
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime


@Document
data class Talk(
        val format: TalkFormat,
        val event: String,
        val title: String,
        val summary: String,
        val speakerIds: List<String> = emptyList(),
        val language: Language = Language.FRENCH,
        val addedAt: LocalDateTime = LocalDateTime.now(),
        val description: String? = null,
        val topic: String? = null,
        val video: String? = null,
        val room: Room? = null,
        val start: LocalDateTime? = null,
        val end: LocalDateTime? = null,
        val slug: String = title.toSlug(),
        @Id val id: String? = null
)

enum class TalkFormat(val duration: Int) {
    TALK(50),
    LIGHTNING_TALK(5),
    WORKSHOP(110),
    RANDOM(25),
    KEYNOTE(25)
}

@Suppress("UNUSED_PARAMETER")
enum class Room(capacity: Int) {
    AMPHI1(500),
    AMPHI2(200),
    ROOM1(110),
    ROOM2(110),
    ROOM3(30),
    ROOM4(30),
    ROOM5(30),
    ROOM6(30),
    ROOM7(50),
    UNKNOWN(0);
}