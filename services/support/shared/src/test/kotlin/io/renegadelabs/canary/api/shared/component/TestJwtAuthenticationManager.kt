//package io.renegadelabs.canary.api.shared.component
//
//import io.kotest.core.annotation.Tags
//import io.kotest.core.spec.style.BehaviorSpec
//import io.mockk.Runs
//import io.mockk.every
//import io.mockk.just
//import io.mockk.mockk
//import io.renegadelabs.canary.api.shared.test.domain.TestType
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
//import reactor.test.StepVerifier
//
//@Tags(TestType.UNIT_TEST)
//class TestJwtAuthenticationManager : BehaviorSpec() {
//
//    private val jwtAuthenticationReader = mockk<JwtAuthenticationReader>()
//    private val jwtAuthenticationManager = JwtAuthenticationManager(jwtAuthenticationReader)
//
//    init {
//        every { jwtAuthenticationReader.verifyClaims(any()) } just Runs
//
//        this.given("a non-JWT authentication") {
//            val authentication = UsernamePasswordAuthenticationToken("username", "password")
//            `when`("I attempt to authenticate") {
//                then("it should return an empty mono") {
//                    val publisher = jwtAuthenticationManager.authenticate(authentication)
//                    StepVerifier.create(publisher)
//                        .expectNextCount(0)
//                        .verifyComplete()
//                }
//            }
//        }
//        this.given("a JWT authentication") {
////            val authentication = JwtAuthentication()
//        }
//    }
//}
