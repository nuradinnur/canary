//package io.renegadelabs.canary.api.identities.controller
//
//import io.kotest.core.annotation.Tags
//import io.kotest.core.spec.Order
//import io.kotest.core.spec.style.FeatureSpec
//import io.kotest.core.test.TestCaseOrder
//import io.kotest.matchers.equals.shouldBeEqual
//import io.kotest.matchers.equals.shouldNotBeEqual
//import io.renegadelabs.canary.api.identities.controller.request.CreateSessionRequest
//import io.renegadelabs.canary.api.identities.domain.TokenPair
//import io.renegadelabs.canary.api.identities.service.IdentityService
//import io.renegadelabs.canary.api.identities.service.JwtAuthenticationService
//import io.renegadelabs.canary.api.shared.component.JwtAuthenticationReader
//import io.renegadelabs.canary.api.shared.test.configuration.WebTestClientConfiguration
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.http.HttpHeaders
//import org.springframework.test.annotation.DirtiesContext
//import org.springframework.test.context.ContextConfiguration
//import org.springframework.test.web.reactive.server.WebTestClient
//import org.springframework.test.web.reactive.server.expectBody
//import org.springframework.web.reactive.function.BodyInserters
//import reactor.core.publisher.Mono
//import reactor.test.StepVerifier
//import java.time.Duration
//
//@Order(4)
//@Tags("identities_service", "integration_test")
//@DirtiesContext
//@ContextConfiguration(classes = [WebTestClientConfiguration::class])
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class TestJwtAuthenticationController(
//    private val webTestClient: WebTestClient,
//    private val identityService: IdentityService,
//    private val jwtAuthenticationService: JwtAuthenticationService,
//    private val jwtAuthenticationReader: JwtAuthenticationReader
//): FeatureSpec() {
//
//    companion object {
//        const val BASE_URL = TokenIssuanceController.BASE_PATH
//        const val TEST_USERNAME = "testuser1"
//        const val TEST_PASSWORD = "testpassword123"
//        const val INVALID_TEST_PASSWORD = "invalidtestpassword123"
//    }
//
//    lateinit var tokenPair: TokenPair
//
//    override fun testCaseOrder() = TestCaseOrder.Sequential
//    override fun timeout() = 5_000L
//
//    init {
//        beforeAny {
//            // TODO: remove once a database connection is implemented - prepopulate with a resources/data.sql
//            StepVerifier.create(this.identityService.existsIdentityByUsername(TEST_USERNAME)
//                .filter { exists -> !exists }
//                .flatMap { this.identityService.createIdentity(TEST_USERNAME, TEST_PASSWORD) }
//                .then(this.jwtAuthenticationService.createJwtAuthentication(TEST_USERNAME, TEST_PASSWORD)))
//                .consumeNextWith {
//                    // TODO: find a cleaner way to delay time during tests; kotlinx-coroutines-test
//                    Mono.delay(Duration.ofSeconds(1)).block()
//                    this.tokenPair = it
//                }
//                .verifyComplete()
//        }
//
//        this.feature("creating a token pair") {
//            scenario("works because user has successfully authenticated") {
//                webTestClient
//                    .post()
//                    .uri(BASE_URL.plus(TokenIssuanceController.POST_SESSION_REQUEST_MAPPING))
//                    .body(BodyInserters.fromValue(CreateSessionRequest(TEST_USERNAME, TEST_PASSWORD)))
//                    .exchange()
//                    .expectAll(
//                        { response -> response.expectStatus().isOk },
//                        { response -> response.expectBody<TokenPair>().returnResult() }
//                    )
//            }
//
//            scenario("fails because user has invalid credentials") {
//                webTestClient
//                    .post()
//                    .uri(BASE_URL.plus(TokenIssuanceController.POST_SESSION_REQUEST_MAPPING))
//                    .body(BodyInserters.fromValue(mapOf(
//                        Pair("username", TEST_USERNAME), Pair("password", INVALID_TEST_PASSWORD))
//                    ))
//                    .exchange()
//                    .expectAll(
//                        { response -> response.expectStatus().isBadRequest },
//                        { response -> response.expectBody<Map<String, Any>>() }
//                    )
//            }
//        }
//
//        this.feature("refreshing an access token") {
//            scenario("works because refresh token is sufficiently authenticated") {
//                webTestClient
//                    .get()
//                    .uri(BASE_URL.plus(TokenIssuanceController.GET_SESSION_REFRESH_REQUEST_MAPPING))
//                    .header(HttpHeaders.AUTHORIZATION, jwtAuthenticationReader.addBearerPrefix(tokenPair.refreshToken))
//                    .exchange()
//                    .expectAll(
//                        { response -> response.expectStatus().isOk },
//                        { response -> response.expectBody<TokenPair>().value {
//                            it.accessToken.shouldNotBeEqual(tokenPair.accessToken)
//                            it.refreshToken.shouldBeEqual(tokenPair.refreshToken)
//                        }}
//                    )
//            }
//
//            scenario("fails because refresh token is not authenticated") {
//                webTestClient
//                    .get()
//                    .uri(BASE_URL.plus(TokenIssuanceController.GET_SESSION_REFRESH_REQUEST_MAPPING))
//                    .header(HttpHeaders.AUTHORIZATION, jwtAuthenticationReader.addBearerPrefix(tokenPair.accessToken))
//                    .exchange()
//                    .expectAll(
//                        { response -> response.expectStatus().isForbidden },
//                        { response -> response.expectBody<Map<String, Any>>() }
//                    )
//            }
//        }
//    }
//}
