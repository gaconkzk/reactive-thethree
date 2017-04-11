package org.gaconkzk.reactive.thethree

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("theflies")
class TheFliesProperties {
    var baseUri: String? = null
    val admin = Credential()

    class Credential {
        var username: String? = null
        var password: String? = null
    }
}


