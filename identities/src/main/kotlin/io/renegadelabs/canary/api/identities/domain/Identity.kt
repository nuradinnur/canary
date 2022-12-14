package io.renegadelabs.canary.api.identities.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class Identity(
    private val id: Long,
    private val username: String,
    @JsonIgnore private val password: String,
    private val authorities: Set<GrantedAuthority>,
    private val expired: Boolean,
    private val locked: Boolean,
    private val passwordExpired: Boolean,
    private val enabled: Boolean,
) : UserDetails {

    companion object {
        fun create(id: Long = 0,
                   username: String,
                   password: String,
                   authorities: Set<GrantedAuthority> = HashSet(),
                   expired: Boolean = false,
                   locked: Boolean = false,
                   passwordExpired: Boolean = false,
                   enabled: Boolean = true): Identity {

            return Identity(
                id = id,
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

    fun getId(): Long {
        return this.id
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