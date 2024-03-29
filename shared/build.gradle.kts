group = "io.renegadelabs.canary"
version = "0.0.1"

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.graalvm) apply false
}

dependencies {
    /**
     * Kotlin dependencies
     */
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    api("org.jetbrains.kotlin:kotlin-reflect")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
    api("io.projectreactor.kotlin:reactor-kotlin-extensions")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    api(libs.kotlin.logging)

    /**
     * Spring Boot dependencies
     */
    api("org.springframework.boot:spring-boot-starter-webflux")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("org.springframework.boot:spring-boot-starter-security")
    api(libs.bundles.jjwt)

    /**
     * Test dependencies
     */
    testApi("org.springframework.boot:spring-boot-starter-test")
    testApi("io.projectreactor:reactor-test")
}