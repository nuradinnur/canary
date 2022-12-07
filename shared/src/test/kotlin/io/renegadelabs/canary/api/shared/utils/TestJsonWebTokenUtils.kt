package io.renegadelabs.canary.api.shared.utils

import io.jsonwebtoken.MalformedJwtException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeInstanceOf
import io.renegadelabs.canary.api.shared.util.JsonWebTokenUtils
import java.util.*

class TestJsonWebTokenUtils: BehaviorSpec({

    val validJsonWebToken = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJhdXRob3JpdGllcyI6WyJVU0VSIl0sInN1YiI6InRlc3R1c2VyIiwiaXNzIjoiY" +
            "2FuYXJ5IiwiaWF0IjoxNjcwMzgzNDMwLCJleHAiOjQxMDI0NDQ4MTV9.czEBuS81QSaoTRCsr1MKI27wMxhENS3H8IIW1lz3iUevhYuf" +
            "ARA8i0BJ03eEVxL1MMzGOlzal5UfwHJ5q_cSvQ"
    val invalidJsonWebToken = "Bearer eyJhbGciOizdWIiOiJ0ZXN0dXNlciIs.ImlzcyI6ImNhbmFyeSNDQ0ODE1fQ.IZyQaKuf0Z43ISOsBB0AJhNMe7Og"

    given("a valid JSON web token") {
        `when`("I want its claims") {
            then("it should be parsed successfully") {
                val result = JsonWebTokenUtils.toClaims(validJsonWebToken)
                println(result)
                result.shouldNotBeNull()
                result.subject.shouldNotBeNull()
                result.subject.shouldBe("testuser")
                result.issuer.shouldBe("canary")
                result.issuedAt.shouldBe(Date(1670383430000))
                result.expiration.shouldBe(Date(4102444815000))
                result["authorities"].shouldBeInstanceOf<Collection<String>>()
            }
        }
    }
    given("an invalid JSON web token") {
        `when`("I want its claims") {
            then("it should throw an exception") {
                val exception = shouldThrow<MalformedJwtException> {
                    JsonWebTokenUtils.toClaims(invalidJsonWebToken)
                }
                exception.shouldHaveMessage("Malformed JWT JSON: {\"alg\":,�X����\u0019\\�\u001D\\�\\��")
            }
        }
    }
})