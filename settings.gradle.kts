rootProject.name = "canary"

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.7.0")
}

include("shared")
include("service-starter")
include("identities")

project(":shared").projectDir = file("services/support/shared")
project(":service-starter").projectDir = file("services/support/service-starter")
project(":identities").projectDir = file("services/identities")
