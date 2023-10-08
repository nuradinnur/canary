package io.renegadelabs.canary.api.identities.service.impl

import io.renegadelabs.canary.api.identities.component.ReactiveUserDetailsCache
import io.renegadelabs.canary.api.identities.component.ReactiveUserDetailsChecker
import io.renegadelabs.canary.api.identities.domain.Identity
import io.renegadelabs.canary.api.identities.exception.IdentityAlreadyExistsException
import io.renegadelabs.canary.api.identities.exception.IdentityNotFoundException
import io.renegadelabs.canary.api.identities.service.IdentityService
import io.renegadelabs.canary.api.shared.domain.Authorities
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Primary
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
@Primary
class IdentityServiceImpl(
    private val reactiveUserDetailsCache: ReactiveUserDetailsCache,
    private val reactiveUserDetailsChecker: ReactiveUserDetailsChecker,
    private val passwordEncoder: PasswordEncoder
) : IdentityService {

    private lateinit var messages: MessageSourceAccessor

    private val database: MutableMap<String, Identity> = HashMap()

    init {
        // TODO("Create system identity on first launch")
    }

    override fun createIdentity(username: String, password: String): Mono<Void> {
        return this.database.containsKey(username).toMono().handle<Identity> { exists, sink ->
            if (exists) sink.error(
                IdentityAlreadyExistsException(this.messages.getMessage("exceptions.identity-already-exists"))
            )
            else sink.next(
                Identity.create(
                    id = this.database.size.toLong(),
                    username = username,
                    password = passwordEncoder.encode(password),
                    authorities = setOf(Authorities.USER)
                )
            )
        }.flatMap { this.updateUserDetails(it) }
    }

    override fun getIdentityById(id: Long): Mono<Identity> {
        return Mono.justOrEmpty(this.database.firstNotNullOfOrNull {
            it.value.takeIf { identity -> identity.getId() == id }
        })
            .switchIfEmpty(Mono.error(IdentityNotFoundException(this.messages.getMessage("exceptions.identity-not-found"))))
    }

    @Suppress("DEPRECATION")
    override fun getIdentityByUsername(username: String): Mono<Identity> {
        return this.findByUsername(username).cast(Identity::class.java)

    }

    override fun updateIdentity(identity: Identity): Mono<Void> {
        return this.database.containsKey(identity.username).toMono().handle<Identity> { exists, sink ->
            if (!exists) sink.error(
                IdentityNotFoundException(this.messages.getMessage("exceptions.identity-not-found"))
            )
            else sink.next(
                identity
            )
        }.flatMap { this.updateUserDetails(it) }
    }

    @Deprecated(
        level = DeprecationLevel.WARNING,
        message = "Use getIdentityByUsername(username) instead to encapsulate UserDetails",
        replaceWith = ReplaceWith("getIdentityByUsername(username)")
    )
    override fun findByUsername(username: String): Mono<UserDetails> {
        return this.reactiveUserDetailsCache.getUserDetails(username)
            .switchIfEmpty(Mono.defer { this.database[username].toMono() }).switchIfEmpty(
                Mono.error(
                    IdentityNotFoundException(
                        this.messages.getMessage(
                            "exceptions.identity-not-found", arrayOf(username)
                        )
                    )
                )
            ).flatMap { userDetails ->
                this.reactiveUserDetailsCache.putUserDetails(userDetails).then(Mono.just(userDetails))
            }
    }

    override fun setMessageSource(messageSource: MessageSource) {
        this.messages = MessageSourceAccessor(messageSource)
    }

    private fun updateUserDetails(userDetails: UserDetails): Mono<Void> {
        return this.reactiveUserDetailsChecker.validate(userDetails)
            .then(Mono.defer { this.database.put(userDetails.username, userDetails as Identity).toMono() })
            .then(this.reactiveUserDetailsCache.putUserDetails(userDetails))
    }
}