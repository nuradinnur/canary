package io.renegadelabs.canary.api.identities.exception

import org.springframework.security.authentication.BadCredentialsException

/**
 * Thrown if an identity already exists in a context where it is expected that the identity does not exist.
 *
 * @version 1.0.0
 * @param   message     A descriptive message
 * @param   cause       The cause of the exception
 * @since   1.0.0
 */
class IdentityAlreadyExistsException(message: String, cause: Throwable? = null) : BadCredentialsException(message)