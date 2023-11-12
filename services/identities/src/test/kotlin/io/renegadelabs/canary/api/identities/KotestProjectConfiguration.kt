package io.renegadelabs.canary.api.identities

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.names.TestNameCase
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCaseOrder
import io.kotest.extensions.blockhound.BlockHound
import io.kotest.extensions.blockhound.BlockHoundMode
import io.kotest.extensions.spring.SpringExtension
import kotlin.time.Duration.Companion.seconds

class KotestProjectConfiguration : AbstractProjectConfig() {
    override fun extensions() = listOf(SpringExtension, BlockHound(BlockHoundMode.ERROR))
    override val specExecutionOrder = SpecExecutionOrder.Annotated
    override val testCaseOrder = TestCaseOrder.Random
    override val assertionMode = AssertionMode.None
    override val duplicateTestNameMode = DuplicateTestNameMode.Error
    override val failOnIgnoredTests = true
    override val globalAssertSoftly = true
    override val testNameCase = TestNameCase.Lowercase
    override val testNameAppendTags = true
    override val testNameRemoveWhitespace = true
    override val parallelism = 4
    override val timeout = 1.seconds
}

