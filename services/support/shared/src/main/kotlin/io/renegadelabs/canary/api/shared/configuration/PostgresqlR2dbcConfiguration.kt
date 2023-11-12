package io.renegadelabs.canary.api.shared.configuration

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import io.renegadelabs.canary.api.shared.extension.toConnectionParameters
import io.renegadelabs.canary.api.shared.extension.toR2dbcUri
import io.renegadelabs.canary.api.shared.support.ConnectionFactoryOptionsSupport
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration

private val logger = KotlinLogging.logger {}

@ConditionalOnMissingBean(InMemoryR2dbcConfiguration::class)
@ConditionalOnProperty(name = ["spring.cloud.environment.enabled"], havingValue = "true")
@EnableConfigurationProperties(R2dbcProperties::class)
@Configuration
class PostgresqlR2dbcConfiguration(
    private val r2dbcProperties: R2dbcProperties
) : AbstractR2dbcConfiguration(), MessageSourceAware {

    private lateinit var messages: MessageSourceAccessor

    @Bean
    override fun connectionFactory(): PostgresqlConnectionFactory {
        logger.info { "Configuring PostgreSQL database connection" }
        val connectionOptions = ConnectionFactoryOptionsSupport.safeParse(this.r2dbcProperties.url)
        val r2dbcUri = connectionOptions.toR2dbcUri(true)
        val host = connectionOptions.getRequiredValue(ConnectionFactoryOptions.HOST).toString()
        val port = connectionOptions.getRequiredValue(ConnectionFactoryOptions.PORT).toString().toInt()
        val database = connectionOptions.getRequiredValue(ConnectionFactoryOptions.DATABASE).toString()
        val username = connectionOptions.getRequiredValue(ConnectionFactoryOptions.USER).toString()
        val password = connectionOptions.getRequiredValue(ConnectionFactoryOptions.PASSWORD).toString()
        val uriParameters = connectionOptions.toConnectionParameters()
        return with(PostgresqlConnectionConfiguration.builder()) {
            host(host)
            port(port)
            database(database)
            username(username)
            password(password)
            schema(uriParameters["schema"])
            logger.info { "Configured database URL: $r2dbcUri" }
            PostgresqlConnectionFactory(build())
        }
    }

    override fun setMessageSource(messageSource: MessageSource) {
        this.messages = MessageSourceAccessor(messageSource)
    }
}
