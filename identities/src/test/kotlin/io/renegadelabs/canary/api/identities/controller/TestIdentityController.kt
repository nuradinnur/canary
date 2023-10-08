package io.renegadelabs.canary.api.identities.controller

import io.kotest.core.spec.style.FeatureSpec
import io.renegadelabs.canary.api.identities.domain.TokenPair
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestIdentityController(
    private val webTestClient: WebTestClient
): FeatureSpec({

    lateinit var token: TokenPair

    beforeSpec {

        // Create a new identity
        webTestClient.post()
            .uri("")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(mapOf(
                Pair("username", "testuser1"),
                Pair("password", "testpassword123")
            )))
            .exchange()
            .expectStatus()
            .isCreated
            .expectHeader()
            .location("/identities/0")

        // Create a new session for identity
        token = webTestClient.post()
            .uri("/sessions")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(mapOf(
                Pair("username", "testuser1"),
                Pair("password", "testpassword123")
            )))
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(TokenPair::class.java)
            .returnResult()
            .responseBody!!
    }

    feature("Getting your own identity") {
        scenario("should return HTTP 200") {

            webTestClient.get()
                .uri("")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token.accessToken)
                .exchange()
                .expectStatus()
                .isOk()
        }
    }

    feature("Getting an identity by ID") {
        scenario("should return HTTP 200") {

            webTestClient.get()
                .uri { uriBuilder -> uriBuilder
                    .path("/{id}")
                    .build("0")
                }
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token.accessToken)
                .exchange()
                .expectStatus()
                .isOk()
        }
    }
})
