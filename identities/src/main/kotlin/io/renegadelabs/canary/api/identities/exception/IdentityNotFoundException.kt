package io.renegadelabs.canary.api.identities.exception

import org.springframework.security.core.AuthenticationException

/**
 * Thrown if an identity does not exist in a context where it is expected that the identity does exist.
 *
 * @version 1.0.0
 * @param   message     A descriptive message
 * @param   cause       The cause of the exception
 * @since   1.0.0
 */
class IdentityNotFoundException(message: String, cause: Throwable?) : AuthenticationException(message) {

    /**
     * Constructs a <code>IdentityNotFoundException</code> with the specified message.
     *
     * @version 1.0.0
     * @param   message     A descriptive message
     * @since   1.0.0
     */
    constructor(message: String) : this(message, null)
}