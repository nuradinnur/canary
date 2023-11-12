package io.renegadelabs.canary.api.identities.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import io.renegadelabs.canary.api.shared.domain.Authority
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.userdetails.UserDetails

/**
 * Represents a user's identity.
 *
 * @version 1.0.0
 * @param   id                  A unique identifier for the id
 * @param   username            The created identity's username
 * @param   password            The created identity's hashed password
 * @param   authorities         The created identity's granted authorities
 * @param   expired             Whether the identity is expired
 * @param   locked              Whether the identity is locked
 * @param   credentialsExpired  Whether the credentials for the identity are expired
 * @param   disabled            Whether the identity is enabled
 * @see     UserDetails
 * @since   1.0.0
 */
@Table
data class Identity(
    @Id
    private val id: Long,
    // TODO: identity creation date as timestamp
    private val username: String?,
    private val password: String?,
    private val authorities: Set<Authority>,
    private val expired: Boolean,
    private val locked: Boolean,
    private val credentialsExpired: Boolean,
    private val disabled: Boolean
) : UserDetails {

    companion object {

        /**
         * Creates an identity object with the given properties.
         *
         * @version 1.0.0
         * @param   id                  A unique identifier for the id
         * @param   username            The created identity's username
         * @param   password            The created identity's hashed password
         * @param   authorities         The created identity's granted authorities
         * @param   expired             Whether the identity is expired
         * @param   locked              Whether the identity is locked
         * @param   credentialsExpired  Whether the credentials for the identity are expired
         * @param   disabled            Whether the identity is disabled
         * @see     UserDetails
         * @since 1.0.0
         */
        fun create(
            id: Long = 0,
            username: String,
            password: String = "",
            authorities: Set<Authority> = HashSet(),
            expired: Boolean = false,
            locked: Boolean = false,
            credentialsExpired: Boolean = false,
            disabled: Boolean = false
        ): Identity {

            return Identity(
                id,
                username,
                password,
                authorities,
                expired,
                locked,
                credentialsExpired,
                disabled
            )
        }
    }

    fun getId(): Long {
        return this.id
    }

    override fun getUsername(): String? {
        return this.username
    }

    @JsonIgnore
    override fun getPassword(): String? {
        return this.password
    }

    override fun getAuthorities(): Set<Authority> {
        return this.authorities
    }

    @JsonIgnore
    fun isAccountExpired(): Boolean {
        return this.expired
    }

    @JsonIgnore
    @Deprecated(
        level = DeprecationLevel.ERROR,
        message = "Use !isAccountExpired() instead to encapsulate UserDetails",
        replaceWith = ReplaceWith("!isAccountExpired()")
    )
    override fun isAccountNonExpired(): Boolean {
        throw UnsupportedOperationException("Use !isAccountExpired() instead to encapsulate UserDetails")
    }

    @JsonIgnore
    fun isAccountLocked(): Boolean {
        return this.locked
    }

    @JsonIgnore
    @Deprecated(
        level = DeprecationLevel.ERROR,
        message = "Use !isAccountLocked() instead to encapsulate UserDetails",
        replaceWith = ReplaceWith("!isAccountLocked()")
    )
    override fun isAccountNonLocked(): Boolean {
        throw UnsupportedOperationException("Use !isAccountLocked() instead to encapsulate UserDetails")
    }

    @JsonIgnore
    fun hasExpiredCredentials(): Boolean {
        return this.credentialsExpired
    }

    @JsonIgnore
    @Deprecated(
        level = DeprecationLevel.ERROR,
        message = "Use !hasExpiredCredentials() instead to encapsulate UserDetails",
        replaceWith = ReplaceWith("!hasExpiredCredentials()")
    )
    override fun isCredentialsNonExpired(): Boolean {
        throw UnsupportedOperationException("Use !hasExpiredCredentials() instead to encapsulate UserDetails")
    }

    @JsonIgnore
    @Deprecated(
        level = DeprecationLevel.ERROR,
        message = "Use !isDisabled() instead to encapsulate UserDetails",
        replaceWith = ReplaceWith("!isDisabled()")
    )
    override fun isEnabled(): Boolean {
        throw UnsupportedOperationException("Use !isDisabled() instead to encapsulate UserDetails")
    }


    @JsonIgnore
    fun isDisabled(): Boolean {
        return this.disabled
    }
}
