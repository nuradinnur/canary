package io.renegadelabs.canary.api.identities.service

import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService
import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono

/**
 * @see org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService
 */
abstract class AbstractReactiveUserDetailsPasswordService: ReactiveUserDetailsPasswordService {

    abstract fun validatePassword(userDetails: UserDetails, password: String): Mono<Void>
}
