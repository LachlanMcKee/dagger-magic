package dagger.magic

import com.google.auto.service.AutoService
import dagger.magic.mapper.ByteCodeMapper.convertClassPathToByteCode
import dagger.magic.mapper.ReplacementsMapper
import org.jetbrains.kotlin.codegen.ClassBuilderFactory
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.codegen.DelegatingClassBuilderFactory
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin

@AutoService(ComponentRegistrar::class)
class DaggerMagicComponentRegistrar : ComponentRegistrar {
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        if (configuration[KEY_ENABLED] == true) {
            ClassBuilderInterceptorExtension.registerExtension(project, createExtension(configuration))
        }
    }

    private fun createExtension(configuration: CompilerConfiguration): ClassBuilderInterceptorExtension {
        return object : ClassBuilderInterceptorExtension {
            override fun interceptClassBuilderFactory(
                    interceptedFactory: ClassBuilderFactory,
                    bindingContext: BindingContext,
                    diagnostics: DiagnosticSink): ClassBuilderFactory {

                return object : DelegatingClassBuilderFactory(interceptedFactory) {
                    override fun newClassBuilder(origin: JvmDeclarationOrigin): DelegatingClassBuilder {
                        return DaggerMagicClassBuilder(
                                convertClassPathToByteCode(configuration[KEY_KOTLIN_OBJECT_MODULE_ANNOTATION]!!),
                                ReplacementsMapper.map(configuration[KEY_PROVIDES_ANNOTATIONS]!!),
                                ReplacementsMapper.map(configuration[KEY_BINDS_ANNOTATIONS]!!),
                                interceptedFactory.newClassBuilder(origin))
                    }
                }
            }
        }
    }
}
