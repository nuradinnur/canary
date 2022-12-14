package io.renegadelabs.canary.api.identities.service

import io.renegadelabs.canary.api.identities.domain.Identity
import org.springframework.context.MessageSourceAware
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService
import reactor.core.publisher.Mono

/**
 * @see org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService
 */
interface IdentityPasswordService: ReactiveUserDetailsPasswordService, MessageSourceAware {

    fun validatePassword(username: String, password: String): Mono<Void>

    fun updatePassword(username: String, password: String, newPassword: String): Mono<Identity>
}
