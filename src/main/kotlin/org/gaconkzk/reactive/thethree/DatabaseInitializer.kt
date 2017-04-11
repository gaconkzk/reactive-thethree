package org.gaconkzk.reactive.thethree

import org.gaconkzk.reactive.thethree.repository.UserRepository
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class DatabaseInitializer(val userRepository: UserRepository) {

    @PostConstruct
    fun init() {
        userRepository.initData()
    }
}