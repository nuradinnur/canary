package io.renegadelabs.canary.api.identities.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class Identity(
    val id: Long,
    private val username: String,
    @JsonIgnore private val password: String,
    private val authorities: Set<GrantedAuthority>,
    val expired: Boolean,
    val locked: Boolean,
    val credentialsExpired: Boolean,
    val enabled: Boolean,
) : UserDetails {

    companion object {
        fun create(
            id: Long = 0,
            username: String,
            password: String,
            authorities: Set<GrantedAuthority> = HashSet(),
            expired: Boolean = false,
            locked: Boolean = false,
            passwordExpired: Boolean = false,
            enabled: Boolean = true
        ): Identity {

            return Identity(
                id = id,
                username = username,
                password = password,
                authorities = authorities,
                expired = expired,
                locked = locked,
                credentialsExpired = passwordExpired,
                enabled = enabled
            )
        }
    }

    override fun getUsername(): String {
        return this.username
    }

    override fun getPassword(): String {
        return this.password
    }

    override fun getAuthorities(): Set<GrantedAuthority> {
        return this.authorities
    }

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean {
        return !this.expired
    }

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean {
        return !this.locked
    }

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean {
        return !this.credentialsExpired
    }

    override fun isEnabled(): Boolean {
        return this.enabled
    }
}