package io.renegadelabs.canary.api.identities.service

import org.springframework.context.MessageSourceAware
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService
import reactor.core.publisher.Mono

/**
 * Defines operations allowing mutation and persistence of an identity's password.
 *
 * Inherits from {@link ReactiveUserDetailsPasswordService}. Implementations should provide encapsulation of the
 * {@link UserDetails} class referenced in the superinterface, preferably by transforming it to an {@link Identity} in
 * a new public method.  This is not necessary, but allows a uniform domain object type for downstream callers to work
 * with.  <code>@Deprecation</code> tags can be used on the superinterface's method in order to somewhat enforce this.
 *
 * @version 1.0.0
 * @see     ReactiveUserDetailsPasswordService
 * @since   1.0.0
 */
interface IdentityPasswordService : ReactiveUserDetailsPasswordService, MessageSourceAware {

    /**
     * Validates the given credentials against an existing identity.  On success, changes the password associated with
     * the identity and persists it.  Should call {@link ReactiveUserDetailsPasswordService#updatePassword} internally
     * in order to encapsulate the returned {@link UserDetails}.
     *
     * @version 1.0.0
     * @param   username                The current username to authenticate against
     * @param   password                The raw current password to authenticate against
     * @param   newPassword             A new password assigned to the identity if authentication is successful
     * @throws  BadCredentialsException If the validation of credentials failed for any reason, including the username
     *                                  not belonging to any persisted identity.  The exception thrown should
     *                                  encapsulate exceptions thrown while calling upstream services to prevent
     *                                  credential enumeration attacks
     * @since   1.0.0
     */
    fun updatePassword(username: String, password: String, newPassword: String): Mono<Void>

    /**
     * Validates a given password against the password belonging to a persisted identity with the given username.
     * Should be used as part of the authentication process in order to confirm a user's identity.
     *
     * @version 1.0.0
     * @param   username                The username to authenticate against
     * @param   password                The raw password to authenticate against
     * @throws  BadCredentialsException If password validation failed for any reason, including the username not
     *                                  belonging to any persisted identity.  The exception thrown should encapsulate
     *                                  exceptions thrown while calling upstream services to prevent credential
     *                                  enumeration attacks
     * @since   1.0.0
     */
    fun validatePassword(username: String, password: String): Mono<Void>
}
