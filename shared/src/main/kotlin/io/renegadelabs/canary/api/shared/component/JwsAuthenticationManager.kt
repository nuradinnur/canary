package io.renegadelabs.canary.api.shared.component

import io.jsonwebtoken.Claims
import io.jsonwebtoken.MissingClaimException
import io.renegadelabs.canary.api.shared.domain.JwsAuthenticationToken
import io.renegadelabs.canary.api.shared.util.extensions.hasValidRefreshClaims
import io.renegadelabs.canary.api.shared.util.extensions.hasValidSessionClaims
import io.renegadelabs.canary.api.shared.util.extensions.isExpired
import io.renegadelabs.canary.api.shared.util.CustomClaims
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
                .filter { !it.token.isExpired() }
                // TODO: move MissingClaimException to extension functions
                .switchIfEmpty(Mono.error(MissingClaimException(
                    authentication.token.header,
                    authentication.token.payload,
                    Claims.EXPIRATION,
                    authentication.token.payload.expiration,
                    "The token is expired"
                )))
                .filter { it.token.hasValidSessionClaims() || it.token.hasValidRefreshClaims() }
                .switchIfEmpty(Mono.error(MissingClaimException(
                    authentication.token.header,
                    authentication.token.payload,
                    CustomClaims.AUTHORITY,
                    authentication.token.payload[CustomClaims.AUTHORITY],
                    "The token does not have valid authorities"
                )))
                .cast(Authentication::class.java)
        }
    }
}