package io.renegadelabs.canary.api.identities.controller

import io.renegadelabs.canary.api.identities.controller.request.CreateSessionRequest
import io.renegadelabs.canary.api.identities.domain.TokenPair
import io.renegadelabs.canary.api.identities.service.JwtAuthenticationService
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RequestMapping(TokenIssuanceController.BASE_PATH)
@RestController
class TokenIssuanceController(
    private val jwtAuthenticationService: JwtAuthenticationService
) {

    companion object {
        const val BASE_PATH = "/sessions"
        const val POST_SESSION_MAPPING: String = ""
        const val GET_SESSION_REFRESH_MAPPING: String = "/refresh"
    }

    @PreAuthorize("permitAll()")
    @PostMapping(POST_SESSION_MAPPING)
    fun postSession(@RequestBody createSessionRequest: CreateSessionRequest): Mono<ResponseEntity<TokenPair>> {
        // TODO: seems like this endpoint returns 200 OK when logging in as an identity that does not exist
        return this.jwtAuthenticationService.createJwtAuthentication(createSessionRequest.username, createSessionRequest.password)
            .map { ResponseEntity.ok(it) }
    }

    @PreAuthorize("@preAuthorizationPredicates.hasRefreshAuthority(getAuthentication())")
    @GetMapping(GET_SESSION_REFRESH_MAPPING)
    fun refreshSession(@RequestHeader(HttpHeaders.AUTHORIZATION) authorizationHeader: String): Mono<ResponseEntity<TokenPair>> {
        return this.jwtAuthenticationService.refreshTokenPair(authorizationHeader)
            .map { ResponseEntity.ok(it) }
    }
}