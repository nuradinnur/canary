package io.renegadelabs.canary.api.starter

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@OpenAPIDefinition(info = Info(title = "service-starter API Documentation", version = "1.0",
    description = "Documents all API endpoints for service-starter"))
@SpringBootApplication(
    scanBasePackages = [
        "io.renegadelabs.canary.api.shared",
        "io.renegadelabs.canary.api.starter"
    ]
)
@OpenAPIDefinition(info = Info(title = "service-starter-service API Documentation", version = "1.0",
    description = "Documents all API endpoints for service-starter-service")
)
class ServiceStarterApplication

fun main(args: Array<String>) {
    SpringApplicationBuilder()
        .bannerMode(Banner.Mode.OFF)
        .sources(ServiceStarterApplication::class.java)
        .run(*args)
}
