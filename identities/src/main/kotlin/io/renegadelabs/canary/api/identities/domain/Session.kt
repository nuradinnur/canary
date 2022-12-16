package io.renegadelabs.canary.api.identities.domain

data class Session(
    val accessToken: String,
    val refreshToken: String
)