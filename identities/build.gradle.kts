group = "io.renegadelabs.canary.api"
version = "0.0.1"

dependencies {
    implementation(project(":shared"))
    testImplementation(libs.kotest)
    testImplementation(libs.kotest.spring)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}
