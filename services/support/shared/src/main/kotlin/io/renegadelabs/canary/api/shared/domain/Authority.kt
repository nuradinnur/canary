package io.renegadelabs.canary.api.shared.domain

import org.springframework.security.core.GrantedAuthority

enum class Authority(private val authority: String): GrantedAuthority {

    // USER TYPES
    ANONYMOUS("ANONYMOUS"),
    USER("USER"),
    ADMINISTRATOR("ADMINISTRATOR"),
    SYSTEM("SYSTEM"),
    REFRESH("REFRESH");

    override fun getAuthority(): String {
        return this.authority
    }
}