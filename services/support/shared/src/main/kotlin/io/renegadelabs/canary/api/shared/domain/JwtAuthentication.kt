package io.renegadelabs.canary.api.shared.domain

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.renegadelabs.canary.api.shared.support.JwtParserSupport
import io.renegadelabs.canary.api.shared.component.JwtAuthenticationReader
import org.springframework.security.authentication.AbstractAuthenticationToken
import java.util.*

data class JwtAuthentication(
    private val token: String,
    private val authorities: Set<Authority>
): AbstractAuthenticationToken(authorities) {

    constructor(token: String, jwt: Jws<Claims>) : this(token, JwtParserSupport.getAuthorities(jwt))

    init {
        this.details = Jwts.parser()
            .verifyWith(JwtAuthenticationReader.SIGNING_KEY)
            .clockSkewSeconds(JwtAuthenticationReader.CLOCK_SKEW_TOLERANCE.seconds)
            .build()
            .parseSignedClaims(JwtParserSupport.removeTokenPrefix(token))

    }

    fun getJwtHeader(): Header {
        return this.getClaims().header
    }

    fun getJwtPayload(): Claims {
        return this.getClaims().payload
    }

    fun getIssuer(): String? {
        return this.getClaims().payload.issuer
    }

    fun getSubject(): String? {
        return this.getClaims().payload.subject
    }

    fun getAudience(): MutableSet<String>? {
        return this.getClaims().payload.audience
    }

    fun getExpiration(): Date? {
        return this.getClaims().payload.expiration
    }

    fun getNotBefore(): Date? {
        return this.getClaims().payload.notBefore
    }

    fun getIssuedAt(): Date? {
        return this.getClaims().payload.issuedAt
    }

    fun getId(): String? {
        return this.getClaims().payload.id
    }

    fun getSubId(): Long? {
        val rawSubId = this.getClaims().payload[JwtClaim.SUB_ID.getKey()].toString()
        return rawSubId.toLongOrNull()
    }

    fun getAuthorizationDetails(): Set<Authority> {
        return this.authorities
    }

    fun getToken(): String {
        return this.token
    }

    override fun getCredentials(): String {
        return this.token
    }

    override fun getPrincipal(): String {
        return this.getClaims().payload.subject
    }

    // TODO: is it a good habit to suppress warnings?
    @Suppress("UNCHECKED_CAST")
    private fun getClaims(): Jws<Claims> {
        return this.details as Jws<Claims>
    }
}