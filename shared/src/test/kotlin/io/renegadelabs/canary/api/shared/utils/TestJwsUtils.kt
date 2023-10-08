package io.renegadelabs.canary.api.shared.utils

import io.jsonwebtoken.MalformedJwtException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeInstanceOf
import io.renegadelabs.canary.api.shared.util.CustomClaims
import io.renegadelabs.canary.api.shared.util.JwsUtils
import java.util.*

class TestJwsUtils: BehaviorSpec({

    given("a valid JSON web token") {

        val validToken = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJhdXRob3JpdGllcyI6WyJVU0VSIl0sInN1YiI6InRlc3R1c2VyIiwiaXNzIjoiY" +
                "2FuYXJ5IiwiaWF0IjoxNjcwMzgzNDMwLCJleHAiOjQxMDI0NDQ4MTV9.czEBuS81QSaoTRCsr1MKI27wMxhENS3H8IIW1lz3iUevhYuf" +
                "ARA8i0BJ03eEVxL1MMzGOlzal5UfwHJ5q_cSvQ"

        `when`("I want its claims") {
            then("it should be parsed successfully") {
                val result = JwsUtils.toJws(validToken)
                result.shouldNotBeNull()
                result.payload.shouldNotBeNull()
                result.payload.subject.shouldNotBeNull().shouldBe("testuser")
                result.payload.issuer.shouldBe("canary")
                result.payload.issuedAt.shouldBe(Date(1670383430000))
                result.payload.expiration.shouldBe(Date(4102444815000))
                result.payload[CustomClaims.AUTHORITY].shouldBeInstanceOf<Collection<String>>()
            }
        }

        `when`("I want its subject") {
            then("it should be parsed successfully") {
                val result = JwsUtils.toSubject(validToken)
                result.shouldNotBeNull().shouldBeEqualComparingTo("testuser")
            }
        }
    }
    given("an invalid JSON web token") {

        val invalidToken = "Bearer eyJhbGciOizdWIiOiJ0ZXN0dXNlciIs.ImlzcyI6ImNhbmFyeSNDQ0ODE1fQ.IZyQaKuf0Z43ISOsBB0AJhNMe7Og"

        `when`("I want its claims") {
            then("it should throw MalformedJwtException") {

                val exception = shouldThrow<MalformedJwtException> {
                    JwsUtils.toJws(invalidToken)
                }
                exception.shouldHaveMessage("Malformed JWT JSON: {\"alg\":,�X����\u0019\\�\u001D\\�\\��")
            }
        }
        `when`("I want its subject") {
            then("it should throw MalformedJwtException") {

                val exception = shouldThrow<MalformedJwtException> {
                    JwsUtils.toSubject(invalidToken)
                }
                exception.shouldHaveMessage("Malformed JWT JSON: {\"alg\":,�X����\u0019\\�\u001D\\�\\��")
            }
        }
    }
})