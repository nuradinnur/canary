package io.renegadelabs.canary.api.identities.service.impl

import io.renegadelabs.canary.api.identities.service.IdentityService
import io.renegadelabs.canary.api.identities.service.SessionService
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestSessionService(
    private val identityService: IdentityService,
    private val sessionService: SessionService
) {


}