package io.renegadelabs.canary.api.identities.component

import io.renegadelabs.canary.api.identities.domain.Identity
import org.springframework.context.MessageSourceAware
import org.springframework.security.core.userdetails.UserDetailsChecker
import reactor.core.publisher.Mono

/**
 * Defines operations allowing validation of an identity.
 *
 * @version 1.0.0
 * @see     UserDetailsChecker
 * @since   1.0.0
 */
interface ReactiveIdentityVerifier: MessageSourceAware {

    /**
     * Verifies a {@link Identity} object is valid and able to be authenticated against. See
     * {@link UserDetailsChecker#check} for corresponding blocking interface method.
     *
     * @version 1.0.0
     * @param   identity    The identity to verify
     * @since   1.0.0
     */
    fun verifyIdentity(identity: Identity): Mono<Void>
}