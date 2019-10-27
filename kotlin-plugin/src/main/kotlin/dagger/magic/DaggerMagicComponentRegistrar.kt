package dagger.magic

import dagger.magic.mapper.ByteCodeMapper.convertClassPathToByteCode
import dagger.magic.mapper.ReplacementsMapper
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
                convertClassPathToByteCode(configuration[KEY_KOTLIN_OBJECT_MODULE_ANNOTATION]!!),
                ReplacementsMapper.map(configuration[KEY_PROVIDES_ANNOTATIONS]!!),
                ReplacementsMapper.map(configuration[KEY_BINDS_ANNOTATIONS]!!)
        )
    }
}
