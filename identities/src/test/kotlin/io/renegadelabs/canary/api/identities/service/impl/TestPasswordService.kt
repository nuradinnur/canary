package io.renegadelabs.canary.api.identities.service.impl

import io.kotest.core.spec.style.BehaviorSpec
import io.renegadelabs.canary.api.identities.service.PasswordService
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestPasswordService(
    private val passwordService: PasswordService
) : BehaviorSpec({

    given("a password") {
        and("the password is null") {
            `when`("I want to validate the user's password") {
                then("it should fail") {
                }
            }
            `when`("I want to update the user's password") {
                then("it should update successfully") {
                }
            }
        }
        and("the password is empty") {
            `when`("I want to validate the user's password") {
                then("it should fail") {
                }
            }
            `when`("I want to update the user's password") {
                then("it should update successfully") {
                }
            }
        }
        and("the password is invalid") {
            `when`("I want to validate the user's password") {
                then("it should fail") {
                }
            }
            `when`("I want to update the user's password") {
                then("it should update successfully") {
                }
            }
        }
        and("the password is valid") {
            `when`("I want to validate the user's password") {
                then("it should be validated successfully") {
                }
            }
            `when`("I want to update the user's password") {
                then("it should update successfully") {
                }
            }
        }
    }
})
