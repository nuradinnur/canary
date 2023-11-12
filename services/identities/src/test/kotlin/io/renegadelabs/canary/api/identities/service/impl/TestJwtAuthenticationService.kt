//package io.renegadelabs.canary.api.identities.service.impl
//
//import io.kotest.core.annotation.Tags
//import io.kotest.core.spec.Order
//import io.kotest.core.spec.style.BehaviorSpec
//import io.kotest.core.test.TestCaseOrder
//import io.renegadelabs.canary.api.identities.service.JwtAuthenticationService
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.test.annotation.DirtiesContext
//
//@Order(2)
//@Tags("identities_service", "unit_test")
//@DirtiesContext
//@SpringBootTest
//class TestJwtAuthenticationService(
//    private val jwtAuthenticationService: JwtAuthenticationService
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
//        }
//    }
//}