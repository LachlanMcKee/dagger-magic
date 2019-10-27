package dagger.magic

import dagger.magic.model.Replacement
import dagger.magic.model.Replacements
import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.org.objectweb.asm.*

internal class DaggerMagicClassBuilder(
        private val providesAnnotations: Replacements,
        private val bindsAnnotations: Replacements,
        private val delegateBuilder: ClassBuilder
) : DelegatingClassBuilder() {

    private var isModuleClass = false
    private var nonStaticMethodNames = mutableListOf<String>()
    private var classLevelProvidesAnnotation: Replacement? = null
    private var classLevelBindsAnnotation: Replacement? = null

    override fun getDelegate(): ClassBuilder {
        return delegateBuilder
    }

    override fun getVisitor(): ClassVisitor {
        val originalVisitor = super.getVisitor()
        return object : ClassVisitor(Opcodes.ASM5, originalVisitor) {
            override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
                if (descriptor?.equals(MODULE_DESCRIPTOR) == true) {
                    isModuleClass = true
                }

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
        if (!isModuleClass || name == CONSTRUCTOR_NAME) {
            return methodOriginal
        }

        if (!doesAccessContain(access, Opcodes.ACC_STATIC)) {
            nonStaticMethodNames.add(name)
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

    override fun newField(origin: JvmDeclarationOrigin, access: Int, name: String, desc: String, signature: String?, value: Any?): FieldVisitor {
        val newField = super.newField(origin, access, name, desc, signature, value)
        if (!isModuleClass) {
            return newField
        }

        // Check for a kotlin object 'INSTANCE' class.
        val isObjectInstanceField = name == INSTANCE_FIELD_NAME &&
                desc == "L$thisName;" &&
                access == (Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC or Opcodes.ACC_FINAL)

        check(!(isObjectInstanceField && nonStaticMethodNames.isNotEmpty())) {
            "Not all methods are static: ${nonStaticMethodNames.joinToString()}"
        }

        return newField
    }

    private fun addAnnotations(method: MethodVisitor, vararg annotations: String) {
        annotations.forEach { annotation ->
            method.visitAnnotation(annotation, true)
        }
    }

    private fun doesAccessContain(access: Int, code: Int): Boolean {
        return (access and code == code)
    }

    private companion object {
        private const val CONSTRUCTOR_NAME = "<init>"
        private const val MODULE_DESCRIPTOR = "Ldagger/Module;"
        private const val BINDS_DESCRIPTOR = "Ldagger/Binds;"
        private const val PROVIDES_DESCRIPTOR = "Ldagger/Provides;"
        private const val INSTANCE_FIELD_NAME = "INSTANCE"
    }
}
