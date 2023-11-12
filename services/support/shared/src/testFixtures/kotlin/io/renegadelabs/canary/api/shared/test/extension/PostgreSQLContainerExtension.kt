package io.renegadelabs.canary.api.shared.test.extension

import io.r2dbc.spi.ConnectionFactoryOptions
import io.renegadelabs.canary.api.shared.support.ConnectionFactoryOptionsSupport
import org.testcontainers.containers.PostgreSQLContainer

fun PostgreSQLContainer<*>.getConnectionFactoryOptions(): ConnectionFactoryOptions {
    return ConnectionFactoryOptionsSupport.safeParse("r2dbc:${this.jdbcUrl.substringAfter("jdbc:")}")
        .mutate()
        .option(ConnectionFactoryOptions.USER, this.username)
        .option(ConnectionFactoryOptions.PASSWORD, this.password)
        .build()
}
