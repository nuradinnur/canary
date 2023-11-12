package io.renegadelabs.canary.api.shared.component

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.IncorrectClaimException
import io.jsonwebtoken.MissingClaimException
import io.jsonwebtoken.PrematureJwtException
import io.jsonwebtoken.security.Keys
import io.renegadelabs.canary.api.shared.domain.Authority
import io.renegadelabs.canary.api.shared.domain.JwtAuthentication
import io.renegadelabs.canary.api.shared.domain.JwtClaim
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceAware
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtAuthenticationReader : MessageSourceAware {

    private lateinit var messages: MessageSourceAccessor

    companion object {
        // TODO: add to configuration properties
        const val BEARER_TOKEN_PREFIX: String = "Bearer"
        const val BEARER_TOKEN_PREFIX_WITH_SEPARATOR: String = BEARER_TOKEN_PREFIX.plus(" ")
        val CLOCK_SKEW_TOLERANCE: Duration = Duration.ofSeconds(1)
        // TODO: store in kubernetes secret
        val SIGNING_KEY: SecretKey = Keys.hmacShaKeyFor(
            "TElDeWs9ME0xdCMtV05WT2J4d0FAIyMxTVBtUHJLPy56ezx6dU1Uez98NU5HeDUkRGY8LFhWUVc0OzN6anQ0".toByteArray())
    }

    fun getPrincipalAsUsername(authentication: Authentication): String {
        val that = this
        with(authentication) {
            if (this is AnonymousAuthenticationToken)
                throw BadCredentialsException(that.messages.getMessage("exception.bad-credentials"))
            if (this is JwtAuthentication)
                return this.getSubject()!!
            throw BadCredentialsException(that.messages.getMessage("exception.bad-credentials"))
        }
    }

    fun verifyClaims(jwtAuthentication: JwtAuthentication) {
        val that = this
        with(jwtAuthentication) {
            for (claim in JwtClaim.values()) {
                when (claim) {
                    JwtClaim.ISSUER ->
                        if (this.getIssuer().isNullOrBlank())
                            that.throwMissingClaimException(this, claim)
                        else if (this.getIssuer() != "identities")
                            that.throwIncorrectClaimException(this, claim)
                    JwtClaim.SUBJECT ->
                        if (this.getSubject().isNullOrBlank())
                            that.throwMissingClaimException(this, claim)
                    JwtClaim.AUDIENCE ->
                        if (this.getAudience().isNullOrEmpty())
                            that.throwMissingClaimException(this, claim)
                        else if (!this.getAudience()!!.contains("canary"))
                            that.throwIncorrectClaimException(this, claim)
                    JwtClaim.EXPIRATION ->
                        if (this.getExpiration() == null)
                            that.throwMissingClaimException(this, claim)
                        else if (this.getExpiration()!!.before(Date.from(Instant.now())))
                            that.throwExpiredJwtException(this)
                    JwtClaim.NOT_BEFORE ->
                        if (this.getNotBefore() == null)
                            that.throwMissingClaimException(this, claim)
                        else if (this.getNotBefore()!!.after(Date.from(Instant.now())))
                            that.throwPrematureJwtException(this)
                    JwtClaim.ISSUED_AT ->
                        if (this.getIssuedAt() == null)
                            that.throwMissingClaimException(this, claim)
                        else if (this.getIssuedAt()!!.after(Date.from(Instant.now())))
                            that.throwPrematureJwtException(this)
                    JwtClaim.ID ->
                        if (this.getId().isNullOrBlank())
                            that.throwMissingClaimException(this, claim)
                        else try { 
                            UUID.fromString(this.getId()!!)
                        } catch (e: IllegalArgumentException) { 
                            that.throwIncorrectClaimException(this, claim) 
                        }
                    JwtClaim.SUB_ID ->
                        if (this.getSubId() == null)
                            that.throwMissingClaimException(this, claim)
                        else if (this.getSubId()!! < 0)
                            that.throwIncorrectClaimException(this, claim)
                    JwtClaim.AUTHORIZATION_DETAILS ->
                        if (this.getAuthorizationDetails().isEmpty())
                            that.throwMissingClaimException(this, claim)
                        else if (!this.getAuthorizationDetails().contains(Authority.USER) &&
                            !this.getAuthorizationDetails().contains(Authority.ADMINISTRATOR) &&
                            !this.getAuthorizationDetails().contains(Authority.REFRESH)) {
                            that.throwIncorrectClaimException(this, claim)
                        }

                }
            }
        }
    }

    private fun throwPrematureJwtException(jwtAuthentication: JwtAuthentication) {
        val that = this
        val prematureUntil = if (jwtAuthentication.getNotBefore()!!.after(jwtAuthentication.getIssuedAt()))
            jwtAuthentication.getNotBefore() else jwtAuthentication.getIssuedAt()
        throw PrematureJwtException(
            jwtAuthentication.getJwtHeader(),
            jwtAuthentication.getJwtPayload(),
            that.messages.getMessage("exception.premature-jwt", arrayOf(prematureUntil)))
    }

    private fun throwExpiredJwtException(jwtAuthentication: JwtAuthentication) {
        throw ExpiredJwtException(
            jwtAuthentication.getJwtHeader(),
            jwtAuthentication.getJwtPayload(),
            this.messages.getMessage("exception.expired-jwt"))
    }

    private fun throwIncorrectClaimException(jwtAuthentication: JwtAuthentication, jwtClaim: JwtClaim) {
        throw IncorrectClaimException(
            jwtAuthentication.getJwtHeader(),
            jwtAuthentication.getJwtPayload(),
            jwtClaim.getKey(),
            jwtAuthentication.getJwtPayload()[jwtClaim.getKey()],
            this.messages.getMessage("exception.incorrect-claim", arrayOf(jwtClaim.getHumanReadableName())))
    }

    private fun throwMissingClaimException(jwtAuthentication: JwtAuthentication, jwtClaim: JwtClaim) {
        throw MissingClaimException(
            jwtAuthentication.getJwtHeader(),
            jwtAuthentication.getJwtPayload(),
            jwtClaim.getKey(),
            jwtAuthentication.getJwtPayload()[jwtClaim.getKey()],
            this.messages.getMessage("exception.missing-claim", arrayOf(jwtClaim.getHumanReadableName())))
    }

    override fun setMessageSource(messageSource: MessageSource) {
        this.messages = MessageSourceAccessor(messageSource)
    }
}
