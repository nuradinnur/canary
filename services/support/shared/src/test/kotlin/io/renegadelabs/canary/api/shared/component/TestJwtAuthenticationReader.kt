//package io.renegadelabs.canary.api.shared.component
//
//import io.kotest.core.annotation.Tags
//import io.kotest.core.spec.style.ShouldSpec
//import io.kotest.matchers.nulls.shouldNotBeNull
//import org.springframework.boot.test.context.SpringBootTest
//
//@Tags("canary_services_shared", "spring_test")
//@SpringBootTest(classes = [JwtAuthenticationReader::class])
//class TestJwtAuthenticationReader(
//    private val jwtAuthenticationReader: JwtAuthenticationReader
//): ShouldSpec() {
//
//    init {
//        this.context("spring bean wiring") {
//            should("should work") {
//                jwtAuthenticationReader.shouldNotBeNull()
//            }
//        }
//    }
//}