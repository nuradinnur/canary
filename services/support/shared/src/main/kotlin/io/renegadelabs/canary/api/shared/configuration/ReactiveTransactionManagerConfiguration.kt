package io.renegadelabs.canary.api.shared.configuration

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Configuration
import org.springframework.r2dbc.connection.R2dbcTransactionManager

@Configuration
class ReactiveTransactionManagerConfiguration(
    private val connectionFactory: ConnectionFactory
) : R2dbcTransactionManager(connectionFactory)