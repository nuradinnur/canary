package io.renegadelabs.canary.api.shared.domain

import io.jsonwebtoken.Claims

enum class JwtClaim(
    private val key: String,
    private val humanReadableName: String
) {
    // Registered JWT claim names
    ID(Claims.ID, "ID"),
    ISSUER(Claims.ISSUER, "issuer"),
    AUDIENCE(Claims.AUDIENCE, "audience"),
    ISSUED_AT(Claims.ISSUED_AT, "issued at"),
    NOT_BEFORE(Claims.NOT_BEFORE, "not before"),
    EXPIRATION(Claims.EXPIRATION, "expiration"),
    SUBJECT(Claims.SUBJECT, "subject"),

    // Public claim names
    SUB_ID("sub_id", "subject ID"),
    AUTHORIZATION_DETAILS("authorization_details", "authorization details");

    fun getKey(): String {
        return this.key
    }

    fun getHumanReadableName(): String {
        return this.humanReadableName
    }
}