package io.renegadelabs.canary.api.identities.component.impl

import io.kotest.core.annotation.Tags
import io.mockk.*
import io.mockk.junit5.MockKExtension.CheckUnnecessaryStub
import io.mockk.junit5.MockKExtension.ConfirmVerification
import io.renegadelabs.canary.api.identities.domain.Identity
import io.renegadelabs.canary.api.shared.test.AbstractUnitTest
import io.renegadelabs.canary.api.shared.test.domain.TestTags
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCache
import reactor.test.StepVerifier

@Tags(TestTags.UNIT_TEST)
@ConfirmVerification
@CheckUnnecessaryStub
class TestIdentityCacheImpl: AbstractUnitTest() {

    init {
        val identity = Identity.create(username = "username", password = "password")

        val cache = mockk<CaffeineCache>()
        val cacheManager = mockk<CacheManager>()

        every { cacheManager.getCache(any(String::class)) } returns cache
        every { cache.get(eq("username"), any(Class::class)) } returns identity
        every { cache.put(eq("username"), any(Identity::class)) } just Runs
        every { cache.evictIfPresent(eq("username")) } returns true

        val identityCacheImpl = IdentityCacheImpl(cacheManager)

        this.given("I have a valid username") {
            val username = identity.username
            `when`("I want to retrieve the cached identity") {
                val publisher = identityCacheImpl.getIdentity(username!!)
                then("it returns the cached identity") {
                    StepVerifier.create(publisher)
                        .expectNext(identity)
                        .expectComplete()
                        .verify()
                    verify(exactly = 1) { cache.get(username, Identity::class.java) }
                }
            }
            `when`("I want to evict the cached identity") {
                val publisher = identityCacheImpl.evictIdentity(identity.username!!)
                then("it evicts the cached identity") {
                    StepVerifier.create(publisher)
                        .expectNext(true)
                        .expectComplete()
                        .verify()
                    verify(exactly = 1) { cache.evictIfPresent(identity.username!!) }
                }
            }
        }

        this.given("I have a valid identity") {
            When("I want to cache an identity") {
                val publisher = identityCacheImpl.putIdentity(identity)
                then("it caches the identity") {
                    StepVerifier.create(publisher)
                        .expectComplete()
                        .verify()
                    verify(exactly = 1) { cache.put(identity.username!!, identity) }
                }
            }
        }
    }
}