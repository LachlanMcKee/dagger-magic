package dagger.magic

import dagger.magic.model.Replacement
import dagger.magic.model.Replacements
import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.org.objectweb.asm.AnnotationVisitor
import org.jetbrains.org.objectweb.asm.ClassVisitor
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes

internal class DaggerMagicClassBuilder(
        private val providesAnnotations: Replacements,
        private val bindsAnnotations: Replacements,
        private val delegateBuilder: ClassBuilder
) : DelegatingClassBuilder() {

    private var classLevelProvidesAnnotation: Replacement? = null
    private var classLevelBindsAnnotation: Replacement? = null

    override fun getDelegate(): ClassBuilder {
        return delegateBuilder
    }

    override fun getVisitor(): ClassVisitor {
        val originalVisitor = super.getVisitor()
        return object : ClassVisitor(Opcodes.ASM5, originalVisitor) {
            override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
                if (classLevelProvidesAnnotation == null) {
                    classLevelProvidesAnnotation = providesAnnotations[descriptor]
                }
                if (classLevelBindsAnnotation == null) {
                    classLevelBindsAnnotation = bindsAnnotations[descriptor]
                }

                return super.visitAnnotation(descriptor, visible)
            }
        }
    }
    override fun newMethod(
            origin: JvmDeclarationOrigin,
            access: Int,
            name: String,
            desc: String,
            signature: String?,
            exceptions: Array<out String>?): MethodVisitor {

        val methodOriginal = super.newMethod(origin, access, name, desc, signature, exceptions)
        if (name == CONSTRUCTOR_NAME) {
            return methodOriginal
        }

        classLevelProvidesAnnotation?.let { annotation ->
            addAnnotations(methodOriginal, PROVIDES_DESCRIPTOR, annotation.replacement)
        }
        classLevelBindsAnnotation?.let { annotation ->
            addAnnotations(methodOriginal, BINDS_DESCRIPTOR, annotation.replacement)
        }

        return object : MethodVisitor(Opcodes.ASM5, methodOriginal) {
            override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
                replaceAnnotationIfRequired(descriptor, PROVIDES_DESCRIPTOR, providesAnnotations)
                replaceAnnotationIfRequired(descriptor, BINDS_DESCRIPTOR, bindsAnnotations)

                return super.visitAnnotation(descriptor, visible)
            }

            private fun replaceAnnotationIfRequired(
                    annotationDescriptor: String?,
                    daggerAnnotation: String,
                    annotationReplacements: Replacements) {

                annotationReplacements[annotationDescriptor]?.apply {
                    addAnnotations(methodOriginal, daggerAnnotation, this.replacement)
                }
            }
        }
    }

    private fun addAnnotations(method: MethodVisitor, vararg annotations: String) {
        annotations.forEach { annotation ->
            method.visitAnnotation(annotation, true)
        }
    }

    private companion object {
        private const val CONSTRUCTOR_NAME = "<init>"
        private const val BINDS_DESCRIPTOR = "Ldagger/Binds;"
        private const val PROVIDES_DESCRIPTOR = "Ldagger/Provides;"
    }
}
