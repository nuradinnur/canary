package io.renegadelabs.canary.api.identities.controller

import io.renegadelabs.canary.api.identities.controller.request.CreateSessionRequest
import io.renegadelabs.canary.api.identities.domain.Session
import io.renegadelabs.canary.api.identities.service.SessionService
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.*

@RestController
@RequestMapping("/sessions")
class SessionController(
    private val sessionService: SessionService
) {

    companion object {
        const val POST_SESSION_REQUEST_MAPPING: String = ""
        const val GET_SESSION_REFRESH_REQUEST_MAPPING: String = "/refresh"
    }

    @PreAuthorize("permitAll()")
    @PostMapping(POST_SESSION_REQUEST_MAPPING)
    fun postSession(@RequestBody createSessionRequest: CreateSessionRequest): Mono<ResponseEntity<Session>> {
        return this.sessionService.createSession(createSessionRequest.username, createSessionRequest.password)
            .map { ResponseEntity.ok(it) }
    }

    @PreAuthorize("hasAuthority('REFRESH')")
    @GetMapping(GET_SESSION_REFRESH_REQUEST_MAPPING)
    fun refreshSession(@RequestHeader(HttpHeaders.AUTHORIZATION) authorizationHeader: String): Mono<ResponseEntity<Session>> {
        return this.sessionService.refreshSession(authorizationHeader)
            .map { ResponseEntity.ok(it) }
    }
}