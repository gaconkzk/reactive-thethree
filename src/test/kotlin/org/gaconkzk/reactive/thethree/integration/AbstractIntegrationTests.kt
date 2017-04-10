package org.gaconkzk.reactive.thethree.integration

import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.reactive.function.client.WebClient

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
abstract class AbstractIntegrationTests {

    @LocalServerPort
    var port: Int? = null

    lateinit var client: WebClient

    @Before
    fun setup() {
        client = WebClient.create("http://localhost:$port")
    }

}