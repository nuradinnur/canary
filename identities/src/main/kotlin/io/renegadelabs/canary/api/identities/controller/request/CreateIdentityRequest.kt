package io.renegadelabs.canary.api.identities.controller.request

data class CreateIdentityRequest(
    val username: String,
    val password: String
)
