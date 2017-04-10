package org.gaconkzk.reactive.thethree.model

import org.gaconkzk.reactive.thethree.util.toSlug
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime


@Document
data class Post(
        val authorId: String,
        val addedAt: LocalDateTime = LocalDateTime.now(),
        val title: Map<Language, String> = emptyMap(),
        val headline: Map<Language, String> = emptyMap(),
        val content: Map<Language, String>? = emptyMap(),
        @Id val id: String? = null,
        val slug: Map<Language, String> = title.entries.map { (k, v) -> Pair(k, v.toSlug()) }.toMap()
)