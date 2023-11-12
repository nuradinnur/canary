package io.renegadelabs.canary.api.identities.service

import io.renegadelabs.canary.api.identities.domain.TokenPair
import org.springframework.context.MessageSourceAware
import org.springframework.security.authentication.BadCredentialsException
import reactor.core.publisher.Mono

/**
 * Defines operations allowing the creation and refreshing of stateless access tokens.
 *
 * @version 1.0.0
 * @since   1.0.0
 */
interface JwtAuthenticationService : MessageSourceAware {

    /**
     * Authenticates the given credentials against an identity.  On success, creates a pair of tokens where the access
     * token allows short-duration consumption of protected endpoints on behalf of an identity while the refresh token
     * allows creating new access tokens.
     *
     * In other words, this is an authentication entrypoint.
     *
     * @version 1.0.0
     * @param   username                    The username to authenticate
     * @param   password                    The raw password to authenticate
     * @return  A pair of tokens allowing access to protected endpoints
     * @throws  BadCredentialsException     If the validation of credentials failed for any reason, including the
     *                                      username not belonging to any persisted identity.  The exception thrown
     *                                      should encapsulate exceptions thrown while calling upstream services to
     *                                      prevent credential enumeration attacks
     * @since   1.0.0
     */
    fun createJwtAuthentication(username: String, password: String): Mono<TokenPair>


    /**
     * Validates the given refresh token.  On success, returns the originally given refresh token in addition to a
     * newly created access token.
     *
     * @version 1.0.0
     * @param   refreshToken                A refresh token to create an access token with
     * @return  A pair of tokens allowing access to protected endpoints
     * @throws  BadCredentialsException     If the username already belongs to another identity
     * @since   1.0.0
     */
    fun refreshTokenPair(refreshToken: String): Mono<TokenPair>
}