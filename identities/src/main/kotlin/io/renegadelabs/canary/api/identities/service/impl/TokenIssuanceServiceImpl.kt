package io.renegadelabs.canary.api.identities.service.impl

import io.jsonwebtoken.Jwts
import io.renegadelabs.canary.api.identities.domain.TokenPair
import io.renegadelabs.canary.api.identities.service.IdentityPasswordService
import io.renegadelabs.canary.api.identities.service.IdentityService
import io.renegadelabs.canary.api.identities.service.TokenIssuanceService
import io.renegadelabs.canary.api.shared.domain.Authorities
import io.renegadelabs.canary.api.shared.util.CustomClaims
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
class TokenIssuanceServiceImpl(
    private val identityService: IdentityService,
    private val identityPasswordService: IdentityPasswordService
) : TokenIssuanceService {

    private lateinit var messages: MessageSourceAccessor

    override fun createTokenPair(username: String, password: String): Mono<TokenPair> {
        return this.identityPasswordService.validatePassword(username, password)
            .then(this.identityService.getIdentityByUsername(username)).map { identity ->
                TokenPair(
                    this.createToken(identity.username, identity.authorities.toSet(), Duration.ofMinutes(15)),
                    this.createToken(identity.username, setOf(Authorities.REFRESH), Duration.ofHours(1))
                )
            }
    }

    override fun refreshTokenPair(refreshToken: String): Mono<TokenPair> {
        return this.identityService.getIdentityByUsername(JwsUtils.toSubject(refreshToken))
            .map { this.createToken(it.username, it.authorities.toSet(), Duration.ofMinutes(15)) }
            .map { TokenPair(it, JwsUtils.stripTokenPrefix(refreshToken)) }
    }

    override fun setMessageSource(messageSource: MessageSource) {
        this.messages = MessageSourceAccessor(messageSource)
    }

    private fun createToken(subject: String, authorities: Set<GrantedAuthority>, duration: Duration): String {
        val issuedAt = Instant.now()
        val expiration = issuedAt.plus(duration).plus(JwsUtils.CLOCK_SKEW_TOLERANCE)

        val claims = with(Jwts.claims()) {
            this.subject(subject)
            // TODO: create constants for issuer and audience
            this.issuer("identities")
            this.audience().add("canary")
            this.issuedAt(Date.from(issuedAt))
            this.expiration(Date.from(expiration))
            this.add(CustomClaims.AUTHORITY, authorities)
            this.build()
        }

        return Jwts.builder().claims(claims).signWith(JwsUtils.SIGNING_KEY).compact()
    }
}