package io.renegadelabs.canary.api.identities.controller

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.string.shouldNotBeBlank
import io.renegadelabs.canary.api.identities.domain.TokenPair
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestTokenIssuanceController(
    private val webTestClient: WebTestClient
): FeatureSpec({

//    beforeSpec {
//
//        // Create a new identity
//        webTestClient.post()
//            .uri("")
//            .contentType(MediaType.APPLICATION_JSON)
//            .body(BodyInserters.fromValue(mapOf(
//                Pair("username", "testuser1"),
//                Pair("password", "testpassword123")
//            )))
//            .exchange()
//            .expectStatus()
//            .isCreated
//            .expectHeader()
//            .location("/identities/0")
//    }

    feature("Creating a new token pair") {
        scenario("should return HTTP 200") {

            webTestClient.post()
                .uri("/sessions")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(mapOf(
                    Pair("username", "testuser1"),
                    Pair("password", "testpassword123")
                )))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(TokenPair::class.java)
                .value {
                    it.accessToken.shouldNotBeBlank()
                    it.refreshToken.shouldNotBeBlank()
                }
        }
    }

    feature("Refreshing an access token") {
        scenario("should return HTTP 200") {

            val tokenPair = webTestClient.post()
                .uri("/sessions")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(mapOf(
                    Pair("username", "testuser1"),
                    Pair("password", "testpassword123")
                )))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(TokenPair::class.java)
                .returnResult()
                .responseBody

             webTestClient.get()
                .uri("/sessions/refresh")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenPair!!.refreshToken)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(TokenPair::class.java)
                .value {
                    // TODO: fix .shouldBeEqual() -> .shouldNotBeEqual()
                    it.accessToken.shouldNotBeBlank()!!
                        .shouldBeEqual(tokenPair.accessToken)
                    it.refreshToken.shouldNotBeBlank()!!
                        .shouldBeEqual(tokenPair.refreshToken)
                }
        }
    }
})
