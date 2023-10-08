package io.renegadelabs.canary.api.identities.controller

import io.renegadelabs.canary.api.identities.controller.request.CreateSessionRequest
import io.renegadelabs.canary.api.identities.domain.TokenPair
import io.renegadelabs.canary.api.identities.service.TokenIssuanceService
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/sessions")
class TokenIssuanceController(
    private val tokenIssuanceService: TokenIssuanceService
) {

    companion object {
        const val POST_SESSION_REQUEST_MAPPING: String = ""
        const val GET_SESSION_REFRESH_REQUEST_MAPPING: String = "/refresh"
    }

    @PreAuthorize("permitAll()")
    @PostMapping(POST_SESSION_REQUEST_MAPPING)
    fun postSession(@RequestBody createSessionRequest: CreateSessionRequest): Mono<ResponseEntity<TokenPair>> {
        return this.tokenIssuanceService.createTokenPair(createSessionRequest.username, createSessionRequest.password)
            .map { ResponseEntity.ok(it) }
    }

    @PreAuthorize("hasAuthority('REFRESH')")
    @GetMapping(GET_SESSION_REFRESH_REQUEST_MAPPING)
    fun refreshSession(@RequestHeader(HttpHeaders.AUTHORIZATION) authorizationHeader: String): Mono<ResponseEntity<TokenPair>> {
        return this.tokenIssuanceService.refreshTokenPair(authorizationHeader)
            .map { ResponseEntity.ok(it) }
    }
}