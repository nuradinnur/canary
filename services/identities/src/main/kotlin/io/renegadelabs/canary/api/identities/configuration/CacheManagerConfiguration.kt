package io.renegadelabs.canary.api.identities.configuration

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class CacheManagerConfiguration {

    @Bean
    fun cacheManager(): CacheManager {
        val caffeine = Caffeine.newBuilder()
        with(caffeine) {
            // TODO: potentially link cache expiration to refresh token duration?
            expireAfterWrite(Duration.ofHours(1))
        }
        val cacheManager = CaffeineCacheManager()
        with(cacheManager) {
            setCaffeine(Caffeine.newBuilder())
            setCacheNames(setOf(
                "sessions",
                "identity"
            ))
        }
        return cacheManager
    }
}