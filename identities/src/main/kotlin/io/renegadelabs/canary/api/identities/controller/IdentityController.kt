package io.renegadelabs.canary.api.identities.controller

import io.renegadelabs.canary.api.identities.controller.request.CreateIdentityRequest
import io.renegadelabs.canary.api.identities.domain.Identity
import io.renegadelabs.canary.api.identities.service.IdentityService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@RestController
class IdentityController(
    private val identityService: IdentityService,
    @Value("\${spring.webflux.base-path}") private val contextPath: String
) {

    companion object {
        const val GET_IDENTITY_REQUEST_MAPPING: String = ""
        const val GET_IDENTITY_REQUEST_MAPPING_PARAMETERIZED: String = "/{id}"
        const val POST_IDENTITY_REQUEST_MAPPING: String = ""
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(GET_IDENTITY_REQUEST_MAPPING)
    fun getIdentity(@AuthenticationPrincipal subject: String): Mono<ResponseEntity<Identity>> {
        return this.identityService.getIdentityByUsername(subject)
            .map { ResponseEntity.ok(it) }
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(GET_IDENTITY_REQUEST_MAPPING_PARAMETERIZED)
    fun getIdentity(@PathVariable id: Long): Mono<ResponseEntity<Identity>> {
        return this.identityService.getIdentityById(id)
            .map { ResponseEntity.ok(it) }
    }

    @PreAuthorize("permitAll()")
    @PostMapping(POST_IDENTITY_REQUEST_MAPPING)
    fun createIdentity(@RequestBody request: CreateIdentityRequest): Mono<ResponseEntity<Void>> {
        return this.identityService.createIdentity(request.username, request.password)
            .then(this.identityService.getIdentityByUsername(request.username))
            .map {
                ResponseEntity.created(UriComponentsBuilder.fromPath(this.contextPath)
                    .path(GET_IDENTITY_REQUEST_MAPPING_PARAMETERIZED)
                    .buildAndExpand(it.getId())
                    .toUri()
                ).build()
            }
    }
}
