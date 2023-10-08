import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringExtension

class KotestSpringProjectConfig : AbstractProjectConfig() {
    override fun extensions() = listOf(SpringExtension)
}