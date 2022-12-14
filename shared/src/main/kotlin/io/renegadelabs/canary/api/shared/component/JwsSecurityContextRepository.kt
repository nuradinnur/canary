package io.renegadelabs.canary.api.shared.component

import io.renegadelabs.canary.api.shared.domain.JwsAuthenticationToken
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwsSecurityContextRepository(
    private val reactiveAuthenticationManager: ReactiveAuthenticationManager
): ServerSecurityContextRepository {

    override fun save(exchange: ServerWebExchange, context: SecurityContext): Mono<Void> {
        TODO("Not yet implemented")
    }

    override fun load(exchange: ServerWebExchange): Mono<SecurityContext> {
        return Mono.justOrEmpty(exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION))
            .flatMap { header ->
                val token = JwsAuthenticationToken(header)
                this.reactiveAuthenticationManager.authenticate(token)
                    .map { SecurityContextImpl(it) }
            }
    }
}