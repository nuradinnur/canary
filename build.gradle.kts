import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

this.group = "io.renegadelabs"
this.version = "1.0.0"

plugins {
    idea
    kotlin("jvm") version "1.4.32" apply false
    kotlin("plugin.spring") version "1.4.32" apply false
    id("com.github.ben-manes.versions") version "0.36.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
    id("org.springframework.boot") version "2.4.5" apply false
    id("org.springframework.experimental.aot") version "0.9.2" apply false
}

allprojects {
    repositories {
        maven { url = uri("https://repo.spring.io/release") }
        mavenCentral()
    }
}

subprojects {
    val sourceCompatibility = JavaVersion.VERSION_11.toString()

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = sourceCompatibility
        }

        tasks.withType<Test> {
            useJUnitPlatform()
        }
    }

    configure(allprojects - project(":shared")) {
        apply(plugin = "org.jetbrains.kotlin.plugin.spring")
        apply(plugin = "io.spring.dependency-management")
        apply(plugin = "org.springframework.boot")
        apply(plugin = "org.springframework.experimental.aot")

        configure<DependencyManagementExtension> {
            imports {
                mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
            }
        }

        tasks.withType<BootBuildImage> {
            builder = "paketobuildpacks/builder:tiny"
            imageName = System.getenv("IMAGE") ?: "renegadelabs.io/canary/identity"
            isPublish = System.getenv("PUSH_IMAGE").toBoolean()
            environment = mapOf(
                "BP_NATIVE_IMAGE" to "true",
                "BP_NATIVE_IMAGE_BUILD_ARGUMENTS" to listOf(
                    "-H:+ReportExceptionStackTraces",
                    "--verbose"
                ).joinToString(separator = " ")
            )

        }
    }
}
