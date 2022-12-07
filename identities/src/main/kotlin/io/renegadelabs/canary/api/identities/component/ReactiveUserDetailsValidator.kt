package io.renegadelabs.canary.api.identities.component

import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono

/**
 * @see org.springframework.security.core.userdetails.UserDetailsChecker
 */
interface ReactiveUserDetailsValidator {
    
    fun validate(toCheck: UserDetails): Mono<Void>
}