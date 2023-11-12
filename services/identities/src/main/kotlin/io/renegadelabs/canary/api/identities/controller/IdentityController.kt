package io.renegadelabs.canary.api.identities.controller

import io.renegadelabs.canary.api.identities.controller.request.CreateIdentityRequest
import io.renegadelabs.canary.api.identities.domain.Identity
import io.renegadelabs.canary.api.identities.service.IdentityService
import io.renegadelabs.canary.api.shared.component.JwtAuthenticationReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@RequestMapping(IdentityController.BASE_PATH)
@RestController
class IdentityController(
    private val identityService: IdentityService,
    private val jwtAuthenticationReader: JwtAuthenticationReader,
    @Value("\${spring.webflux.base-path}")
    private val contextPath: String
) {

    companion object {
        const val BASE_PATH: String = ""
        const val GET_IDENTITY_MAPPING: String = ""
        const val GET_IDENTITY_PARAMETERIZED_MAPPING: String = "/{id}"
        const val POST_IDENTITY_MAPPING: String = ""
    }

    @PreAuthorize("@preAuthorizationPredicates.isUser(getAuthentication())")
    @GetMapping(GET_IDENTITY_MAPPING)
    fun getIdentity(@AuthenticationPrincipal authentication: Authentication): Mono<ResponseEntity<Identity>> {
        val username = this.jwtAuthenticationReader.getPrincipalAsUsername(authentication)
        return this.identityService.readIdentityByUsername(username)
            .map { ResponseEntity.ok(it) }
    }

    @PreAuthorize("@preAuthorizationPredicates.hasId(getAuthentication(), #id)")
    @GetMapping(GET_IDENTITY_PARAMETERIZED_MAPPING)
    fun getIdentity(@PathVariable id: Long): Mono<ResponseEntity<Identity>> {
        return this.identityService.readIdentityById(id)
            .map { ResponseEntity.ok(it) }
    }

    @PreAuthorize("permitAll()")
    @PostMapping(POST_IDENTITY_MAPPING)
    fun createIdentity(@RequestBody request: CreateIdentityRequest): Mono<ResponseEntity<Void>> {
        return this.identityService.createIdentity(request.username, request.password)
            .then(this.identityService.readIdentityByUsername(request.username))
            .map { ResponseEntity.created(
                // TODO: write component to automatically prefix Location headers with context path
                UriComponentsBuilder.fromPath(this.contextPath)
                    .path(GET_IDENTITY_PARAMETERIZED_MAPPING)
                    .buildAndExpand(it.getId())
                    .toUri())
                .build()
            }
    }
}
