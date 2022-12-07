package io.renegadelabs.canary.api.identities.service

import org.springframework.context.MessageSourceAware
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService
import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono

/**
 * @see org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService
 */
interface PasswordService: ReactiveUserDetailsPasswordService, MessageSourceAware {

    fun validatePassword(userDetails: UserDetails, password: String): Mono<Void>
}
