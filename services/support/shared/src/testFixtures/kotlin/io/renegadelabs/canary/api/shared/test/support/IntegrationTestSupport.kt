package io.renegadelabs.canary.api.shared.test.support

import org.springframework.beans.factory.config.PropertiesFactoryBean
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.core.io.PathResource
import org.springframework.util.Assert
import org.springframework.util.ResourceUtils
import org.testcontainers.containers.PostgreSQLContainer
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class IntegrationTestSupport {

    enum class ValidSpringProfiles {
        DEFAULT {
            override fun getApplicationPropertiesFileName(): String {
                return "classpath:application.properties"
            }

            override fun getApplicationYmlFileName(): String {
                return "classpath:application.yml"
            }

            override fun getApplicationYamlFileName(): String {
                return "classpath:application.yaml"
            }
        },
        DEVELOPMENT,
        TEST,
        POSTGRESQL;

        override fun toString(): String {
            return super.toString().lowercase()
        }

        open fun getApplicationPropertiesFileName(): String {
            return "classpath:application-${this}.properties"
        }

        open fun getApplicationYmlFileName(): String {
            return "classpath:application-${this}.yml"
        }

        open fun getApplicationYamlFileName(): String {
            return "classpath:application-${this}.yaml"
        }
    }

    companion object {
        private const val POSTGRESQL_DOCKER_IMAGE_NAME = "postgres:15-alpine"

        private const val PROPERTIES_EXTENSION = "properties"
        private const val YML_EXTENSION = "yml"
        private const val YAML_EXTENSION = "yaml"

        fun createPostgresqlContainer(): PostgreSQLContainer<*> {
            return PostgreSQLContainer<Nothing>(this.POSTGRESQL_DOCKER_IMAGE_NAME)
        }

        fun getSpringProperties(): Properties {
            return this.getClasspathSpringPropertiesFiles().map {
                val propertiesFactoryBean = PropertiesFactoryBean()
                val yamlPropertiesFactoryBean = YamlPropertiesFactoryBean()
                if (it.extension.equals(this.PROPERTIES_EXTENSION, ignoreCase = true)) {
                    this.readApplicationPropertiesFile(propertiesFactoryBean, it)
                } else {
                    this.readYamlPropertiesFile(yamlPropertiesFactoryBean, it)
                }
            }.let {
                if (it.isEmpty()) Properties()
                it.reduce { merged, properties ->
                    merged.putAll(properties)
                    merged
                }
            }
        }

        private fun getClasspathSpringPropertiesFiles(): List<File> {
            val applicationPropertiesFiles = ValidSpringProfiles.values().map { it.getApplicationPropertiesFileName() }
            val applicationYmlFiles = ValidSpringProfiles.values().map { it.getApplicationYmlFileName() }
            val applicationYamlFiles = ValidSpringProfiles.values().map { it.getApplicationYamlFileName() }
            return applicationPropertiesFiles.plus(applicationYmlFiles).plus(applicationYamlFiles)
                .mapNotNull {
                    try {
                        ResourceUtils.getFile(it)
                    } catch (exception: FileNotFoundException) {
                        null
                    }
                }.filter { it.exists() }
        }

        private fun readApplicationPropertiesFile(propertiesFactoryBean: PropertiesFactoryBean, file: File): Properties {
            Assert.state(file.exists(), "The file does not exist: \"${file.name}\"")
            Assert.state(file.extension.equals(this.PROPERTIES_EXTENSION, ignoreCase = true),
                "The file's extension must be .properties. File name: \"${file.name}\"")
            return with(propertiesFactoryBean) {
                setLocation(PathResource(file.toPath()))
                afterPropertiesSet()
                `object` ?: Properties()
            }
        }

        private fun readYamlPropertiesFile(yamlPropertiesFactoryBean: YamlPropertiesFactoryBean, file: File): Properties {
            Assert.state(file.exists(), "The file does not exist: \"${file.name}\"")
            Assert.state(file.extension.equals(this.YML_EXTENSION, ignoreCase = true)
                    or file.extension.equals(this.YAML_EXTENSION, ignoreCase = true) ,
                "The file's extension must be either .yml or .yaml. Filename: \"${file.name}\"")
            return with(yamlPropertiesFactoryBean) {
                setResources(PathResource(file.toPath()))
                afterPropertiesSet()
                `object` ?: Properties()
            }
        }
    }
}