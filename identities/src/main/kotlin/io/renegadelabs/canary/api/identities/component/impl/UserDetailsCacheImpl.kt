package io.renegadelabs.canary.api.identities.component.impl

import io.renegadelabs.canary.api.identities.component.ReactiveUserDetailsCache
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.set
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class UserDetailsCacheImpl(
    cacheManager: CacheManager,
) : ReactiveUserDetailsCache {

    private val cache: Cache = cacheManager.getCache("userDetails")!!

    override fun getUserDetails(username: String): Mono<UserDetails> {
        return this.cache[username, UserDetails::class.java].toMono()
    }

    override fun putUserDetails(userDetails: UserDetails): Mono<Void> {
        this.cache[userDetails.username] = userDetails
        return Mono.empty()
    }

    override fun removeUserDetails(username: String): Mono<Boolean> {
        return this.cache.evictIfPresent(username).toMono()
    }
}