//package io.renegadelabs.canary.api.identities.service.impl
//
//import io.kotest.core.annotation.Tags
//import io.kotest.core.spec.Order
//import io.kotest.core.spec.style.BehaviorSpec
//import io.kotest.core.test.TestCaseOrder
//import io.kotest.matchers.types.shouldBeInstanceOf
//import io.renegadelabs.canary.api.identities.service.IdentityPasswordService
//import io.renegadelabs.canary.api.identities.service.IdentityService
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.security.authentication.BadCredentialsException
//import org.springframework.security.crypto.password.PasswordEncoder
//import org.springframework.test.annotation.DirtiesContext
//import reactor.test.StepVerifier
//
//// TODO: remove annotation on all tests when repository operations are mocked
//@Order(0)
//@Tags("identities_service", "unit_test")
//// TODO: remove annotation on all tests when repository operations are mocked
//@DirtiesContext
//@SpringBootTest
//class TestIdentityPasswordService(
//    private val passwordEncoder: PasswordEncoder,
//    private val identityService: IdentityService,
//    private val identityPasswordService: IdentityPasswordService
//) : BehaviorSpec() {
//
//    companion object {
//        const val TEST_USERNAME = "testuser1"
//        const val TEST_PASSWORD = "testpassword123"
//        const val NEW_TEST_PASSWORD = "newtestpassword123"
//        const val INVALID_TEST_PASSWORD = "invalidtestpassword123"
//    }
//
//    override fun testCaseOrder() = TestCaseOrder.Sequential
//    override fun timeout() = 5_000L
//
//    init {
//        this.given("an identity's password") {
//            StepVerifier.create(identityService
//                .createIdentity(TEST_USERNAME, TEST_PASSWORD))
//                .expectNextCount(0)
//                .verifyComplete()
//
//            and("it is valid") {
//                `when`("I want to validate the identity's password") {
//                    then("it should validate the password") {
//                        StepVerifier.create(identityPasswordService
//                            .validatePassword(TEST_USERNAME, TEST_PASSWORD))
//                            .expectNextCount(0)
//                            .verifyComplete()
//                    }
//                }
//
//                `when`("I want to change the identity's password") {
//                    then("it should change the password") {
//                        StepVerifier.create(
//                            with(identityPasswordService) {
//                                updatePassword(TEST_USERNAME, TEST_PASSWORD, NEW_TEST_PASSWORD)
//                                    // TODO: remove when repository operations can be mocked and verified
//                                    .then(validatePassword(TEST_USERNAME, NEW_TEST_PASSWORD))
//                            })
//                            .expectNextCount(0)
//                            .verifyComplete()
//                    }
//                }
//            }
//
//            and("it is invalid") {
//                `when`("I want to validate the identity's password") {
//                    then("it should throw BadCredentialsException") {
//                        StepVerifier.create(identityPasswordService
//                            .validatePassword(TEST_USERNAME, INVALID_TEST_PASSWORD))
//                            .expectNextCount(0)
//                            .consumeErrorWith { exception -> exception.shouldBeInstanceOf<BadCredentialsException>() }
//                            .verify()
//                    }
//                }
//                `when`("I want to change the identity's password") {
//                    then("it should throw BadCredentialsException") {
//                        StepVerifier.create(
//                            with(identityPasswordService) {
//                                updatePassword(TEST_USERNAME, INVALID_TEST_PASSWORD, NEW_TEST_PASSWORD)
//                                    .then(validatePassword(TEST_USERNAME, NEW_TEST_PASSWORD))
//                            })
//                            .expectNextCount(0)
//                            .consumeErrorWith { exception -> exception.shouldBeInstanceOf<BadCredentialsException>() }
//                            .verify()
//                    }
//                }
//            }
//        }
//    }
//}
