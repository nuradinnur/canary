package io.renegadelabs.canary.api.identities.service

import org.springframework.context.MessageSourceAware
import reactor.core.publisher.Mono

interface SessionService: MessageSourceAware {

    fun createSession(username: String, password: String): Mono<Pair<String, String>>

    fun refreshSession(jsonWebToken: String): Mono<String>
}