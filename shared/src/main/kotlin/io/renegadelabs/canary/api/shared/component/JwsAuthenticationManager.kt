package io.renegadelabs.canary.api.shared.component

import io.jsonwebtoken.MissingClaimException
import io.renegadelabs.canary.api.shared.domain.JwsAuthenticationToken
import io.renegadelabs.canary.api.shared.extensions.hasValidRefreshClaims
import io.renegadelabs.canary.api.shared.extensions.hasValidSessionClaims
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwsAuthenticationManager: ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return if (authentication !is JwsAuthenticationToken) {
            Mono.empty()
        } else {
            Mono.just(authentication)
                .filter { it.token.hasValidSessionClaims() || it.token.hasValidRefreshClaims() }
                .switchIfEmpty(Mono.error(MissingClaimException(authentication.token.header, authentication.token.body, authentication.token.signature)))
                .cast(Authentication::class.java)
        }
    }
}