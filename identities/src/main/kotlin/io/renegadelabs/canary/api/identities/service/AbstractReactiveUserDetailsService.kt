package io.renegadelabs.canary.api.identities.service

import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import reactor.core.publisher.Mono

/**
 * @see org.springframework.security.core.userdetails.ReactiveUserDetailsService
 */
abstract class AbstractReactiveUserDetailsService: ReactiveUserDetailsService {

    abstract fun validateCredentials(username: String, password: String): Mono<Void>
}