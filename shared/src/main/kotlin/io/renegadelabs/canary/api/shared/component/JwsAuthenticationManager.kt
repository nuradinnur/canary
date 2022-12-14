package io.renegadelabs.canary.api.shared.component

import io.jsonwebtoken.MissingClaimException
import io.renegadelabs.canary.api.shared.domain.JwsAuthenticationToken
import io.renegadelabs.canary.api.shared.extensions.hasValidRefreshClaims
import io.renegadelabs.canary.api.shared.extensions.hasValidSessionClaims
import io.renegadelabs.canary.api.shared.util.JwsUtils
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwsAuthenticationManager: ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {

        val jws = JwsUtils.toJws(authentication.credentials.toString())
        return Mono.just(jws)
            .filter { it.hasValidSessionClaims() || it.hasValidRefreshClaims() }
            .switchIfEmpty(Mono.error(MissingClaimException(jws.header, jws.body, jws.signature)))
            .map { validJws -> JwsAuthenticationToken(validJws) }
    }
}