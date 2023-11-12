group = "io.renegadelabs.canary.api"
version = "1.0.0"

plugins {

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.graalvm) apply false
}

dependencies {

    /**
     * Kotlin dependencies
     */
    api(libs.bundles.kotlin)

    /**
     * Spring Boot dependencies
     */
    api(libs.bundles.spring.webflux)
    api(libs.bundles.spring.cloud)
    api(libs.bundles.spring.cache)
    api(libs.bundles.spring.security)
    api(libs.bundles.spring.data.r2dbc)

    /**
     * Test dependencies
     */
    testFixturesApi(libs.bundles.kotest)
    testFixturesApi(libs.bundles.kotest.spring.webflux)
    testFixturesApi(libs.bundles.kotest.spring.security)
    testFixturesApi(libs.bundles.kotest.spring.data)
}