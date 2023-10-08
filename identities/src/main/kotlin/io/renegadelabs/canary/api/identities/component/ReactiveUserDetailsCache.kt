package io.renegadelabs.canary.api.identities.component

import org.springframework.security.core.userdetails.UserCache
import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono

/**
 * Defines operations allowing caching of an identity.
 *
 * This component should be used
 *
 * @version 1.0.0
 * @see     UserCache
 * @since   1.0.0
 */
interface ReactiveUserDetailsCache {

    /**
     * Retrieves a {@link UserDetails} object from cache.
     *
     * @version 1.0.0
     * @param   username    The username to retrieve {@link UserDetails} for
     * @return  The {@link UserDetails} requested
     * @since   1.0.0
     */
    fun getUserDetails(username: String): Mono<UserDetails>

    /**
     * Caches a {@link UserDetails} object for future use.
     *
     * @version 1.0.0
     * @param   userDetails    The {@link UserDetails} to cache
     * @since   1.0.0
     */
    fun putUserDetails(userDetails: UserDetails): Mono<Void>

    /**
     * Evicts a {@link UserDetails} object from cache.
     *
     * @version 1.0.0
     * @param   username    The username of the {@link UserDetails} to evict from cache
     * @since   1.0.0
     */
    fun removeUserDetails(username: String): Mono<Boolean>
}