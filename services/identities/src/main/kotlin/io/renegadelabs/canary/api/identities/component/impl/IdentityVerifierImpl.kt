package io.renegadelabs.canary.api.identities.component.impl

import io.renegadelabs.canary.api.identities.component.ReactiveIdentityVerifier
import io.renegadelabs.canary.api.identities.domain.Identity
import org.springframework.context.MessageSource
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class IdentityVerifierImpl : ReactiveIdentityVerifier {

    private lateinit var messages: MessageSourceAccessor

    override fun verifyIdentity(identity: Identity): Mono<Void> {
        val that = this
        with(identity) {
            if (isAccountExpired())
                return Mono.error(AccountExpiredException(that.messages.getMessage("exception.account-expired")))
            if (isAccountLocked())
                return Mono.error(LockedException(that.messages.getMessage("exception.locked")))
            if (hasExpiredCredentials())
                return Mono.error(CredentialsExpiredException(that.messages.getMessage("exception.credentials-expired")))
            if (isDisabled())
                return Mono.error(DisabledException(that.messages.getMessage("exception.disabled")))
            return Mono.empty()
        }
    }

    override fun setMessageSource(messageSource: MessageSource) {
        this.messages = MessageSourceAccessor(messageSource)
    }
}