package io.renegadelabs.canary.api.identities.controller

import io.kotest.core.annotation.Tags
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveMinLength
import io.kotest.matchers.string.shouldStartWith
import io.renegadelabs.canary.api.identities.controller.request.CreateIdentityRequest
import io.renegadelabs.canary.api.identities.domain.Identity
import io.renegadelabs.canary.api.shared.domain.Authority
import io.renegadelabs.canary.api.shared.test.AbstractIntegrationTest
import io.renegadelabs.canary.api.shared.test.configuration.WebTestClientConfiguration
import io.renegadelabs.canary.api.shared.test.domain.TestTags
import org.springframework.http.HttpHeaders
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockAuthentication
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.BodyInserters

/**
 * TODO: note that running any test individually using the Kotest plugin requires the following JVM argument set
 *       "-XX:+AllowRedefinitionToAddDeleteMethods"
*/
@Tags(TestTags.INTEGRATION_TEST)
@ContextConfiguration(classes = [WebTestClientConfiguration::class])
class TestIdentityController(
    private val webTestClient: WebTestClient
): AbstractIntegrationTest() {

    init {
        this.feature("I want to create a new identity") {
            scenario("the username doesn't exist") {
                webTestClient
                    .post()
                    .uri(IdentityController.POST_IDENTITY_MAPPING)
                    .body(BodyInserters.fromValue(CreateIdentityRequest("username", "password")))
                    .exchange()
                    .expectAll(
                        { response -> response.expectStatus().isCreated },
                        { response -> response.expectBody().isEmpty },
                        { response -> response.expectHeader().value(HttpHeaders.LOCATION) { it
                            .shouldHaveMinLength(IdentityController.BASE_PATH.plus("/").length + 1)
                            .shouldStartWith(IdentityController.BASE_PATH.plus("/"))
                            .takeLast(1)
                            .shouldBe("2")
                        } }
                    )
            }

            scenario("the username already exists") {
                webTestClient
                    .post()
                    .uri(IdentityController.POST_IDENTITY_MAPPING)
                    .body(BodyInserters.fromValue(CreateIdentityRequest("testusername", "testpassword")))
                    .exchange()
                    .expectAll(
                        { response -> response.expectStatus().isBadRequest },
                        { response -> response.expectBody<Map<String, Any>>() }
                    )
            }
        }

        this.feature("I want to retrieve the currently authenticated identity") {
            scenario("the request is authenticated by an identity") {
                val identity = Identity.create(
                    username = "username",
                    password = "password",
                    authorities = setOf(Authority.USER)
                )
                webTestClient.mutateWith(mockAuthentication(getJwtAuthentication(identity)))
                    .get()
                    .uri(IdentityController.GET_IDENTITY_MAPPING)
                    .exchange()
                    .expectAll(
                        { response -> response.expectStatus().isOk },
                        { response -> response.expectBody<Identity>() }
                    )
            }

            scenario("the request is not sufficiently authenticated") {
                webTestClient.mutateWith(mockAuthentication(getAnonymousAuthentication()))
                    .get()
                    .uri(IdentityController.GET_IDENTITY_MAPPING)
                    .exchange()
                    .expectAll(
                        { response -> response.expectStatus().isForbidden },
                        { response -> response.expectBody<Map<String, Any>>() }
                    )
            }
        }

        this.feature("I want to retrieve an identity by id") {
            scenario("the request authentication has administrator permissions") {
                val identity = Identity.create(
                    username = "system",
                    password = "systempassword",
                    authorities = setOf(Authority.ADMINISTRATOR)
                )
                webTestClient.mutateWith(mockAuthentication(getJwtAuthentication(identity)))
                    .get()
                    .uri { uriBuilder -> uriBuilder.path(IdentityController.GET_IDENTITY_PARAMETERIZED_MAPPING)
                        .build(0)
                    }
                    .exchange()
                    .expectAll(
                        { response -> response.expectStatus().isOk },
                        { response -> response.expectBody<Identity>() }
                    )
            }

            scenario("the request authentication is for an identity who requests their own id") {
                val identity = Identity.create(
                    username = "username",
                    password = "password",
                    authorities = setOf(Authority.ADMINISTRATOR)
                )
                webTestClient.mutateWith(mockAuthentication(getJwtAuthentication(identity)))
                    .get()
                    .uri { uriBuilder -> uriBuilder
                        .path(IdentityController.GET_IDENTITY_PARAMETERIZED_MAPPING)
                        .build(identity.getId())
                    }
                    .exchange()
                    .expectAll(
                        { response -> response.expectStatus().isForbidden },
                        { response -> response.expectBody<Map<String, Any>>() }
                    )
            }

            scenario("the request is not sufficiently authenticated") {
                webTestClient.mutateWith(mockAuthentication(getAnonymousAuthentication()))
                    .get()
                    .uri { uriBuilder -> uriBuilder
                        .path(IdentityController.GET_IDENTITY_PARAMETERIZED_MAPPING)
                        .build(0)
                    }
                    .exchange()
                    .expectAll(
                        { response -> response.expectStatus().isBadRequest },
                        { response -> response.expectBody<Map<String, Any>>() }
                    )
            }
        }
    }
}
