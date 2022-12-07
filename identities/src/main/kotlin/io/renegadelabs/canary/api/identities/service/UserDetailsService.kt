package io.renegadelabs.canary.api.identities.service

import org.springframework.context.MessageSourceAware
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import reactor.core.publisher.Mono

/**
 * @see org.springframework.security.core.userdetails.ReactiveUserDetailsService
 */
interface UserDetailsService: ReactiveUserDetailsService, MessageSourceAware {

    fun validateCredentials(username: String, password: String): Mono<Void>
}