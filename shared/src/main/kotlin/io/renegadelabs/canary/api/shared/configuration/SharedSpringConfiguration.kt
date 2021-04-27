package io.renegadelabs.canary.api.shared.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource("classpath:bootstrap.yml")
class SharedSpringConfiguration