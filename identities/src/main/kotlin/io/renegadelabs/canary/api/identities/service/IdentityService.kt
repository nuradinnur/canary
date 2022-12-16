package io.renegadelabs.canary.api.identities.service

import io.renegadelabs.canary.api.identities.domain.Identity
import org.springframework.context.MessageSourceAware
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import reactor.core.publisher.Mono

/**
 * @see org.springframework.security.core.userdetails.ReactiveUserDetailsService
 */
interface IdentityService: ReactiveUserDetailsService, MessageSourceAware {

    fun createIdentity(username: String, password: String): Mono<Void>

    fun getIdentityById(id: Long): Mono<Identity>

    fun getIdentityByUsername(username: String): Mono<Identity>

    fun updateIdentity(identity: Identity): Mono<Void>
}