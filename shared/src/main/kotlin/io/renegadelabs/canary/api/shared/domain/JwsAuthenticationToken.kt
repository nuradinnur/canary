package io.renegadelabs.canary.api.shared.domain

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.renegadelabs.canary.api.shared.extensions.getAuthorities
import io.renegadelabs.canary.api.shared.util.JwsUtils
import org.springframework.security.authentication.AbstractAuthenticationToken

data class JwsAuthenticationToken(
    private val token: Jws<Claims>
) : AbstractAuthenticationToken(token.getAuthorities()) {

    constructor(token: String) : this(JwsUtils.toJws(token))

    override fun getCredentials(): String {
        return token.signature
    }

    override fun getPrincipal(): String {
        return token.body.subject
    }
}