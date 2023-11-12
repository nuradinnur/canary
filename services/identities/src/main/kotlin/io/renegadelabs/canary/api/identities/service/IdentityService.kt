package io.renegadelabs.canary.api.identities.service

import io.renegadelabs.canary.api.identities.component.ReactiveIdentityCache
import io.renegadelabs.canary.api.identities.component.ReactiveIdentityVerifier
import io.renegadelabs.canary.api.identities.domain.Identity
import io.renegadelabs.canary.api.identities.exception.IdentityAlreadyExistsException
import io.renegadelabs.canary.api.identities.exception.IdentityNotFoundException
import org.springframework.context.MessageSourceAware
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import reactor.core.publisher.Mono

/**
 * Defines operations allowing mutation and persistence of an identity.
 *
 * Inherits from {@link ReactiveUserDetailsService} and implementations should provide encapsulation of the
 * {@link UserDetails} class referenced in the superinterface, preferably by transforming it to an {@link Identity} in
 * a new public method.  This is not necessary, but allows a uniform domain object type for downstream callers to work
 * with.  <code>@Deprecation</code> tags can be used on the superinterface's method in order to somewhat enforce this.
 *
 * Implementations may consider using {@link ReactiveUserDetailsChecker#validate} in order to assert identity instances
 * that are persisted are consistent with a set of validation rules.  Additionally, {@link ReactiveUserDetailsCache}
 * may be used for performance.
 *
 * @version 1.0.0
 * @see     ReactiveIdentityCache
 * @see     ReactiveIdentityVerifier
 * @see     ReactiveUserDetailsService
 * @since   1.0.0
 */
interface IdentityService : ReactiveUserDetailsService, MessageSourceAware {

    /**
     * Creates an identity using the given credentials and persists it.
     *
     * @version 1.0.0
     * @param   username                        The created identity's username
     * @param   password                        The created identity's raw password
     * @throws  IdentityAlreadyExistsException  If the username already belongs to another identity
     * @since   1.0.0
     */
    fun createIdentity(username: String, password: String): Mono<Void>

    /**
     * Persists an updated version of an identity that already exists.  This is strictly intended on being an update
     * call - and not a create call.
     *
     * @version 1.0.0
     * @param   identity                    The mutated identity object
     * @throws  IdentityNotFoundException   If an identity with the given unique identifier does not exist
     * @since   1.0.0
     */
    fun updateIdentity(identity: Identity): Mono<Void>

    /**
     * Retrieves an identity by its unique identifier.
     *
     * @version 1.0.0
     * @param   id                          The identity's unique identifier
     * @return  The identity requested
     * @throws  IdentityNotFoundException   If an identity with the given unique identifier does not exist
     * @since   1.0.0
     */
    fun readIdentityById(id: Long): Mono<Identity>

    /**
     * Retrieves an identity by its username.
     *
     * @version 1.0.0
     * @param   username                    The identity's username
     * @return  The identity requested
     * @throws  IdentityNotFoundException   If an identity with the given unique identifier does not exist
     * @since   1.0.0
     */
    fun readIdentityByUsername(username: String): Mono<Identity>

    /**
     * Checks if an identity exists via its unique identifier.
     *
     * @version 1.0.0
     * @param   id                          The identity's unique identifier
     * @return  True if the identity exists, false otherwise
     * @since   1.0.0
     */
    fun existsIdentityById(id: Long): Mono<Boolean>

    /**
     * Checks if an identity exists belonging to the given username.
     *
     * @version 1.0.0
     * @param   username                    The identity's username
     * @return  True if the identity exists, false otherwise
     * @since   1.0.0
     */
    fun existsIdentityByUsername(username: String): Mono<Boolean>
}