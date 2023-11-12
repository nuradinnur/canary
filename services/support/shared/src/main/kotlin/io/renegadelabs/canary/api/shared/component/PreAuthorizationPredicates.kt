package io.renegadelabs.canary.api.shared.component

import io.renegadelabs.canary.api.shared.domain.Authority
import io.renegadelabs.canary.api.shared.domain.JwtAuthentication
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class PreAuthorizationPredicates {

    fun isUser(authentication: Authentication): Boolean {
        return this.isAdministrator(authentication) || authentication.authorities.contains(Authority.USER)
    }

    fun isAdministrator(authentication: Authentication): Boolean {
        return authentication.authorities.contains(Authority.ADMINISTRATOR)
    }

    fun hasRefreshAuthority(authentication: Authentication): Boolean {
        return authentication.authorities.contains(Authority.REFRESH)
    }

    fun hasUsername(authentication: Authentication, username: String): Boolean {
        if (authentication !is JwtAuthentication)
            return false
        if (authentication.getAuthorizationDetails().isEmpty()) {
            return false
        }
        return this.isAdministrator(authentication) || authentication.getSubject() == username
    }

    fun hasId(authentication: Authentication, id: Long): Boolean {
        if (authentication !is JwtAuthentication)
            return false
        return this.isAdministrator(authentication) || authentication.getSubId() == id
    }
}
