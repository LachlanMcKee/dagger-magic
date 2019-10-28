package dagger.magic

import dagger.magic.model.Replacements
import org.jetbrains.kotlin.codegen.ClassBuilderFactory
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.codegen.DelegatingClassBuilderFactory
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin

class DaggerMagicExtension(
        private val providesAnnotations: Replacements,
        private val bindsAnnotations: Replacements
) : ClassBuilderInterceptorExtension {

    override fun interceptClassBuilderFactory(
            interceptedFactory: ClassBuilderFactory,
            bindingContext: BindingContext,
            diagnostics: DiagnosticSink): ClassBuilderFactory {

        return object : DelegatingClassBuilderFactory(interceptedFactory) {
            override fun newClassBuilder(origin: JvmDeclarationOrigin): DelegatingClassBuilder {
                return DaggerMagicClassBuilder(
                        providesAnnotations,
                        bindsAnnotations,
                        interceptedFactory.newClassBuilder(origin))
            }
        }
    }
}
