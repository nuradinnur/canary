package io.renegadelabs.canary.api.shared.domain

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.renegadelabs.canary.api.shared.extensions.getAuthorities
import io.renegadelabs.canary.api.shared.util.JwsUtils
import org.springframework.security.authentication.AbstractAuthenticationToken

data class JwsAuthenticationToken(
    val token: Jws<Claims>
) : AbstractAuthenticationToken(token.getAuthorities()) {

    override fun getCredentials(): String {
        return this.token.signature
    }

    override fun getPrincipal(): String {
        return this.token.body.subject
    }
}