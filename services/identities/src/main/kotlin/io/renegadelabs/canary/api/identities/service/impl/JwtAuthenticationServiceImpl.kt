package io.renegadelabs.canary.api.identities.service.impl

import io.jsonwebtoken.Jwts
import io.renegadelabs.canary.api.identities.domain.Identity
import io.renegadelabs.canary.api.identities.domain.TokenPair
import io.renegadelabs.canary.api.identities.service.IdentityPasswordService
import io.renegadelabs.canary.api.identities.service.IdentityService
import io.renegadelabs.canary.api.identities.service.JwtAuthenticationService
import io.renegadelabs.canary.api.shared.support.JwtParserSupport
import io.renegadelabs.canary.api.shared.component.JwtAuthenticationReader
import io.renegadelabs.canary.api.shared.domain.Authority
import io.renegadelabs.canary.api.shared.domain.JwtClaim
import org.springframework.context.MessageSource
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
class JwtAuthenticationServiceImpl(
    private val identityService: IdentityService,
    private val identityPasswordService: IdentityPasswordService
) : JwtAuthenticationService {

    private lateinit var messages: MessageSourceAccessor

    override fun createJwtAuthentication(username: String, password: String): Mono<TokenPair> {
        return this.identityPasswordService.validatePassword(username, password)
            .then(this.identityService.readIdentityByUsername(username))
            .map { identity -> TokenPair(
                // TODO: is hardcoding token duration here a good idea?
                this.createToken(identity, identity.authorities, Duration.ofMinutes(15)),
                this.createToken(identity, setOf(Authority.REFRESH), Duration.ofHours(1))
            )}
    }

    override fun refreshTokenPair(refreshToken: String): Mono<TokenPair> {
        val rawRefreshToken = JwtParserSupport.removeTokenPrefix(refreshToken)
        val subject = JwtParserSupport.toJws(rawRefreshToken).payload.subject!!
        return this.identityService.readIdentityByUsername(subject)
            .map { this.createToken(it, it.authorities, Duration.ofMinutes(15)) }
            .map { TokenPair(it, JwtParserSupport.removeTokenPrefix(refreshToken)) }
    }


    @PreAuthorize("denyAll()")
    override fun setMessageSource(messageSource: MessageSource) {
        this.messages = MessageSourceAccessor(messageSource)
    }

    private fun createToken(identity: Identity, authorities: Set<Authority>, duration: Duration): String {
        val issuedAt = Instant.now()
        val expiration = issuedAt.plus(duration).plus(JwtAuthenticationReader.CLOCK_SKEW_TOLERANCE)

        val claims = with(Jwts.claims()) {
            // TODO: create constants for issuer and audience
            this.id(UUID.randomUUID().toString())
            this.issuer("identities")
            this.add(JwtClaim.AUDIENCE.getKey(), setOf("canary"))
            this.issuedAt(Date.from(issuedAt))
            this.notBefore(Date.from(issuedAt))
            this.expiration(Date.from(expiration))
            this.subject(identity.username)
            this.add(JwtClaim.SUB_ID.getKey(), identity.getId())
            this.add(JwtClaim.AUTHORIZATION_DETAILS.getKey(), authorities)
            this.build()
        }

        return Jwts.builder().claims(claims).signWith(JwtAuthenticationReader.SIGNING_KEY).compact()
    }
}