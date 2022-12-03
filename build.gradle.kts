import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

group = "io.renegadelabs"
version = "0.0.1"

val fullyQualifiedImageName: String = System.getenv("IMAGE") ?: "renegadelabs.io/canary-${project.name}:latest"
val useBuildPack: String = System.getenv("USE_BUILDPACK") ?: "paketobuildpacks/builder:full"
val pushImageToRepository: Boolean = System.getenv("PUSH_IMAGE").toBoolean()
val useNativeCompiler: Boolean = System.getenv("USE_NATIVE_COMPILER").toBoolean()

plugins {
    idea
    kotlin("jvm") version "1.7.10" apply false
    kotlin("plugin.spring") version "1.7.10" apply false
    id("com.github.ben-manes.versions") version "0.44.0"
    id("io.spring.dependency-management") version "1.1.0" apply false
    id("org.springframework.boot") version "3.0.0" apply false
    id("org.graalvm.buildtools.native") version "0.9.14" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "io.spring.dependency-management")

    configure<DependencyManagementExtension> {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.5")
        }
    }
}

configure(allprojects - project(":shared")) {
    apply(plugin = "org.springframework.boot")
}

tasks.withType<Wrapper> {
    gradleVersion = "7.6"
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

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<BootBuildImage> {
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
