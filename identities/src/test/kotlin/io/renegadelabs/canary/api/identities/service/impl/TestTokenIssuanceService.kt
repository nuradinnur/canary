package io.renegadelabs.canary.api.identities.service.impl

import io.renegadelabs.canary.api.identities.service.IdentityService
import io.renegadelabs.canary.api.identities.service.TokenIssuanceService
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestTokenIssuanceService(
    private val identityService: IdentityService,
    private val tokenIssuanceService: TokenIssuanceService
) {


}