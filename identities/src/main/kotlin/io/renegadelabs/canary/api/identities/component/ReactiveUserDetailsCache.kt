package io.renegadelabs.canary.api.identities.component

import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono

/**
 * @see org.springframework.security.core.userdetails.UserCache
 */
interface ReactiveUserDetailsCache {

    fun getUserDetails(username: String): Mono<UserDetails>

    fun putUserDetails(user: UserDetails): Mono<Void>

    fun removeUserDetails(username: String): Mono<Void>
}