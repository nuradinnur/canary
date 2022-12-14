package io.renegadelabs.canary.api.identities.exception

import org.springframework.security.core.AuthenticationException

class IdentityAlreadyExistsException(msg: String, cause: Throwable?) : AuthenticationException(msg) {

    constructor(msg: String) : this(msg, null)
}