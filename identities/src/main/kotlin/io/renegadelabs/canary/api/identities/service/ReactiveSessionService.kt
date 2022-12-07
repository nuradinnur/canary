package io.renegadelabs.canary.api.identities.service

import reactor.core.publisher.Mono

interface ReactiveSessionService {

    fun createSession(username: String, password: String): Mono<Pair<String, String>>

    fun refreshSession(jsonWebToken: String): Mono<String>
}