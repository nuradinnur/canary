package io.renegadelabs.canary.api.identities.service.impl

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import io.renegadelabs.canary.api.identities.service.IdentityPasswordService
import io.renegadelabs.canary.api.identities.service.IdentityService
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.test.StepVerifier
import java.time.Duration

@SpringBootTest
class TestIdentityPasswordService(
    private val passwordEncoder: PasswordEncoder,
    private val identityService: IdentityService,
    private val identityPasswordService: IdentityPasswordService
) : BehaviorSpec({

    beforeSpec {
        // These identities are immutable over all tests
        identityService.createIdentity("adamsmith103", "pdnZRfG8Fz7Dyygbsf5F").subscribe()
        // This identity is mutated over the course of the tests
        identityService.createIdentity("serendipitous1", "achievebraidoutfield").subscribe()
        identityService.createIdentity("dancingmonkey", "monkeysarecool1").subscribe()
    }

    given("a password") {
        and("the password is empty") {
            `when`("I want to update the user's password") {
                then("it should update and return identity") {

                    val publisher = identityPasswordService.updatePassword("serendipitous1", "achievebraidoutfield", "newPassword123")

                    StepVerifier.create(publisher)
                        .consumeNextWith {result ->
                            passwordEncoder.matches("newPassword123", result.password)
                        }
                        .verifyComplete()
                        .shouldBeLessThan(Duration.ofMillis(250))
                }
            }
        }

        and("the password is invalid") {
            `when`("I want to validate the user's password") {
                then("it should throw BadCredentialsException") {

                    val publisher = identityPasswordService.validatePassword("adamsmith103", "invalidPassword123")

                    StepVerifier.create(publisher)
                        .consumeErrorWith { error ->
                            error.shouldNotBeNull()
                                .shouldBeInstanceOf<BadCredentialsException>()
                        }
                        .verify()
                        .shouldBeLessThan(Duration.ofMillis(250))
                }
            }
        }

        and("the password is valid") {
            `when`("I want to validate the user's password") {
                then("it should return empty") {

                    val publisher = identityPasswordService.validatePassword("adamsmith103", "pdnZRfG8Fz7Dyygbsf5F")

                    StepVerifier.create(publisher)
                        .expectNextCount(0)
                        .verifyComplete()
                        .shouldBeLessThan(Duration.ofMillis(250))
                }
            }

            `when`("I want to update the user's password") {
                then("it should update and return identity") {

                    val publisher = identityPasswordService.updatePassword("dancingmonkey", "monkeysarecool1", "newPassword123")

                    StepVerifier.create(publisher)
                        .consumeNextWith {result ->
                            passwordEncoder.matches("newPassword123", result.password)
                        }
                        .verifyComplete()
                        .shouldBeLessThan(Duration.ofMillis(250))
                }
            }
        }
    }
})
