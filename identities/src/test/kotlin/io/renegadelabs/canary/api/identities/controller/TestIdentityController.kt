package io.renegadelabs.canary.api.identities.controller

import io.kotest.core.spec.style.FeatureSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestIdentityController(
    private val webTestClient: WebTestClient
): FeatureSpec({

    feature("POST identity") {
        xscenario("should return HTTP 201") {

            webTestClient.post()
                .uri("/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(mapOf(
                        Pair("username", "testuser1"),
                        Pair("password", "testpassword123")
                )))
                .exchange()
                .expectStatus()
                .isCreated
                .expectHeader()
                .location("/")
        }
    }
})