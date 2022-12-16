package io.renegadelabs.canary.api.shared.extensions

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import io.renegadelabs.canary.api.shared.domain.Authorities
import io.renegadelabs.canary.api.shared.util.JwsUtils

class TestJwsExtensions: BehaviorSpec({

    given("valid claims") {

        val token = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJhdXRob3JpdGllcyI6WyJVU0VSIl0sInN1YiI6InRlc3R1c2VyIiwiaXNzIjoiY" +
                "2FuYXJ5IiwiaWF0IjoxNjcwMzgzNDMwLCJleHAiOjQxMDI0NDQ4MTV9.czEBuS81QSaoTRCsr1MKI27wMxhENS3H8IIW1lz3iUevhYuf" +
                "ARA8i0BJ03eEVxL1MMzGOlzal5UfwHJ5q_cSvQ"
        val validJws: Jws<Claims> = JwsUtils.toJws(token)

        `when`("I want its authorities") {
            then("it should be parsed successfully") {
                val result = validJws.getAuthorities()
                result.shouldNotBeNull()
                    .shouldBeInstanceOf<Collection<String>>()
                    .shouldContain(Authorities.USER)
            }
        }

        `when`("I want to know if it's expired") {
            then("it should be false") {
                validJws.isExpired()
                    .shouldNotBeNull()
                    .shouldBeFalse()
            }
        }

        `when`("I want to know if it is a valid access token") {
            then("it should be true") {
                validJws.hasValidSessionClaims()
                    .shouldNotBeNull()
                    .shouldBeTrue()
            }
        }

        `when`("I want to know if it is a valid refresh token") {
            then("it should be true") {
                validJws.hasValidRefreshClaims()
                    .shouldNotBeNull()
                    .shouldBeFalse()
            }
        }
    }
})
