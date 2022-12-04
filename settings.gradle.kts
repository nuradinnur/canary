rootProject.name = "canary"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

include("shared")
include("service-starter")
include("identities")
