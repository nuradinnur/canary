
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

/**
 * Gradle project identifiers
 */
group = "io.renegadelabs"
version = "1.0.0"

/**
 * Compile-time environment
 */
val JVM_TARGET_VERSION: String by project

/**
 * Build-time environment variables
 */
val IMAGE_REPOSITORY = "renegadelabs.io"
val FULLY_QUALIFIED_IMAGE_NAME: String = System.getenv("IMAGE") ?: "renegadelabs.io/canary-${project.name}:latest"
val USE_BUILDPACK: String = System.getenv("USE_BUILDPACK") ?: "paketobuildpacks/builder:base"
val BUILD_NATIVE_IMAGE: Boolean = System.getenv("USE_NATIVE_COMPILER").toBoolean()
val PUSH_IMAGE_TO_REPOSITORY: Boolean = System.getenv("PUSH_IMAGE").toBoolean()

plugins {
    alias(libs.plugins.versions)
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.graalvm) apply false
}

tasks.withType<Wrapper> {
    gradleVersion = libs.versions.wrapper.get()
}

tasks.withType<DependencyUpdatesTask> {
    fun isNonStable(version: String): Boolean {
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)
        return !isStable
    }

    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

configure(subprojects) {
    apply(plugin = "jacoco")
    apply(plugin = rootProject.project.libs.plugins.kotlin.jvm.get().pluginId)
    apply(plugin = rootProject.project.libs.plugins.kotlin.spring.get().pluginId)
    apply(plugin = rootProject.project.libs.plugins.spring.dependency.management.get().pluginId)

    configure<KotlinProjectExtension> {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(JVM_TARGET_VERSION))
        }
    }

    configure<DependencyManagementExtension> {
        imports {
            mavenBom(SpringBootPlugin.BOM_COORDINATES)
            mavenBom(rootProject.libs.spring.cloud.dependencies.get().toString())
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = JVM_TARGET_VERSION
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        jvmArgs = listOf("-XX:+AllowRedefinitionToAddDeleteMethods")
    }

    tasks.withType<JacocoReport> {
        dependsOn(tasks.withType<Test>())
        reports {
            xml.required = true
        }
        doLast {
            println("View code coverage report at:")
            println("file://${layout.buildDirectory.get()}/reports/jacoco/test/html/index.html")
        }
    }

    tasks.withType<JacocoCoverageVerification> {
        dependsOn(tasks.withType<JacocoReport>())
        violationRules {
            rule {
                limit {
                    counter = "BRANCH"
                    minimum = 0.80.toBigDecimal()
                }
                limit {
                    counter = "LINE"
                    minimum = 0.80.toBigDecimal()
                }
            }
        }
    }

    tasks.getByName("check") {
        dependsOn(tasks.withType<JacocoReport>())
        dependsOn(tasks.withType<JacocoCoverageVerification>())
    }
}

configure(setOf(project(":shared"))) {
    apply(plugin = "java-test-fixtures")
}

configure(subprojects - project(":shared")) {
    apply(plugin = rootProject.project.libs.plugins.spring.boot.get().pluginId)
    apply(plugin = rootProject.project.libs.plugins.graalvm.get().pluginId)

    setOf(
        "aotClasses",
        "aotTestClasses",
        "compileAotJava",
        "compileAotKotlin",
        "compileAotTestJava",
        "compileAotTestKotlin",
        "processAot",
        "processTestAot",
        "processAotResources",
        "processAotTestResources",
    ).forEach {
        tasks.named(it) {
            enabled = BUILD_NATIVE_IMAGE
        }
    }

    tasks.withType<BootBuildImage> {
        imageName.set(FULLY_QUALIFIED_IMAGE_NAME)
        publish.set(PUSH_IMAGE_TO_REPOSITORY)
        builder.set(USE_BUILDPACK)
        buildpacks.set(buildList {
            add("gcr.io/paketo-buildpacks/ca-certificates:latest")
            add("gcr.io/paketo-buildpacks/bellsoft-liberica:latest")
            add("gcr.io/paketo-buildpacks/syft:latest")
            add("gcr.io/paketo-buildpacks/executable-jar:latest")
            add("gcr.io/paketo-buildpacks/spring-boot:latest")
            if (BUILD_NATIVE_IMAGE) add("gcr.io/paketo-buildpacks/native-image:latest")
        })
        environment.set(buildMap {
            put("BP_JVM_VERSION", JVM_TARGET_VERSION)
            if (BUILD_NATIVE_IMAGE) {
                put("BP_NATIVE_IMAGE", true.toString())
                put("BP_NATIVE_IMAGE_BUILD_ARGUMENTS",
                    listOf(
                        "-H:+AddAllCharsets",
                        "-H:+ReportExceptionStackTraces",
                        "--verbose"
                    ).joinToString(" ")
                )
            }
        })
        verboseLogging.set(true)
    }
}
