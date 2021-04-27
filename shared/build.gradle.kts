group = "io.renegadelabs.canary"

plugins {
    java
    kotlin("jvm")
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    api("org.jetbrains.kotlin:kotlin-reflect")
    api("org.springframework.boot:spring-boot-starter-webflux")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
    api("io.projectreactor.kotlin:reactor-kotlin-extensions")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    api("org.springframework.boot:spring-boot-starter-actuator")
    testApi("org.springframework.boot:spring-boot-starter-test")
    testApi("io.projectreactor:reactor-test")
}