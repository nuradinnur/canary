package io.renegadelabs.canary.api.shared.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.time.Duration

class JsonWebTokenUtils {

    companion object {
        const val BEARER_TOKEN_PREFIX_WITH_SEPARATOR: String = "Bearer "
        const val BEARER_TOKEN_PREFIX: String = "Bearer"

        val SIGNING_KEY: Key = Keys.hmacShaKeyFor("TElDeWs9ME0xdCMtV05WT2J4d0FAIyMxTVBtUHJLPy56ezx6dU1Uez98NU5HeDUkRGY8LFhWUVc0OzN6anQ0".toByteArray())
        val CLOCK_SKEW_TOLERANCE: Duration = Duration.ofSeconds(15)

        fun toClaims(jsonWebToken: String): Claims {
            return Jwts.parserBuilder().setSigningKey(this.SIGNING_KEY).build()
                .parseClaimsJws(stripTokenPrefix(jsonWebToken)).body
        }

        private fun stripTokenPrefix(jsonWebToken: String): String {
            return if (jsonWebToken.startsWith(this.BEARER_TOKEN_PREFIX_WITH_SEPARATOR))
                jsonWebToken.substring(this.BEARER_TOKEN_PREFIX_WITH_SEPARATOR.length)
            else jsonWebToken
        }
    }
}