package io.renegadelabs.canary.api.identities.service.impl

import io.renegadelabs.canary.api.identities.domain.Identity
import io.renegadelabs.canary.api.identities.service.IdentityPasswordService
import io.renegadelabs.canary.api.identities.service.IdentityService
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
class IdentityPasswordServiceImpl(
    private val passwordEncoder: PasswordEncoder,
    private val identityService: IdentityService
): IdentityPasswordService, MessageSourceAware {

    private lateinit var messages: MessageSourceAccessor

    override fun validatePassword(username: String, password: String): Mono<Void> {
        return this.identityService.getIdentityByUsername(username)
            .onErrorMap { error -> BadCredentialsException(this.messages.getMessage("exceptions.bad-credentials"), error) }
            .map { this.passwordEncoder.matches(password, it.password) }
            .filter { matches -> matches }
            .switchIfEmpty(Mono.error(BadCredentialsException(this.messages.getMessage("exceptions.bad-credentials"))))
            .then()
    }

    @Suppress("DEPRECATION")
    override fun updatePassword(username: String, password: String, newPassword: String): Mono<Identity> {
        val identity = this.identityService.getIdentityByUsername(username).cache()
        return identity.map { this.passwordEncoder.matches(password, it.password) }
            .filter { matches -> matches }
            .switchIfEmpty(Mono.error(BadCredentialsException(this.messages.getMessage("exceptions.bad-credentials"))))
            .then(identity.flatMap { this.updatePassword(it, newPassword).cast(Identity::class.java) })
    }

    @Deprecated(
        level = DeprecationLevel.WARNING,
        message = "Use updatePassword(identity, newPassword) instead to encapsulate UserDetails",
        replaceWith = ReplaceWith("updatePassword(identity, newPassword)")
    )
    override fun updatePassword(user: UserDetails, newPassword: String): Mono<UserDetails> {
        return Mono.just(user)
            .cast(Identity::class.java)
            .map { it.copy(password = this.passwordEncoder.encode(newPassword)) }
            .flatMap { identityService.updateIdentity(it).thenReturn(it) }
    }

    override fun setMessageSource(messageSource: MessageSource) {
        this.messages = MessageSourceAccessor(messageSource)
    }
}