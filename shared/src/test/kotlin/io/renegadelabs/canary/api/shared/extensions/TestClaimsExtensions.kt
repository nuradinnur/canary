package io.renegadelabs.canary.api.shared.extensions

import io.jsonwebtoken.Claims
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import io.renegadelabs.canary.api.shared.domain.Authority
import io.renegadelabs.canary.api.shared.util.JsonWebTokenUtils

class TestClaimsExtensions: BehaviorSpec({

    val jsonWebToken = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJhdXRob3JpdGllcyI6WyJVU0VSIl0sInN1YiI6InRlc3R1c2VyIiwiaXNzIjoiY" +
            "2FuYXJ5IiwiaWF0IjoxNjcwMzgzNDMwLCJleHAiOjQxMDI0NDQ4MTV9.czEBuS81QSaoTRCsr1MKI27wMxhENS3H8IIW1lz3iUevhYuf" +
            "ARA8i0BJ03eEVxL1MMzGOlzal5UfwHJ5q_cSvQ"
    val validClaims: Claims = JsonWebTokenUtils.toClaims(jsonWebToken)

    given("valid claims") {
        `when`("I want its authorities") {
            then("it should be parsed successfully") {
                val result = validClaims.getAuthorities()
                result.shouldNotBeNull()
                result.shouldBeInstanceOf<Collection<String>>()
                result.shouldContain(Authority.USER)
            }
        }
    }
})
