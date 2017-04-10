package org.gaconkzk.reactive.thethree

import org.gaconkzk.reactive.thethree.repository.EventRepository
import org.gaconkzk.reactive.thethree.repository.PostRepository
import org.gaconkzk.reactive.thethree.repository.TalkRepository
import org.gaconkzk.reactive.thethree.repository.UserRepository

@org.springframework.stereotype.Component
class DatabaseInitializer(val userRepository: UserRepository,
                          val eventRepository: EventRepository,
                          val talkRepository: TalkRepository,
                          val postRepository: PostRepository) {

    @javax.annotation.PostConstruct
    fun init() {
        userRepository.initData()
        eventRepository.initData()
        talkRepository.initData()
        postRepository.initData()
    }
}