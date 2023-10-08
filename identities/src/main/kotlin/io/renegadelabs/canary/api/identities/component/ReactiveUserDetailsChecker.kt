package io.renegadelabs.canary.api.identities.component

import org.springframework.context.MessageSourceAware
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsChecker
import reactor.core.publisher.Mono

/**
 * Defines operations allowing validation of an identity.
 *
 * @version 1.0.0
 * @see     UserDetailsChecker
 * @since   1.0.0
 */
interface ReactiveUserDetailsChecker: MessageSourceAware {

    /**
     * Verifies a {@link UserDetails} object is valid and able to be authenticated against.
     *
     * @version 1.0.0
     * @param   userDetails    The userDetails to verify
     * @since   1.0.0
     */
    fun validate(userDetails: UserDetails): Mono<Void>
}