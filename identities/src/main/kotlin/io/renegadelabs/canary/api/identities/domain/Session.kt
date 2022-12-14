package io.renegadelabs.canary.api.identities.domain

data class Session(
    private val accessToken: String?,
    private var refreshToken: String?
)