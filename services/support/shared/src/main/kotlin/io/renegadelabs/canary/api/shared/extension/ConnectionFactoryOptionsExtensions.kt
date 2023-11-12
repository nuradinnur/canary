package io.renegadelabs.canary.api.shared.extension

import io.r2dbc.spi.ConnectionFactoryOptions
import io.r2dbc.spi.Option

fun ConnectionFactoryOptions.toJdbcUri(stripAuthority: Boolean = false): String {
    return this.toConnectionUri(true, stripAuthority)
}

fun ConnectionFactoryOptions.toR2dbcUri(stripAuthority: Boolean = false): String {
    return this.toConnectionUri(false, stripAuthority)
}

fun ConnectionFactoryOptions.toConnectionParameters(): Map<String, String> {
    val driver = this.getRequiredValue(ConnectionFactoryOptions.DRIVER).toString()
    return this.getNonStandardOptions().entries.associate { (key, value) ->
        if (!driver.equals("h2", ignoreCase = true))
            key.name().lowercase() to value.toString()
        else
            key.name().uppercase() to value.toString()
    }
}


private fun ConnectionFactoryOptions.toConnectionUri(buildJdbcUrl: Boolean, stripAuthority: Boolean): String {
    val that = this
    val driver = that.getRequiredValue(ConnectionFactoryOptions.DRIVER).toString()
    val nonStandardOptions = that.getNonStandardOptions()
    return buildString {
        if (buildJdbcUrl) {
            this.append("jdbc:")
        } else {
            this.append("r2dbc:")
        }
        this.append(that.getRequiredValue(ConnectionFactoryOptions.DRIVER))
        this.append(":")

        // H2 URIs do not contain an authority, host, or port
        if (!driver.equals("h2", ignoreCase = true)) {
            this.append("//")
            // Optionally add a URI authority
            if (!stripAuthority) {
                this.append(that.getRequiredValue(ConnectionFactoryOptions.USER))
                this.append(":")
                this.append(that.getRequiredValue(ConnectionFactoryOptions.PASSWORD))
                this.append("@")
            }
            this.append(that.getRequiredValue(ConnectionFactoryOptions.HOST))
            this.append(":")
            this.append(that.getRequiredValue(ConnectionFactoryOptions.PORT))
            this.append("/")
            this.append(that.getRequiredValue(ConnectionFactoryOptions.DATABASE).toString())
            if (nonStandardOptions.isNotEmpty()) {
                this.append("?")
                nonStandardOptions.forEach { (key, value) ->
                    this.append(key.name().lowercase())
                    this.append("=")
                    this.append(value.toString())
                    this.append("&")
                }
                // Remove trailing ampersand ('&')
                this.deleteCharAt(this.length - 1)
            }
        }
        // Shortened URI for H2 databases
        else {
            this.append(that.getRequiredValue(ConnectionFactoryOptions.PROTOCOL))
            this.append(":")
            this.append(that.getRequiredValue(ConnectionFactoryOptions.DATABASE).toString())
            if (nonStandardOptions.isNotEmpty()) {
                nonStandardOptions.forEach { (key, value) ->
                    this.append(";")
                    this.append(key.name().uppercase())
                    this.append("=")
                    this.append(value.toString())
                }
            }
        }
    }
}

private fun ConnectionFactoryOptions.getNonStandardOptions(): Map<Option<*>, Any> {
    return this.getAllOptions().filter { !isStandardOption(it.key) }
}

private fun ConnectionFactoryOptions.isStandardOption(option: Option<*>): Boolean {
    return setOf(
        ConnectionFactoryOptions.CONNECT_TIMEOUT,
        ConnectionFactoryOptions.DATABASE,
        ConnectionFactoryOptions.DRIVER,
        ConnectionFactoryOptions.HOST,
        ConnectionFactoryOptions.LOCK_WAIT_TIMEOUT,
        ConnectionFactoryOptions.PASSWORD,
        ConnectionFactoryOptions.PORT,
        ConnectionFactoryOptions.PROTOCOL,
        ConnectionFactoryOptions.SSL,
        ConnectionFactoryOptions.STATEMENT_TIMEOUT,
        ConnectionFactoryOptions.USER
    ).any { it.name().equals(option.name(), ignoreCase = true) }
}

@Suppress("UNCHECKED_CAST")
private fun ConnectionFactoryOptions.getAllOptions(): Map<Option<*>, Any> {
    return this.javaClass.getDeclaredField("options").let {
        it.isAccessible = true
        val options = it.get(this) as Map<Option<*>, Any>
        it.isAccessible = false
        options
    }
}
