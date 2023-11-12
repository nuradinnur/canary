package io.renegadelabs.canary.api.identities.component.impl

import io.renegadelabs.canary.api.identities.component.ReactiveIdentityCache
import io.renegadelabs.canary.api.identities.domain.Identity
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.set
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class IdentityCacheImpl(
    cacheManager: CacheManager
): ReactiveIdentityCache {

    private val cache: Cache = cacheManager.getCache("identity")!!

    override fun getIdentity(username: String): Mono<Identity> {
        return Mono.defer { this.cache[username, Identity::class.java].toMono() }
    }

    override fun putIdentity(identity: Identity): Mono<Void> {
        this.cache[identity.username!!] = identity
        return Mono.empty()
    }

    override fun evictIdentity(username: String): Mono<Boolean> {
        return this.cache.evictIfPresent(username).toMono()
    }
}
