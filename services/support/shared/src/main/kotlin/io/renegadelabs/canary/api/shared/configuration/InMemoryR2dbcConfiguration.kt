package io.renegadelabs.canary.api.shared.configuration

import io.r2dbc.h2.H2ConnectionConfiguration
import io.r2dbc.h2.H2ConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import io.renegadelabs.canary.api.shared.extension.toR2dbcUri
import io.renegadelabs.canary.api.shared.support.ConnectionFactoryOptionsSupport
import mu.KotlinLogging
import org.apache.commons.lang3.StringUtils
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration

private val logger = KotlinLogging.logger {}

@ConditionalOnMissingBean(PostgresqlR2dbcConfiguration::class)
@ConditionalOnProperty(name = ["spring.cloud.environment.enabled"], havingValue = "false")
@EnableConfigurationProperties(R2dbcProperties::class)
@Configuration
class InMemoryR2dbcConfiguration(
    private val r2dbcProperties: R2dbcProperties
) : AbstractR2dbcConfiguration() {

    @Bean
    override fun connectionFactory(): ConnectionFactory {
        logger.info { "Configuring in-memory database connection" }
        val r2dbcConnectionOptions = ConnectionFactoryOptionsSupport.safeParse(this.r2dbcProperties.url)
        val r2dbcUri = r2dbcConnectionOptions.toR2dbcUri()
        // H2ConnectionConfiguration.Builder#url() requires a R2DBC uri that has no scheme or driver prefix
        val strippedUri = r2dbcUri.substring(StringUtils.ordinalIndexOf(r2dbcUri, ":", 2) + 1)
        val username = r2dbcConnectionOptions.getRequiredValue(ConnectionFactoryOptions.USER).toString()
        val password = r2dbcConnectionOptions.getRequiredValue(ConnectionFactoryOptions.PASSWORD).toString()
        return with(H2ConnectionConfiguration.builder()) {
            url(strippedUri)
            username(username)
            password(password)
            logger.info { "Configured database URL: r2dbc:h2:$strippedUri" }
            H2ConnectionFactory(build())
        }
    }
}
