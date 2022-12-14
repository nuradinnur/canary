package io.renegadelabs.canary.api.shared.extensions

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.renegadelabs.canary.api.shared.domain.Authorities
import io.renegadelabs.canary.api.shared.util.JwsUtils
import org.springframework.security.core.GrantedAuthority
import java.time.Instant
import java.util.*

fun Jws<Claims>.getAuthorities(): Set<GrantedAuthority> {
    return this.body.get("authorities", ArrayList::class.java)
        .map { it.toString() }
        .map { Authorities.valueOf(it) }
        .toSet()
}

fun Jws<Claims>.isExpired(): Boolean {
    return this.body.expiration.before(Date.from(Instant.now().plus(JwsUtils.CLOCK_SKEW_TOLERANCE)))
}

fun Jws<Claims>.hasValidSessionClaims(): Boolean {
    return !this.isExpired() && !this.getAuthorities().contains(Authorities.REFRESH)
}

fun Jws<Claims>.hasValidRefreshClaims(): Boolean {
    val authorities = this.getAuthorities()
    return !this.isExpired() && authorities.size == 1 && authorities.contains(Authorities.REFRESH)
}
