package io.renegadelabs.canary.api.shared.test

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.junit5.MockKExtension.CheckUnnecessaryStub
import io.mockk.junit5.MockKExtension.ConfirmVerification

@ConfirmVerification
@CheckUnnecessaryStub
abstract class AbstractUnitTest: BehaviorSpec()