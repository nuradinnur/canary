package io.renegadelabs.canary.api.identities.component

import io.renegadelabs.canary.api.identities.domain.Identity
import org.springframework.security.core.userdetails.UserCache
import reactor.core.publisher.Mono

/**
 * Defines operations allowing caching of an identity.
 *
 * @version 1.0.0
 * @see     UserCache
 * @since   1.0.0
 */
// TODO: explore using @Cacheable annotation instead
interface ReactiveIdentityCache {

    /**
     * Retrieves an {@link Identity} object from cache.
     *
     * @version 1.0.0
     * @param   username    The username to retrieve {@link Identity} for
     * @return  The {@link Identity} requested
     * @since   1.0.0
     */
    fun getIdentity(username: String): Mono<Identity>

    /**
     * Caches an {@link Identity} object for future use.
     *
     * @version 1.0.0
     * @param   identity    The {@link Identity} to cache
     * @since   1.0.0
     */
    fun putIdentity(identity: Identity): Mono<Void>

    /**
     * Evicts an {@link Identity} object from cache.
     *
     * @version 1.0.0
     * @param   username    The username of the {@link Identity} to evict from cache
     * @since   1.0.0
     */
    fun evictIdentity(username: String): Mono<Boolean>
}