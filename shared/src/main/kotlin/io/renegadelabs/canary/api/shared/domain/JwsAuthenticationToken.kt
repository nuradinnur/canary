package io.renegadelabs.canary.api.shared.domain

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.renegadelabs.canary.api.shared.util.extensions.getAuthorities
import org.springframework.security.authentication.AbstractAuthenticationToken

data class JwsAuthenticationToken(
    val token: Jws<Claims>
) : AbstractAuthenticationToken(token.getAuthorities()) {

    override fun getCredentials(): String {
        return String(this.token.digest)
    }

    override fun getPrincipal(): String {
        return this.token.payload.subject
    }
}