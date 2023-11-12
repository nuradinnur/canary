//package io.renegadelabs.canary.api.shared.component
//
//import io.kotest.core.annotation.Tags
//import io.kotest.core.spec.style.ShouldSpec
//import io.kotest.matchers.nulls.shouldNotBeNull
//import org.springframework.boot.test.context.SpringBootTest
//
//@Tags("canary_services_shared", "spring_test")
//@SpringBootTest(classes = [WebFluxErrorAttributes::class])
//class TestWebFluxErrorAttributes(
//    private val webFluxErrorAttributes: WebFluxErrorAttributes
//): ShouldSpec() {
//
//    init {
//        this.context("spring bean wiring") {
//            should("should work") {
//                webFluxErrorAttributes.shouldNotBeNull()
//            }
//        }
//    }
//}