group = "io.renegadelabs.canary.api"
version = "1.0.0"

dependencies {
    implementation(project(":shared"))
    testImplementation(libs.bundles.kotest)
    // testImplementation(libs.bundles.blockhound)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}
