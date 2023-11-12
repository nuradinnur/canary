package io.renegadelabs.canary.api.identities

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@OpenAPIDefinition(info = Info(title = "identities-service API Documentation", version = "1.0",
    description = "Documents all API endpoints for identities-service"))
@SpringBootApplication(
    scanBasePackages = [
        "io.renegadelabs.canary.api.shared",
        "io.renegadelabs.canary.api.identities"
    ]
)
@OpenAPIDefinition(info = Info(title = "identities-service API Documentation", version = "1.0",
    description = "Documents all API endpoints for identities-service"))
class IdentitiesApplication

fun main(args: Array<String>) {
    SpringApplicationBuilder()
        .bannerMode(Banner.Mode.OFF)
        .sources(IdentitiesApplication::class.java)
        .run(*args)
}
