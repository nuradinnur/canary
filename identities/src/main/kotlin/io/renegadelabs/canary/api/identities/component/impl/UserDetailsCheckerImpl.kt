package io.renegadelabs.canary.api.identities.component.impl

import io.renegadelabs.canary.api.identities.component.ReactiveUserDetailsChecker
import org.springframework.context.MessageSource
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UserDetailsCheckerImpl : ReactiveUserDetailsChecker {

    private lateinit var messages: MessageSourceAccessor

    override fun validate(userDetails: UserDetails): Mono<Void> {
        if (!userDetails.isAccountNonExpired) {
            return Mono.error(AccountExpiredException(this.messages.getMessage("exceptions.account-expired")))
        }
        if (!userDetails.isAccountNonLocked) {
            return Mono.error(LockedException(this.messages.getMessage("exceptions.locked")))
        }
        if (!userDetails.isCredentialsNonExpired) {
            return Mono.error(CredentialsExpiredException(this.messages.getMessage("exceptions.credentials-expired")))
        }
        if (!userDetails.isEnabled) {
            return Mono.error(DisabledException(this.messages.getMessage("exceptions.disabled")))
        }
        return Mono.empty()
    }

    override fun setMessageSource(messageSource: MessageSource) {
        this.messages = MessageSourceAccessor(messageSource)
    }
}