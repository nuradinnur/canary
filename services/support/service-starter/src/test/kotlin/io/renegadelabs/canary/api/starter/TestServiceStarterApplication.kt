package io.renegadelabs.canary.api.starter

import io.kotest.core.annotation.Tags
import io.kotest.matchers.date.shouldBeBefore
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.renegadelabs.canary.api.shared.test.AbstractIntegrationTest
import io.renegadelabs.canary.api.shared.test.domain.TestTags
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import java.time.Instant

@Tags(TestTags.INTEGRATION_TEST)
@ContextConfiguration(classes = [ServiceStarterApplication::class])
class TestServiceStarterApplication(
    private val applicationContext: ApplicationContext,
    @Value("\${spring.application.name}")
    private val applicationName: String
): AbstractIntegrationTest() {

    init {
        this.feature("I want to start this Spring application") {
            scenario("application context is successfully injected by Spring") {
                applicationContext.shouldNotBeNull()
                applicationContext.id.shouldBe(applicationName)
                Instant.ofEpochMilli(applicationContext.startupDate).shouldBeBefore(Instant.now())
            }
        }
    }
}
