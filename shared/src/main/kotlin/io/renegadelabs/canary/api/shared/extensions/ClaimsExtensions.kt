package io.renegadelabs.canary.api.shared.extensions

import io.jsonwebtoken.Claims
import io.renegadelabs.canary.api.shared.domain.Authority
import io.renegadelabs.canary.api.shared.util.JsonWebTokenUtils
import java.time.Instant
import java.util.*

fun Claims.getAuthorities(): Set<Authority> {
    return this.get("authorities", ArrayList::class.java)
        .map { it.toString() }
        .map { Authority.valueOf(it) }
        .toSet()
}

fun Claims.isExpired(): Boolean {
    return this.expiration.before(Date.from(Instant.now().plus(JsonWebTokenUtils.CLOCK_SKEW_TOLERANCE)))
}

fun Claims.validateSessionClaims(): Boolean {
    return !this.isExpired() && !this.getAuthorities().contains(Authority.REFRESH)
}

fun Claims.validateRefreshClaims(): Boolean {
    val authorities = this.getAuthorities()
    return !this.isExpired() && authorities.size == 1 && authorities.contains(Authority.REFRESH)
}
