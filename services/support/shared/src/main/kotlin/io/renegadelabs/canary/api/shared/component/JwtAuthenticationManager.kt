package io.renegadelabs.canary.api.shared.component

import io.renegadelabs.canary.api.shared.domain.JwtAuthentication
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceAware
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationManager(
    private val jwtAuthenticationReader: JwtAuthenticationReader,
): ReactiveAuthenticationManager, MessageSourceAware {

    private lateinit var messages: MessageSourceAccessor

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.fromCallable {
            when (authentication) {
                is JwtAuthentication -> {
                    this.jwtAuthenticationReader.verifyClaims(authentication)
                    authentication
                }
                is AnonymousAuthenticationToken -> authentication
                else -> throw InternalAuthenticationServiceException(this.messages.getMessage("exception.invalid-authentication-type"))
            }
        }
    }

    override fun setMessageSource(messageSource: MessageSource) {
        this.messages = MessageSourceAccessor(messageSource)
    }
}