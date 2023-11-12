//package io.renegadelabs.canary.api.shared.configuration
//
//import io.kotest.core.annotation.Tags
//import io.kotest.core.spec.style.ShouldSpec
//import io.kotest.matchers.nulls.shouldNotBeNull
//import io.renegadelabs.canary.api.shared.component.JwtAuthenticationManager
//import io.renegadelabs.canary.api.shared.component.JwtAuthenticationSecurityContextRepository
//import org.springframework.boot.test.context.SpringBootTest
//
//@Tags("canary_services_shared", "spring_test")
//@SpringBootTest(classes = [
//    WebFluxConfiguration::class,
//    JwtAuthenticationManager::class,
//    JwtAuthenticationSecurityContextRepository::class])
//class TestWebFluxConfiguration(
//    private val webFluxConfiguration: WebFluxConfiguration
//): ShouldSpec() {
//
//    init {
//        this.context("spring bean wiring") {
//            should("should work") {
//                webFluxConfiguration.shouldNotBeNull()
//            }
//        }
//    }
//}