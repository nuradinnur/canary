import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

group = "io.renegadelabs"
version = "1.0.0"

val fullyQualifiedImageName: String = System.getenv("IMAGE") ?: "renegadelabs.io/canary-${project.name}:latest"
val useBuildPack: String = System.getenv("USE_BUILDPACK") ?: "paketobuildpacks/builder:full"
val pushImageToRepository: Boolean = System.getenv("PUSH_IMAGE").toBoolean()
val useNativeCompiler: Boolean = System.getenv("USE_NATIVE_COMPILER").toBoolean()

plugins {
    idea
    kotlin("jvm") version "1.4.32" apply false
    kotlin("plugin.spring") version "1.4.32" apply false
    id("com.github.ben-manes.versions") version "0.38.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
    id("org.springframework.boot") version "2.4.5" apply false
    id("org.springframework.experimental.aot") version "0.9.2" apply false
}

dependencies {
    enforcedPlatform("org.springframework.cloud:spring-cloud-dependencies:2020.0.0-M4")
}

allprojects {
    repositories {
        maven { url = uri("https://repo.spring.io/release") }
        mavenCentral()
    }

    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.springframework.boot")

    configure<DependencyManagementExtension> {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        }
    }
}

configure(allprojects - project(":shared")) {
    if (useNativeCompiler) {
        apply(plugin = "org.springframework.experimental.aot")
    }
    tasks.withType<BootBuildImage> {
        builder = useBuildPack
        imageName = fullyQualifiedImageName
        isPublish = pushImageToRepository
        isVerboseLogging = true
        environment = mapOf(
            "BP_NATIVE_IMAGE" to useNativeCompiler.toString(),
            "BP_NATIVE_IMAGE_BUILD_ARGUMENTS" to listOf(
                "-H:+ReportExceptionStackTraces",
                "--verbose"
            ).joinToString(separator = " ")
        )
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "7.0"
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
