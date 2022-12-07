import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

/**
 * Gradle project identifiers
 */
group = "io.renegadelabs"
version = "0.0.1"

/**
 * Build time environment variables
 */
val fullyQualifiedImageName: String = System.getenv("IMAGE") ?: "renegadelabs.io/canary-${project.name}:latest"
val useBuildPack: String = System.getenv("USE_BUILDPACK") ?: "paketobuildpacks/builder:full"
val pushImageToRepository: Boolean = System.getenv("PUSH_IMAGE").toBoolean()
val useNativeCompiler: Boolean = System.getenv("USE_NATIVE_COMPILER").toBoolean()


@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.versions)
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.graalvm) apply false
}

tasks.withType<Wrapper> {
    gradleVersion = libs.versions.wrapper.get()
}

tasks.withType<DependencyUpdatesTask> {
    fun isNonStable(version: String): Boolean {
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)
        return isStable.not()
    }

    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}


configure(subprojects) {
    apply(plugin = rootProject.project.libs.plugins.kotlin.jvm.get().pluginId)
    apply(plugin = rootProject.project.libs.plugins.kotlin.spring.get().pluginId)
    apply(plugin = rootProject.project.libs.plugins.spring.dependency.management.get().pluginId)

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    configure<DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.0.0")
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.5")
        }
    }
}

configure(subprojects - project(":shared")) {
    apply(plugin = rootProject.project.libs.plugins.spring.boot.get().pluginId)
    apply(plugin = rootProject.project.libs.plugins.graalvm.get().pluginId)

    tasks.named<BootBuildImage>("bootBuildImage") {
        builder.set(useBuildPack)
        imageName.set(fullyQualifiedImageName)
        publish.set(pushImageToRepository)
        verboseLogging.set(true)
        environment.set(mapOf(
            "BP_JVM_VERSION" to "17",
            "BP_NATIVE_IMAGE" to useNativeCompiler.toString(),
            "BP_NATIVE_IMAGE_BUILD_ARGUMENTS" to listOf(
                "-H:+AddAllCharsets",
                "-H:+ReportExceptionStackTraces",
                "--verbose"
            ).joinToString(separator = " ")
        ))
    }
}
