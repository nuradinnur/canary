package io.renegadelabs.canary.api.identities.service.impl

import io.renegadelabs.canary.api.identities.service.IdentityPasswordService
import io.renegadelabs.canary.api.identities.service.IdentityService
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceAware
import org.springframework.context.annotation.Primary
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
@Primary
class IdentityPasswordServiceImpl(
    private val identityService: IdentityService,
    private val passwordEncoder: PasswordEncoder
) : IdentityPasswordService, MessageSourceAware {

    lateinit var messages: MessageSourceAccessor

    override fun updatePassword(username: String, password: String, newPassword: String): Mono<Void> {
        return this.validatePassword(username, password)
            .then(this.identityService.readIdentityByUsername(username))
            .map { it.copy(password = this.passwordEncoder.encode(newPassword)) }
            .flatMap { this.identityService.updateIdentity(it) }
            .then()

    }

    override fun validatePassword(username: String, password: String): Mono<Void> {
        return this.identityService.readIdentityByUsername(username)
            .onErrorMap { BadCredentialsException(this.messages.getMessage("exception.bad-credentials"), it) }
            .map { this.passwordEncoder.matches(password, it.password) }
            .handle { matches, sink ->
                if (!matches) sink.error(
                    BadCredentialsException(this.messages.getMessage("exception.bad-credentials"))
                )
                else sink.complete()
            }
    }

    // TODO: throw UnsupportedOperationException in all @Deprecated code
    // TODO: replace @Deprecated with @DeprecatedSinceKotlin and @ReplaceWith
    @Deprecated(
        level = DeprecationLevel.ERROR,
        message = "Use updatePassword(identity: Identity, newPassword: String) instead to encapsulate UserDetails",
        replaceWith = ReplaceWith("updatePassword(identity, newPassword)")
    )
    @PreAuthorize("denyAll()")
    override fun updatePassword(user: UserDetails, newPassword: String): Mono<UserDetails> {
        throw UnsupportedOperationException("Use updatePassword(identity: Identity, newPassword: String) instead to encapsulate UserDetails")
    }

    override fun setMessageSource(messageSource: MessageSource) {
        this.messages = MessageSourceAccessor(messageSource)
    }
}