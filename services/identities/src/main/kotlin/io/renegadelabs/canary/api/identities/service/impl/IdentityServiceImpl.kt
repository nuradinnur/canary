package io.renegadelabs.canary.api.identities.service.impl

import io.renegadelabs.canary.api.identities.component.ReactiveIdentityCache
import io.renegadelabs.canary.api.identities.component.ReactiveIdentityVerifier
import io.renegadelabs.canary.api.identities.domain.Identity
import io.renegadelabs.canary.api.identities.exception.IdentityAlreadyExistsException
import io.renegadelabs.canary.api.identities.exception.IdentityNotFoundException
import io.renegadelabs.canary.api.identities.repository.IdentityRepository
import io.renegadelabs.canary.api.identities.service.IdentityService
import io.renegadelabs.canary.api.shared.domain.Authority
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Primary
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
@Primary
class IdentityServiceImpl(
    private val identityRepository: IdentityRepository,
    private val reactiveIdentityCache: ReactiveIdentityCache,
    private val reactiveIdentityVerifier: ReactiveIdentityVerifier,
    private val passwordEncoder: PasswordEncoder
) : IdentityService {

    private lateinit var messages: MessageSourceAccessor

    // TODO: default identity creation is with setOf(Authority.USER), but removing that parameter causes recursion...
    override fun createIdentity(username: String, password: String): Mono<Void> {
        return this.createIdentity(username, password, setOf(Authority.USER))
    }

    override fun updateIdentity(identity: Identity): Mono<Void> {
        return this.existsIdentityByUsername(identity.username!!)
            .handle { exists, sink ->
                if (!exists) sink.error(
                    IdentityNotFoundException(this.messages.getMessage("exception.identity-not-found"))
                )
                else sink.next(exists)
            }
            .then(this.updateUserDetails(identity))
    }

    override fun readIdentityById(id: Long): Mono<Identity> {
        return this.identityRepository.findById(id)
            .switchIfEmpty(Mono.error(IdentityNotFoundException(this.messages.getMessage("exception.identity-not-found"))))
    }

    override fun readIdentityByUsername(username: String): Mono<Identity> {
        return this.identityRepository.findByUsername(username)
    }

    override fun existsIdentityById(id: Long): Mono<Boolean> {
        return this.identityRepository.existsById(id)
    }

    override fun existsIdentityByUsername(username: String): Mono<Boolean> {
        return this.identityRepository.existsByUsername(username)
    }

    @Deprecated(
        level = DeprecationLevel.ERROR,
        message = "Use getIdentityByUsername(username: String) instead to encapsulate UserDetails",
        replaceWith = ReplaceWith("getIdentityByUsername(username)")
    )
    @PreAuthorize("denyAll()")
    override fun findByUsername(username: String): Mono<UserDetails> {
        throw UnsupportedOperationException("Use getIdentityByUsername(username: String) instead to encapsulate UserDetails")
    }

    @PreAuthorize("denyAll()")
    override fun setMessageSource(messageSource: MessageSource) {
        this.messages = MessageSourceAccessor(messageSource)
    }

    private fun createIdentity(username: String, password: String, authorities: Set<Authority> = setOf(Authority.USER)): Mono<Void> {
        return this.existsIdentityByUsername(username)
            .handle { exists, sink ->
                if (exists) sink.error(IdentityAlreadyExistsException(
                    this.messages.getMessage("exception.identity-already-exists")))
                else sink.next(exists)
            }
            .map { Identity.create(
                username = username,
                password = passwordEncoder.encode(password),
                authorities = authorities) }
            .flatMap {
                this.updateUserDetails(it)
            }
    }

    private fun updateUserDetails(identity: Identity): Mono<Void> {
        return this.reactiveIdentityVerifier.verifyIdentity(identity)
            .then(this.identityRepository.save(identity))
            .then(this.reactiveIdentityCache.putIdentity(identity))
    }
}