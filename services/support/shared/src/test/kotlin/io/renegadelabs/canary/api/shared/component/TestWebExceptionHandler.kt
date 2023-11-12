//package io.renegadelabs.canary.api.shared.component
//
//import io.kotest.core.annotation.Tags
//import io.kotest.core.spec.style.ShouldSpec
//import io.kotest.matchers.nulls.shouldNotBeNull
//import io.renegadelabs.canary.api.shared.configuration.WebFluxConfiguration
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.web.reactive.error.ErrorAttributes
//import org.springframework.context.ApplicationContext
//import org.springframework.http.codec.ServerCodecConfigurer
//import org.springframework.test.context.ContextConfiguration
//
//@Tags("canary_services_shared", "spring_test")
//@ContextConfiguration()
//@SpringBootTest(classes = [
//    WebExceptionHandler::class,
//    ErrorAttributes::class,
//    ApplicationContext::class,
//    ServerCodecConfigurer::class,
//    WebFluxConfiguration::class])
//class TestWebExceptionHandler(
//    private val webExceptionHandler: WebExceptionHandler
//): ShouldSpec() {
//
//    init {
//        this.context("spring bean wiring") {
//            should("should work") {
//                webExceptionHandler.shouldNotBeNull()
//            }
//        }
//    }
//}