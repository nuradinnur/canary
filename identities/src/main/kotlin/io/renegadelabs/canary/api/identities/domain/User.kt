package io.renegadelabs.canary.api.identities.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class User(
    private val username: String,
    @JsonIgnore private val password: String,
    private val authorities: Set<GrantedAuthority>,
    private val expired: Boolean,
    private val locked: Boolean,
    private val passwordExpired: Boolean,
    private val enabled: Boolean,
) : UserDetails {

    companion object {
        fun create(username: String,
                   password: String,
                   authorities: Set<GrantedAuthority> = HashSet(),
                   expired: Boolean = false,
                   locked: Boolean = false,
                   passwordExpired: Boolean = false,
                   enabled: Boolean = false): User {

            return User(
                username = username,
                password = password,
                authorities = authorities,
                expired = expired,
                locked = locked,
                passwordExpired = passwordExpired,
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

    override fun isAccountNonExpired(): Boolean {
        return !this.expired
    }

    override fun isAccountNonLocked(): Boolean {
        return !this.locked
    }

    override fun isCredentialsNonExpired(): Boolean {
        return !this.passwordExpired
    }

    override fun isEnabled(): Boolean {
        return this.enabled
    }
}