package io.renegadelabs.canary.api.shared.support

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.renegadelabs.canary.api.shared.component.JwtAuthenticationReader
import io.renegadelabs.canary.api.shared.domain.Authority
import io.renegadelabs.canary.api.shared.domain.JwtClaim

class JwtParserSupport {

    companion object {
        fun getAuthorities(jwt: Jws<Claims>): Set<Authority> {
            return jwt.payload
                .get(JwtClaim.AUTHORIZATION_DETAILS.getKey(), List::class.java)
                .map { Authority.valueOf(it.toString()) }
                .toSet()
        }

        fun addBearerPrefix(token: String): String {
            return if (!token.startsWith(JwtAuthenticationReader.BEARER_TOKEN_PREFIX)) JwtAuthenticationReader.BEARER_TOKEN_PREFIX_WITH_SEPARATOR.plus(token)
            else token
        }

        fun removeTokenPrefix(bearerToken: String): String {
            return if (bearerToken.startsWith(JwtAuthenticationReader.BEARER_TOKEN_PREFIX_WITH_SEPARATOR))
                bearerToken.substring(JwtAuthenticationReader.BEARER_TOKEN_PREFIX_WITH_SEPARATOR.length)
            else bearerToken
        }

        fun toJws(token: String): Jws<Claims> {
            return Jwts.parser()
                .verifyWith(JwtAuthenticationReader.SIGNING_KEY)
                .clockSkewSeconds(JwtAuthenticationReader.CLOCK_SKEW_TOLERANCE.seconds)
                .build()
                .parseSignedClaims(removeTokenPrefix(token))
        }
    }
}