package io.renegadelabs.canary.api.identities.service.impl

import io.jsonwebtoken.Jwts
import io.renegadelabs.canary.api.identities.domain.Session
import io.renegadelabs.canary.api.identities.service.IdentityPasswordService
import io.renegadelabs.canary.api.identities.service.IdentityService
import io.renegadelabs.canary.api.identities.service.SessionService
import io.renegadelabs.canary.api.shared.domain.Authorities
import io.renegadelabs.canary.api.shared.util.JwsUtils
import org.springframework.context.MessageSource
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
class SessionServiceImpl(
    private val identityService: IdentityService,
    private val identityPasswordService: IdentityPasswordService
) : SessionService {

    private lateinit var messages: MessageSourceAccessor

    override fun createSession(username: String, password: String): Mono<Session> {
        return this.identityPasswordService.validatePassword(username, password)
            .then(this.identityService.findByUsername(username))
            .map { userDetails -> Session(
                this.createToken(userDetails.username, userDetails.authorities.toSet(), Duration.ofMinutes(15)),
                this.createToken(userDetails.username, setOf(Authorities.REFRESH), Duration.ofHours(1))
            )}
    }

    override fun refreshSession(jsonWebToken: String): Mono<Session> {
        return this.identityService.getIdentityByUsername(JwsUtils.toSubject(jsonWebToken))
            .map { this.createToken(it.username, it.authorities.toSet(), Duration.ofMinutes(15)) }
            .map { Session(it, jsonWebToken) }
    }

    override fun setMessageSource(messageSource: MessageSource) {
        this.messages = MessageSourceAccessor(messageSource)
    }

    private fun createToken(subject: String, authorities: Set<GrantedAuthority>, duration: Duration): String {
        val issuedAt = Instant.now()
        val expiration = issuedAt.plus(duration).plus(JwsUtils.CLOCK_SKEW_TOLERANCE)

        val claims = Jwts.claims()
        claims.subject = subject
        claims.issuer = "identities"
        claims.audience = "canary"
        claims.issuedAt = Date.from(issuedAt)
        claims.expiration = Date.from(expiration)
        claims["authorities"] = authorities

        return Jwts.builder()
            .setClaims(claims)
            .compact()
    }
}