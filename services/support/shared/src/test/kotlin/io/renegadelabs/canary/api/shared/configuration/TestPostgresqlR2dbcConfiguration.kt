//package io.renegadelabs.canary.api.shared.configuration
//
//import io.kotest.core.annotation.Tags
//import io.kotest.core.spec.style.ShouldSpec
//import io.kotest.matchers.nulls.shouldNotBeNull
//import io.renegadelabs.canary.api.shared.PostgresqlTestContainer
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.boot.test.context.SpringBootTest
//
//@Tags("canary_services_shared", "spring_test")
//@SpringBootTest(classes = [PostgresqlR2dbcConfiguration::class, PostgresqlTestContainer::class], properties = [
//    "spring.cloud.environment.enabled = true"
//])
//class TestPostgresqlR2dbcConfiguration(
//    private val postgresqlR2dbcConfiguration: PostgresqlR2dbcConfiguration,
//    private val postgresqlTestContainer: PostgresqlTestContainer,
//    @Value("\${spring.r2dbc.host}")
//    private val host: String,
//    @Value("\${spring.r2dbc.port}")
//    private val port: String,
//    @Value("\${spring.r2dbc.username}")
//    private val username: String,
//    @Value("\${spring.r2dbc.password}")
//    private val password: String,
//    @Value("\${spring.r2dbc.schema}")
//    private val schema: String
//): ShouldSpec() {
//
//    init {
//        this.context("spring bean wiring") {
//            should("should be autowired and connected to test container") {
//                postgresqlR2dbcConfiguration.shouldNotBeNull()
//                postgresqlTestContainer.host == host
//                postgresqlTestContainer.portBindings.contains(port)
//                postgresqlTestContainer.username == username
//                postgresqlTestContainer.password == password
//                postgresqlTestContainer.databaseName == schema
//            }
//        }
//    }
//}