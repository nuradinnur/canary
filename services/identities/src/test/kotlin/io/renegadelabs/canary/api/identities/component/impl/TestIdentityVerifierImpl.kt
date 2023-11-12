package io.renegadelabs.canary.api.identities.component.impl

import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.junit5.MockKExtension.CheckUnnecessaryStub
import io.mockk.junit5.MockKExtension.ConfirmVerification
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.renegadelabs.canary.api.identities.domain.Identity
import io.renegadelabs.canary.api.shared.test.domain.TestTags
import org.springframework.context.MessageSource
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import reactor.test.StepVerifier
import java.util.*

@Tags(TestTags.UNIT_TEST)
@ConfirmVerification
@CheckUnnecessaryStub
class TestIdentityVerifierImpl: BehaviorSpec({

    val validIdentity = Identity.create(username = "username", password = "password")

    val messageSource = mockk<MessageSource>()
    val identityCheckerImpl = spyk(IdentityVerifierImpl())

    identityCheckerImpl.setMessageSource(messageSource)

    every { messageSource.getMessage("exception.account-expired", any(), any(Locale::class)) } returns "exception.account-expired"
    every { messageSource.getMessage("exception.locked", any(), any(Locale::class)) } returns "exception.locked"
    every { messageSource.getMessage("exception.credentials-expired", any(), any(Locale::class)) } returns "exception.credentials-expired"
    every { messageSource.getMessage("exception.disabled", any(), any(Locale::class)) } returns "exception.disabled"

    this.given("I would like to check an identity") {
        `when`("it is valid") {
            val publisher = identityCheckerImpl.verifyIdentity(validIdentity)
            then("it validates the identity") {
                StepVerifier.create(publisher)
                    .expectNextCount(0)
                    .verifyComplete()
                // TODO: fix verification
                verify(exactly = 1) { identityCheckerImpl.verifyIdentity(validIdentity) }
            }
        }
        `when`("it is expired") {
            val publisher = identityCheckerImpl.verifyIdentity(validIdentity.copy(expired = true))
            then("it throws AccountExpiredException") {
                StepVerifier.create(publisher)
                    .expectNextCount(0)
                    .expectErrorMatches { throwable ->
                        throwable is AccountExpiredException &&
                        throwable.message == "exception.account-expired"
                    }
                    .verify()
                verify(exactly = 1) { identityCheckerImpl.verifyIdentity(validIdentity) }
            }
        }
        `when`("it is locked") {
            val publisher = identityCheckerImpl.verifyIdentity(validIdentity.copy(locked = true))
            then("it throws LockedException") {
                StepVerifier.create(publisher)
                    .expectNextCount(0)
                    .expectErrorMatches { throwable ->
                        throwable is LockedException &&
                        throwable.message == "exception.locked"
                    }
                    .verify()
                verify(exactly = 1) { identityCheckerImpl.verifyIdentity(validIdentity) }
            }
        }
        `when`("it has expired credentials") {
            val publisher = identityCheckerImpl.verifyIdentity(validIdentity.copy(credentialsExpired = true))
            then("it throws CredentialsExpiredException") {
                StepVerifier.create(publisher)
                    .expectNextCount(0)
                    .expectErrorMatches { throwable ->
                        throwable is CredentialsExpiredException &&
                        throwable.message == "exception.credentials-expired"
                    }
                    .verify()
                verify(exactly = 1) { identityCheckerImpl.verifyIdentity(validIdentity) }
            }
        }
        `when`("it is disabled") {
            val publisher = identityCheckerImpl.verifyIdentity(validIdentity.copy(disabled = true))
            then("it throws DisabledException") {
                StepVerifier.create(publisher)
                    .expectNextCount(0)
                    .expectErrorMatches { throwable ->
                        throwable is DisabledException &&
                        throwable.message == "exception.disabled"
                    }
                    .verify()
                verify(exactly = 1) { identityCheckerImpl.verifyIdentity(validIdentity) }
            }
        }
    }
})