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
//    api("org.springframework.cloud:spring-cloud-starter-kubernetes-client")
    api("io.github.microutils:kotlin-logging:2.0.11")
    testApi("org.springframework.boot:spring-boot-starter-test")
    testApi("io.projectreactor:reactor-test")
}
