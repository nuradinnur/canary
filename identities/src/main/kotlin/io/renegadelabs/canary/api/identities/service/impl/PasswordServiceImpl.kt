package io.renegadelabs.canary.api.identities.service.impl

import io.renegadelabs.canary.api.identities.domain.User
import io.renegadelabs.canary.api.identities.service.PasswordService
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceAware
import org.springframework.context.annotation.Primary
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
@Primary
class PasswordServiceImpl(
    private val passwordEncoder: PasswordEncoder
): PasswordService, MessageSourceAware {

    private lateinit var messages: MessageSourceAccessor

    override fun validatePassword(userDetails: UserDetails, password: String): Mono<Void> {
        return if (this.passwordEncoder.matches(password, userDetails.password))
            Mono.empty()
        else
            Mono.error(BadCredentialsException(this.messages.getMessage("exceptions.bad-credentials")))
    }

    override fun updatePassword(user: UserDetails, newPassword: String): Mono<UserDetails> {
        return Mono.just(
            User.create(
                username = user.username,
                password = passwordEncoder.encode(newPassword),
                authorities = user.authorities.toSet(),
                expired = user.isAccountNonExpired,
                locked = user.isAccountNonLocked,
                passwordExpired = user.isCredentialsNonExpired,
                enabled = user.isEnabled
            )).cast(UserDetails::class.java)
    }

    override fun setMessageSource(messageSource: MessageSource) {
        this.messages = MessageSourceAccessor(messageSource)
    }
}