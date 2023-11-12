package io.renegadelabs.canary.api.identities.controller.request

data class CreateSessionRequest(
    val username: String,
    val password: String
)