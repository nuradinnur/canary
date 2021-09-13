package io.renegadelabs.canary.api.starter

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication(
    scanBasePackages = [
        "io.renegadelabs.canary.api.shared",
        "io.renegadelabs.canary.api.starter"
    ]
)
class ServiceStarterApplication

fun main(args: Array<String>) {
    SpringApplicationBuilder()
        .bannerMode(Banner.Mode.OFF)
        .sources(ServiceStarterApplication::class.java)
        .run(*args)
}
