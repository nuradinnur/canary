package io.renegadelabs.canary.api.identities.service

import io.renegadelabs.canary.api.identities.domain.Session
import org.springframework.context.MessageSourceAware
import reactor.core.publisher.Mono

interface SessionService: MessageSourceAware {

    fun createSession(username: String, password: String): Mono<Session>

    fun refreshSession(jsonWebToken: String): Mono<Session>
}