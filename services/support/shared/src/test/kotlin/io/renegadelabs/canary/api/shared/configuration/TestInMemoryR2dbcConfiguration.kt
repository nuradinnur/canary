//package io.renegadelabs.canary.api.shared.configuration
//
//import io.kotest.core.annotation.Tags
//import io.kotest.core.spec.style.ShouldSpec
//import io.kotest.matchers.nulls.shouldNotBeNull
//import org.springframework.boot.test.context.SpringBootTest
//
//@Tags("canary_services_shared", "spring_test")
//@SpringBootTest(classes = [InMemoryR2dbcConfiguration::class])
//class TestInMemoryR2dbcConfiguration(
//    private val inMemoryR2dbcConfiguration: InMemoryR2dbcConfiguration
//): ShouldSpec() {
//
//    init {
//        this.context("spring bean wiring") {
//            should("should work") {
//                inMemoryR2dbcConfiguration.shouldNotBeNull()
//            }
//        }
//    }
//}