//package io.renegadelabs.canary.api.identities.service.impl
//
//import io.kotest.core.annotation.Tags
//import io.kotest.core.spec.Order
//import io.kotest.core.spec.style.BehaviorSpec
//import io.kotest.core.test.TestCaseOrder
//import io.kotest.matchers.nulls.shouldNotBeNull
//import io.kotest.matchers.shouldBe
//import io.kotest.matchers.shouldNotBe
//import io.kotest.matchers.types.shouldBeInstanceOf
//import io.renegadelabs.canary.api.identities.exception.IdentityAlreadyExistsException
//import io.renegadelabs.canary.api.identities.exception.IdentityNotFoundException
//import io.renegadelabs.canary.api.identities.service.IdentityService
//import io.renegadelabs.canary.api.shared.domain.Authority
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.test.annotation.DirtiesContext
//import reactor.test.StepVerifier
//
//@Order(1)
//@Tags("identities_service", "unit_test")
//@DirtiesContext
//@SpringBootTest
//class TestIdentityService(
//    private val identityService: IdentityService
//) : BehaviorSpec() {
//
//    companion object {
//        const val TEST_USERNAME = "testuser1"
//        const val TEST_PASSWORD = "testpassword123"
//    }
//
//    override fun testCaseOrder() = TestCaseOrder.Sequential
//    override fun timeout() = 5_000L
//
//    init {
//        this.given("a valid set of credentials") {
//            `when`("I want to create an identity") {
//                then("it should be successful") {
//                    StepVerifier.create(
//                        identityService.createIdentity(TEST_USERNAME, TEST_PASSWORD).then(
//                            // TODO: remove when database connection is implemented - mock and verify repository operations
//                            identityService.readIdentityByUsername(TEST_USERNAME)))
//                        .consumeNextWith { identity ->
//                            identity.shouldNotBeNull()
//                            identity.getId().shouldBe(1)
//                            identity.username.shouldBe(TEST_USERNAME)
//                            identity.password.shouldNotBe(TEST_PASSWORD)
//                            identity.authorities.shouldBe(setOf(Authority.USER))
//                            identity.isAccountExpired().shouldBe(false)
//                            identity.isAccountLocked().shouldBe(false)
//                            identity.hasExpiredCredentials().shouldBe(false)
//                            identity.isEnabled.shouldBe(true)
//                        }
//                        .verifyComplete()
//                }
//            }
//
//            `when`("I want to get its identity") {
//                then("it should be successful") {
//                    StepVerifier.create(
//                        identityService.readIdentityByUsername(TEST_USERNAME))
//                        .consumeNextWith { identity ->
//                            identity.shouldNotBeNull()
//                            identity.getId().shouldBe(1)
//                            identity.username.shouldBe(TEST_USERNAME)
//                            identity.password.shouldNotBe(TEST_PASSWORD)
//                            identity.authorities.shouldBe(setOf(Authority.USER))
//                            identity.isAccountExpired().shouldBe(false)
//                            identity.isAccountLocked().shouldBe(false)
//                            identity.hasExpiredCredentials().shouldBe(false)
//                            identity.isEnabled.shouldBe(true)
//                        }
//                        .verifyComplete()
//                }
//            }
//        }
//
//        this.given("an invalid set of credentials") {
//            `when`("I enter an existing username") {
//                then("it should throw IdentityAlreadyExistsException") {
//                    StepVerifier.create(identityService.createIdentity(TEST_USERNAME, TEST_PASSWORD))
//                        .consumeErrorWith { error -> error
//                            .shouldNotBeNull()
//                            .shouldBeInstanceOf<IdentityAlreadyExistsException>()
//                        }
//                        .verify()
//                }
//            }
//        }
//
//        this.given("an ID belonging to an identity") {
//            `when`("I want to get the associated identity") {
//                then("it should be successful") {
//                    StepVerifier.create(
//                        identityService.readIdentityById(1))
//                        .consumeNextWith { identity ->
//                            identity.shouldNotBeNull()
//                            identity.getId().shouldBe(1)
//                            identity.username.shouldBe(TEST_USERNAME)
//                            identity.password.shouldNotBe(TEST_PASSWORD)
//                            identity.authorities.shouldBe(setOf(Authority.USER))
//                            identity.hasExpiredCredentials().shouldBe(false)
//                            identity.isAccountLocked().shouldBe(false)
//                            identity.hasExpiredCredentials().shouldBe(false)
//                            identity.isEnabled.shouldBe(true)
//                        }
//                        .verifyComplete()
//                }
//            }
//        }
//
//        this.given("an ID not belonging to any identity") {
//            `when`("I want to get the associated identity") {
//                then("it should throw IdentityNotFoundException") {
//                    StepVerifier.create(
//                        identityService.readIdentityById(Long.MAX_VALUE))
//                        .consumeErrorWith { error -> error
//                            .shouldNotBeNull()
//                            .shouldBeInstanceOf<IdentityNotFoundException>()
//                        }
//                        .verify()
//                }
//            }
//        }
//    }
//}
