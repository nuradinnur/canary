rootProject.name = "canary"

pluginManagement {
	repositories {
		maven { url = uri("https://repo.spring.io/release") }
		mavenCentral()
		gradlePluginPortal()
	}
}

include("shared")
include("service-starter")
include("identities")
