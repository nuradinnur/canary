package io.renegadelabs.canary.api.shared.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.time.Duration

class JwsUtils {

    companion object {
        const val BEARER_TOKEN_PREFIX: String = "Bearer"
        const val BEARER_TOKEN_PREFIX_WITH_SEPARATOR: String = BEARER_TOKEN_PREFIX.plus(" ")

        val SIGNING_KEY: Key = Keys.hmacShaKeyFor("TElDeWs9ME0xdCMtV05WT2J4d0FAIyMxTVBtUHJLPy56ezx6dU1Uez98NU5HeDUkRGY8LFhWUVc0OzN6anQ0".toByteArray())
        val CLOCK_SKEW_TOLERANCE: Duration = Duration.ofSeconds(15)

        fun toJws(token: String): Jws<Claims> {
            return Jwts.parserBuilder().setSigningKey(this.SIGNING_KEY).build()
                .parseClaimsJws(stripTokenPrefix(token))
        }

        fun toSubject(token: String): String {
            return this.toJws(token).body.subject.toString()
        }

        private fun stripTokenPrefix(jsonWebToken: String): String {
            return if (jsonWebToken.startsWith(this.BEARER_TOKEN_PREFIX_WITH_SEPARATOR))
                jsonWebToken.substring(this.BEARER_TOKEN_PREFIX_WITH_SEPARATOR.length)
            else jsonWebToken
        }
    }
}