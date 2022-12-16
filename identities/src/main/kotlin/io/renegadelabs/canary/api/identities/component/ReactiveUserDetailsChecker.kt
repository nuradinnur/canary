package io.renegadelabs.canary.api.identities.component

import org.springframework.context.MessageSourceAware
import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono

/**
 * @see org.springframework.security.core.userdetails.UserDetailsChecker
 */
interface ReactiveUserDetailsChecker: MessageSourceAware {
    
    fun validate(userDetails: UserDetails): Mono<Void>
}