package io.renegadelabs.canary.api.starter.identity

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication
class IdentityApplication

fun main(args: Array<String>) {
    SpringApplicationBuilder()
            .bannerMode(Banner.Mode.OFF)
            .sources(IdentityApplication::class.java)
            .run(*args)
}
