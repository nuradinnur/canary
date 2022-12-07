package io.renegadelabs.canary.api.identities

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication(
    scanBasePackages = [
        "io.renegadelabs.canary.api.shared",
        "io.renegadelabs.canary.api.identities"
    ]
)
class IdentitiesApplication

fun main(args: Array<String>) {
    SpringApplicationBuilder()
        .bannerMode(Banner.Mode.OFF)
        .sources(IdentitiesApplication::class.java)
        .run(*args)
}
