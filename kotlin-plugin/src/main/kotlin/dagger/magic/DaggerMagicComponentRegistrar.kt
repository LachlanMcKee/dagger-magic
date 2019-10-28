package dagger.magic

import dagger.magic.DaggerMagicKeys.KEY_BINDS_ANNOTATIONS
import dagger.magic.DaggerMagicKeys.KEY_ENABLED
import dagger.magic.DaggerMagicKeys.KEY_PROVIDES_ANNOTATIONS
import dagger.magic.mapper.ReplacementsMapper
import dagger.magic.model.Replacements
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration

class DaggerMagicComponentRegistrar : ComponentRegistrar {
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        if (configuration[KEY_ENABLED] == true) {
            ClassBuilderInterceptorExtension.registerExtension(project, createExtension(configuration))
        }
    }

    private fun createExtension(configuration: CompilerConfiguration): ClassBuilderInterceptorExtension {
        return DaggerMagicExtension(
                configuration.getReplacements(KEY_PROVIDES_ANNOTATIONS),
                configuration.getReplacements(KEY_BINDS_ANNOTATIONS)
        )
    }

    private fun CompilerConfiguration.getReplacements(key: ListConfigurationKey): Replacements {
        return ReplacementsMapper.map(this[key] ?: emptyList())
    }
}
