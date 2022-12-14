package io.renegadelabs.canary.api.identities.service.impl

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.renegadelabs.canary.api.identities.exception.IdentityAlreadyExistsException
import io.renegadelabs.canary.api.identities.exception.IdentityNotFoundException
import io.renegadelabs.canary.api.identities.service.IdentityService
import io.renegadelabs.canary.api.shared.domain.Authorities
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.Duration

@SpringBootTest
class TestIdentityService(
    private val identityService: IdentityService
) : BehaviorSpec({

    beforeSpec {
        Flux.concat(listOf(
            // These identities are immutable over all tests
            identityService.createIdentity("adamsmith103", "pdnZRfG8Fz7Dyygbsf5F"),
            identityService.createIdentity("serendipitous1", "achievebraidoutfield"),
            // This identity is mutated over the course of the tests
            identityService.createIdentity("dancingmonkey", "monkeysarecool1")
        )).subscribe()
    }

    given("a valid set of credentials") {
        `when`("I want to create an identity") {
            then("it should return empty") {

                val publisher = identityService.createIdentity("newuser1", "newPassword1")

                StepVerifier.create(publisher)
                    .expectNextCount(0)
                    .verifyComplete()
                    .shouldBeLessThan(Duration.ofMillis(250))

                val verification = identityService.getIdentityByUsername("newuser1")

                StepVerifier.create(verification)
                    .consumeNextWith { identity ->
                        identity.shouldNotBeNull()
                        identity.getId().shouldNotBeNull().shouldBe(3)
                        identity.username.shouldNotBeNull().shouldBe("newuser1")
                        identity.password.shouldNotBeNull().shouldNotBe("newPassword1")
                        identity.authorities.shouldNotBeNull().shouldBe(setOf(Authorities.USER))
                        identity.isAccountNonExpired.shouldNotBeNull().shouldBe(true)
                        identity.isAccountNonLocked.shouldNotBeNull().shouldBe(true)
                        identity.isCredentialsNonExpired.shouldNotBeNull().shouldBe(true)
                        identity.isEnabled.shouldNotBeNull().shouldBe(true)
                    }
                    .verifyComplete()
                    .shouldBeLessThan(Duration.ofMillis(250))
            }
        }

        `when`("I want to get its identity") {
            then("it should return the identity created") {

                val publisher = identityService.getIdentityByUsername("serendipitous1")

                StepVerifier.create(publisher)
                    .consumeNextWith { identity ->
                        identity.shouldNotBeNull()
                        identity.getId().shouldNotBeNull().shouldBe(1)
                        identity.username.shouldNotBeNull().shouldBe("serendipitous1")
                        identity.password.shouldNotBeNull().shouldNotBe("achievebraidoutfield")
                        identity.authorities.shouldNotBeNull().shouldBe(setOf(Authorities.USER))
                        identity.isAccountNonExpired.shouldNotBeNull().shouldBe(true)
                        identity.isAccountNonLocked.shouldNotBeNull().shouldBe(true)
                        identity.isCredentialsNonExpired.shouldNotBeNull().shouldBe(true)
                        identity.isEnabled.shouldNotBeNull().shouldBe(true)
                    }
                    .verifyComplete()
                    .shouldBeLessThan(Duration.ofMillis(250))
            }
        }
    }

    given("an invalid set of credentials") {
        `when`("I want to create an identity") {
            then("it should throw IdentityAlreadyExistsException") {

                val publisher = identityService.createIdentity("newuser1", "newPassword1")

                StepVerifier.create(publisher)
                    .consumeErrorWith { error ->
                        error.shouldNotBeNull()
                            .shouldBeInstanceOf<IdentityAlreadyExistsException>()
                    }
                    .verify()
                    .shouldBeLessThan(Duration.ofMillis(250))
            }
        }
    }

    given("an identity ID") {
        `when`("I want to get the associated identity") {
            then("it should return empty") {

                val publisher = identityService.getIdentityById(0)

                StepVerifier.create(publisher)
                    .consumeNextWith { identity ->
                        identity.shouldNotBeNull()
                        identity.getId().shouldNotBeNull().shouldBe(0)
                        identity.username.shouldNotBeNull().shouldBe("adamsmith103")
                        identity.password.shouldNotBeNull().shouldNotBe("pdnZRfG8Fz7Dyygbsf5F")
                        identity.authorities.shouldNotBeNull().shouldBe(setOf(Authorities.USER))
                        identity.isAccountNonExpired.shouldNotBeNull().shouldBe(true)
                        identity.isAccountNonLocked.shouldNotBeNull().shouldBe(true)
                        identity.isCredentialsNonExpired.shouldNotBeNull().shouldBe(true)
                        identity.isEnabled.shouldNotBeNull().shouldBe(true)
                    }
                    .verifyComplete()
                    .shouldBeLessThan(Duration.ofMillis(250))
            }
        }
    }

    given("an invalid identity ID") {
        `when`("I want to get the associated identity") {
            then("it should throw IdentityNotFoundException") {

                val publisher = identityService.getIdentityById(Long.MAX_VALUE)

                StepVerifier.create(publisher)
                    .consumeErrorWith { error ->
                        error.shouldNotBeNull()
                            .shouldBeInstanceOf<IdentityNotFoundException>()
                    }
                    .verify()
                    .shouldBeLessThan(Duration.ofMillis(250))
            }
        }
    }
})
