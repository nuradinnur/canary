package io.renegadelabs.canary.api.identities.domain

/**
 * A pair of tokens that provides a short-lived access token to consume protected endpoints as well as a refresh token
 * that can be used to refresh the access token.
 *
 * @version 1.0.0
 * @param   accessToken     A unique identifier for the id
 * @param   refreshToken    The created identity's username
 * @since   1.0.0
 */
data class TokenPair(
    val accessToken: String,
    val refreshToken: String
)