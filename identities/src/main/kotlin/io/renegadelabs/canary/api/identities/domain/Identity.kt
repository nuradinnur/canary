package io.renegadelabs.canary.api.identities.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import io.renegadelabs.canary.api.shared.domain.Authorities
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * Represents a user's identity.
 *
 * @version 1.0.0
 * @param   id                  A unique identifier for the id
 * @param   username            The created identity's username
 * @param   password            The created identity's raw password
 * @param   authorities         The created identity's granted authorities
 * @param   expired             Whether the identity is expired
 * @param   locked              Whether the identity is locked
 * @param   credentialsExpired  Whether the credentials for the identity are expired
 * @param   enabled             Whether the identity is enabled
 * @see     UserDetails
 * @since   1.0.0
 */
data class Identity(
    private val id: Long,
    private val username: String,
    private val password: String,
    private val authorities: Set<Authorities>,
    private val expired: Boolean,
    private val locked: Boolean,
    private val credentialsExpired: Boolean,
    private val enabled: Boolean,
) : UserDetails {

    companion object {

        /**
         * Creates an identity object with the given properties.
         *
         * @version 1.0.0
         * @param   id                  A unique identifier for the id
         * @param   username            The created identity's username
         * @param   password            The created identity's raw password
         * @param   authorities         The created identity's granted authorities
         * @param   expired             Whether the identity is expired
         * @param   locked              Whether the identity is locked
         * @param   credentialsExpired  Whether the credentials for the identity are expired
         * @param   enabled             Whether the identity is enabled
         * @see     UserDetails
         * @since 1.0.0
         */
        fun create(
            id: Long = 0,
            username: String,
            password: String,
            authorities: Set<Authorities> = HashSet(),
            expired: Boolean = false,
            locked: Boolean = false,
            credentialsExpired: Boolean = false,
            enabled: Boolean = true
        ): Identity {

            return Identity(
                id = id,
                username = username,
                password = password,
                authorities = authorities,
                expired = expired,
                locked = locked,
                credentialsExpired = credentialsExpired,
                enabled = enabled
            )
        }
    }

    fun getId(): Long {
        return this.id
    }

    override fun getUsername(): String {
        return this.username
    }

    @JsonIgnore
    override fun getPassword(): String {
        return this.password
    }

    override fun getAuthorities(): Set<GrantedAuthority> {
        return this.authorities
    }

    @JsonIgnore
    @Suppress("DEPRECATION")
    fun isAccountExpired(): Boolean {
        return !this.isAccountNonExpired
    }

    @JsonIgnore
    @Deprecated(
        level = DeprecationLevel.WARNING,
        message = "Use !isAccountExpired() instead to encapsulate UserDetails",
        replaceWith = ReplaceWith("!isAccountExpired()")
    )
    override fun isAccountNonExpired(): Boolean {
        return !this.expired
    }

    @JsonIgnore
    @Suppress("DEPRECATION")
    fun isAccountLocked(): Boolean {
        return !this.isAccountNonLocked
    }

    @JsonIgnore
    @Deprecated(
        level = DeprecationLevel.WARNING,
        message = "Use !isAccountNonLocked() instead to encapsulate UserDetails",
        replaceWith = ReplaceWith("!isAccountNonLocked()")
    )
    override fun isAccountNonLocked(): Boolean {
        return !this.locked
    }

    @JsonIgnore
    @Suppress("DEPRECATION")
    fun hasExpiredCredentials(): Boolean {
        return !this.isCredentialsNonExpired
    }

    @JsonIgnore
    @Deprecated(
        level = DeprecationLevel.WARNING,
        message = "Use !hasExpiredCredentials() instead to encapsulate UserDetails",
        replaceWith = ReplaceWith("!hasExpiredCredentials()")
    )
    override fun isCredentialsNonExpired(): Boolean {
        return !this.credentialsExpired
    }

    @JsonIgnore
    override fun isEnabled(): Boolean {
        return this.enabled
    }
}