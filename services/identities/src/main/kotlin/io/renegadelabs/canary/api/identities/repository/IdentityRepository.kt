package io.renegadelabs.canary.api.identities.repository

import io.renegadelabs.canary.api.identities.domain.Identity
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface IdentityRepository: R2dbcRepository<Identity, Long> {

    fun findByUsername(username: String): Mono<Identity>

    fun existsByUsername(username: String): Mono<Boolean>
}