package io.renegadelabs.canary.api.shared.test

import io.jsonwebtoken.Jwts
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.extensions.testcontainers.perProject
import io.r2dbc.spi.ConnectionFactoryOptions
import io.renegadelabs.canary.api.shared.component.JwtAuthenticationReader
import io.renegadelabs.canary.api.shared.domain.Authority
import io.renegadelabs.canary.api.shared.domain.JwtAuthentication
import io.renegadelabs.canary.api.shared.domain.JwtClaim
import io.renegadelabs.canary.api.shared.extension.toConnectionParameters
import io.renegadelabs.canary.api.shared.extension.toJdbcUri
import io.renegadelabs.canary.api.shared.extension.toR2dbcUri
import io.renegadelabs.canary.api.shared.support.ConnectionFactoryOptionsSupport
import io.renegadelabs.canary.api.shared.test.extension.getConnectionFactoryOptions
import io.renegadelabs.canary.api.shared.test.support.IntegrationTestSupport
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec.ResponseSpecConsumer
import org.springframework.test.web.reactive.server.expectBody
import java.time.Duration
import java.time.Instant
import java.util.*

@ActiveProfiles("test")
@SpringBootTest
abstract class AbstractIntegrationTest: FeatureSpec() {

    companion object {
        /**
         * TODO: Kafka containers require a platform change while running on Apple Silicon
         *       kafkaContainer.apply { it.withPlatform("linux/amd64") }
         *       For more information: https://kotest.io/docs/extensions/test_containers.html#kafka-containers
         */
        @JvmStatic
        val postgresql = IntegrationTestSupport.createPostgresqlContainer().apply {
            val springProperties = IntegrationTestSupport.getSpringProperties()
            val r2dbcUri = springProperties["spring.r2dbc.url"].toString()
            val connectionOptions = ConnectionFactoryOptionsSupport.safeParse(r2dbcUri)
            val uriParameters = connectionOptions.toConnectionParameters()

            this.withDatabaseName(connectionOptions.getRequiredValue(ConnectionFactoryOptions.DATABASE).toString())
            this.withUsername(connectionOptions.getRequiredValue(ConnectionFactoryOptions.USER).toString())
            this.withPassword(connectionOptions.getRequiredValue(ConnectionFactoryOptions.PASSWORD).toString())
            uriParameters.forEach { (key, value) -> this.withUrlParam(key, value)}
            this.start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun setSpringDataConfigurationProperties(dynamicPropertyRegistry: DynamicPropertyRegistry) {
            val connectionOptions = this.postgresql.getConnectionFactoryOptions()
            dynamicPropertyRegistry.add("spring.flyway.url", connectionOptions::toJdbcUri)
            dynamicPropertyRegistry.add("spring.r2dbc.url", connectionOptions::toR2dbcUri)
            dynamicPropertyRegistry.add("spring.r2dbc.username", postgresql::getUsername)
            dynamicPropertyRegistry.add("spring.r2dbc.password", postgresql::getPassword)
            dynamicPropertyRegistry.add("spring.r2dbc.name", postgresql::getDatabaseName)
        }
    }

    override fun extensions(): List<Extension> {
        return super.extensions().plus(postgresql.perProject())
    }

    override fun timeout() = 5_000L

    protected fun getAnonymousAuthentication(): AnonymousAuthenticationToken {
        return AnonymousAuthenticationToken("key", "anonymous", setOf(Authority.ANONYMOUS))
    }

    protected fun getJwtAuthentication(userDetails: UserDetails): JwtAuthentication {
        val issuedAt = Instant.now()
        val expiration = issuedAt.plus(Duration.ofMinutes(5)).plus(JwtAuthenticationReader.CLOCK_SKEW_TOLERANCE)

        val claims = with(Jwts.claims()) {
            // TODO: create constants for issuer and audience
            this.id(UUID.randomUUID().toString())
            this.issuer("identities")
            this.add(JwtClaim.AUDIENCE.getKey(), setOf("canary"))
            this.issuedAt(Date.from(issuedAt))
            this.notBefore(Date.from(issuedAt))
            this.expiration(Date.from(expiration))
            this.subject(userDetails.username)
            this.add(JwtClaim.SUB_ID.getKey(), 1)
            this.add(JwtClaim.AUTHORIZATION_DETAILS.getKey(), userDetails)
            this.build()
        }

        val token = Jwts.builder().claims(claims).signWith(JwtAuthenticationReader.SIGNING_KEY).compact()
        val authorities = userDetails.authorities.map { Authority.valueOf(it.authority) }.toSet()
        return JwtAuthentication(token, authorities)
    }

    protected fun ResponseSpec.expectErrorResponse(
        status: HttpStatus,
        message: String
    ): ResponseSpec {
        val expectBody = ResponseSpecConsumer {
            response: ResponseSpec -> response.expectBody<Map<String, Any>>()
//            .isEqualTo<>()
        }
        val expectTimestamp = ResponseSpecConsumer {
            response: ResponseSpec -> response.expectBody<Map<String, Any>>().value {
            }
        }
        val expectPath = ResponseSpecConsumer {
            response: ResponseSpec -> response.expectStatus().isEqualTo(status)
        }
        val expectStatus = ResponseSpecConsumer {
            response: ResponseSpec -> response.expectStatus().isEqualTo(status)
        }
        val expectMessage = ResponseSpecConsumer {
            response: ResponseSpec -> response.expectStatus().isEqualTo(status)
        }
        this.expectAll(expectBody, expectTimestamp, expectPath, expectStatus, expectMessage)
        return this
    }
}