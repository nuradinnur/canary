package io.renegadelabs.canary.api.shared.component

import io.renegadelabs.canary.api.shared.support.JwtParserSupport
import io.renegadelabs.canary.api.shared.domain.Authority
import io.renegadelabs.canary.api.shared.domain.JwtAuthentication
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationSecurityContextRepository(
    private val reactiveAuthenticationManager: ReactiveAuthenticationManager,
    private val jwtAuthenticationReader: JwtAuthenticationReader
): ServerSecurityContextRepository {

    override fun save(serverWebExchange: ServerWebExchange, securityContext: SecurityContext): Mono<Void> {
        TODO("Not yet implemented")
    }

    override fun load(serverWebExchange: ServerWebExchange): Mono<SecurityContext> {
        return Mono.fromCallable { this.getJwtAuthentication(serverWebExchange) }
            .flatMap { this.reactiveAuthenticationManager.authenticate(it) }
            .map { SecurityContextImpl(it) }
    }

    private fun getJwtAuthentication(serverWebExchange: ServerWebExchange): Authentication {
        val authorizationHeaderValue = serverWebExchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION).orEmpty()
        if (authorizationHeaderValue.isBlank()) {
            // TODO: probably should not use the signing key here...
            return AnonymousAuthenticationToken(String(JwtAuthenticationReader.SIGNING_KEY.encoded), "anonymous", setOf(Authority.ANONYMOUS))
        }
        val tokenAsString = JwtParserSupport.removeTokenPrefix(authorizationHeaderValue)
        return JwtAuthentication(tokenAsString, JwtParserSupport.toJws(tokenAsString))
    }
}
