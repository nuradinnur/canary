package io.renegadelabs.canary.api.shared.configuration

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpStatus
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@Configuration
class WebFluxConfiguration(
    private val reactiveAuthenticationManager: ReactiveAuthenticationManager,
    private val serverSecurityContextRepository: ServerSecurityContextRepository
): WebFluxConfigurer {

    @Bean
    @Primary
    fun jackson2ObjectMapperBuilder(): Jackson2ObjectMapperBuilder {
        return Jackson2ObjectMapperBuilder()
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .featuresToEnable(SerializationFeature.INDENT_OUTPUT)
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .modulesToInstall(KotlinModule.Builder().build())
    }

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            // TODO: see what happens when exception handling is disabled - does WebExceptionHandler take over instead?
            .exceptionHandling {
                exceptionHandlingCustomizer ->
                    exceptionHandlingCustomizer.authenticationEntryPoint { exchange, throwable ->
                        Mono.fromRunnable {
                            logger.error(throwable) { throwable.localizedMessage }
                            exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                        }
                    }
                    exceptionHandlingCustomizer.accessDeniedHandler { exchange, throwable ->
                        Mono.fromRunnable {
                            logger.error(throwable) { throwable.localizedMessage }
                            exchange.response.statusCode = HttpStatus.FORBIDDEN
                        }
                    }
            }
            .httpBasic { httpBasicCustomizer -> httpBasicCustomizer.disable() }
            .formLogin { formLoginCustomizer -> formLoginCustomizer.disable() }
            .csrf { csrfCustomizer -> csrfCustomizer.disable() }
            .anonymous { anonymousCustomizer -> anonymousCustomizer.disable() }
            .authenticationManager(this.reactiveAuthenticationManager)
            .securityContextRepository(this.serverSecurityContextRepository)
            .authorizeExchange {
                authorizeExchangeCustomizer -> authorizeExchangeCustomizer
                .anyExchange()
                .permitAll()
            }
            .build()
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("*")
            .allowedHeaders("*")
    }
}
