package io.renegadelabs.canary.api.shared.support

import io.r2dbc.spi.ConnectionFactoryOptions
import io.r2dbc.spi.Option
import io.renegadelabs.canary.api.shared.extension.toR2dbcUri
import org.springframework.util.Assert

internal class ConnectionFactoryOptionsSupport {

    companion object {
        /**
         * Database option is incorrectly parsed for H2 databases - the option is parsed as:
         *
         * ConnectionFactoryOptions{
         *      options={
         *          driver=h2,
         *          protocol=mem,
         *          database=canary;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1;MODE=PostgreSQL; // ...
         *      }
         *  }
         *
         * This will probably not be fixed by the r2dbc-spi maintainers who do not plan on supporting short URIs.
         * More information can be found here: https://github.com/r2dbc/r2dbc-spi/issues/196
         */
        fun safeParse(connectionUri: CharSequence): ConnectionFactoryOptions {
            // Conversion to R2DBC URI is required in ConnectionFactoryOptions.parse(url)
            val options = when {
                connectionUri.startsWith("r2dbc:") -> ConnectionFactoryOptions.parse(connectionUri)
                else -> ConnectionFactoryOptions.parse("r2dbc:${connectionUri.toString().substringAfter("jdbc:")}")
            }
            val driver = options.getRequiredValue(ConnectionFactoryOptions.DRIVER)
            val host = options.getValue(ConnectionFactoryOptions.HOST)
            // Short URIs are only applicable when using the H2 driver and do not have hosts or ports
            // TODO: confirm the above is factual
            return if (!driver.toString().equals("H2", ignoreCase = true) && host == null) {
                options
            } else {
                with(options.mutate()) {
                    // Parsed "normally", like the example in the function-level comment
                    val invalidDatabaseValue = options.getRequiredValue(ConnectionFactoryOptions.DATABASE).toString()
                    // Correctly parse ConnectionFactoryOptions.DATABASE option value
                    val validDatabaseValue = invalidDatabaseValue.substringBefore(';')
                    this.option(ConnectionFactoryOptions.DATABASE, validDatabaseValue)
                    // Correctly parse connection parameters without a ConnectionFactoryOptions constant
                    val connectionParameters = invalidDatabaseValue.removePrefix(validDatabaseValue)
                    parseConnectionParameters(connectionParameters).entries.forEach {
                        (key, value) -> this.option(key, value)
                    }
                    this.build()
                }
            }
        }

        private fun parseConnectionParameters(connectionParameters: String): Map<Option<String>, String> {
            if (connectionParameters.isBlank())
                return emptyMap()
            val prefix = connectionParameters[0]
            Assert.isTrue(prefix == ';' || prefix == '?',
                "Invalid connection parameter format. Expecting connection parameters to start with " +
                        "';' or '?', actual: \"$prefix\", connection parameter string \"$connectionParameters\"")
            val delimiter = when (connectionParameters[0]) {
                ';' -> ';'
                else -> '&'
            }
            return connectionParameters
                .trim(predicate = { c -> c == prefix })
                .split(delimiter)
                .associate {
                    val rawPair = it.split("=")
                    Assert.isTrue(rawPair.size == 2 && rawPair[0].isNotBlank() && rawPair[1].isNotBlank(),
                        "Invalid connection parameter format. " + "Expecting format: " +
                                "\"${delimiter}KEY=value\", actual: \"$it\"")
                    it.trim(predicate = { c -> c == '=' })
                        .split("=")
                        .let { (key, value) -> when (key.lowercase()) {
                            ConnectionFactoryOptions.USER.name() ->
                                Option.valueOf<String>(ConnectionFactoryOptions.USER.name().lowercase()) to value
                            ConnectionFactoryOptions.PASSWORD.name() ->
                                Option.valueOf<String>(ConnectionFactoryOptions.PASSWORD.name().lowercase()) to value
                            else -> Option.valueOf<String>(key) to value
                        } }
                }
        }
    }
}
