package io.renegadelabs.canary.api.identities.controller

import io.renegadelabs.canary.api.identities.controller.request.CreateSessionRequest
import io.renegadelabs.canary.api.identities.domain.Session
import io.renegadelabs.canary.api.identities.service.SessionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/sessions")
class SessionController(
    private val sessionService: SessionService
) {

    companion object {
        const val POST_SESSION_REQUEST_MAPPING: String = "/"
    }

    @PostMapping(POST_SESSION_REQUEST_MAPPING)
    fun postSession(@RequestBody createSessionRequest: CreateSessionRequest): Mono<ResponseEntity<Session>> {
        return this.sessionService.createSession(createSessionRequest.username, createSessionRequest.password)
            .map { ResponseEntity.ok(it) }
    }
}