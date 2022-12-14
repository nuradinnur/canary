package io.renegadelabs.canary.api.identities.exception

import org.springframework.security.core.AuthenticationException

class IdentityNotFoundException(msg: String, cause: Throwable?) : AuthenticationException(msg) {

    constructor(msg: String) : this(msg, null)
}