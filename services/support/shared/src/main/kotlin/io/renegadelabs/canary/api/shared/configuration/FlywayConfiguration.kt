package io.renegadelabs.canary.api.shared.configuration

import io.r2dbc.spi.ConnectionFactoryOptions
import io.renegadelabs.canary.api.shared.extension.toJdbcUri
import io.renegadelabs.canary.api.shared.support.ConnectionFactoryOptionsSupport
import mu.KotlinLogging
import org.flywaydb.core.Flyway
import org.springframework.boot.autoconfigure.flyway.FlywayProperties
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val logger = KotlinLogging.logger {}

@EnableConfigurationProperties(value = [R2dbcProperties::class, FlywayProperties::class])
@Configuration
class FlywayConfiguration(
    private val flywayProperties: FlywayProperties
) {

    @Bean(initMethod = "migrate")
    fun flyway(): Flyway {
        logger.info { "Configuring Flyway database connection" }
        val that = this
        val jdbcConnectionOptions = ConnectionFactoryOptionsSupport.safeParse(this.flywayProperties.url)
        val jdbcUri = jdbcConnectionOptions.toJdbcUri(true)
        val username = jdbcConnectionOptions.getRequiredValue(ConnectionFactoryOptions.USER).toString()
        val password = jdbcConnectionOptions.getRequiredValue(ConnectionFactoryOptions.PASSWORD).toString()
        return with(Flyway.configure()) {
            dataSource(jdbcUri, username, password)
            schemas(*that.flywayProperties.schemas.toTypedArray())
            table(that.flywayProperties.table)
            baselineOnMigrate(that.flywayProperties.isBaselineOnMigrate)
            failOnMissingLocations(that.flywayProperties.isFailOnMissingLocations)
            locations(*that.flywayProperties.locations.toTypedArray())
            logger.info { "Configured database URL: $jdbcUri" }
            load()
        }
    }
}
