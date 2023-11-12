package io.renegadelabs.canary.api.shared.test.configuration

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration
import java.util.*

@TestConfiguration
class WebTestClientConfiguration {

    @Bean
    @Primary
    fun webTestClient(applicationContext: ApplicationContext): WebTestClient {
        return WebTestClient
            .bindToApplicationContext(applicationContext)
            .apply(SecurityMockServerConfigurers.springSecurity())
            .configureClient()
            .responseTimeout(Duration.ofSeconds(5))
            .defaultHeaders {
                // TODO: ensure webTestClient and messageSourceAccessor communicate through Accept-Language header
                it.accept = listOf(MediaType.APPLICATION_JSON)
                it.acceptLanguageAsLocales = listOf(Locale.US)
                it.contentType = MediaType.APPLICATION_JSON
            }
            .build()
    }
}