package io.renegadelabs.canary.api.shared.domain

import org.springframework.security.core.GrantedAuthority

enum class Authorities(private val authority: String): GrantedAuthority {
    REFRESH("REFRESH"),
    USER("USER"),
    ADMINISTRATOR("ADMINISTRATOR");

    override fun getAuthority(): String {
        return this.authority
    }
}