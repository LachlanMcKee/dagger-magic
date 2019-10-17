package dagger.magic

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

@AutoService(CommandLineProcessor::class)
class DaggerMagicCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = "dagger-magic"

    override val pluginOptions: Collection<AbstractCliOption> = listOf(
            CliOption(
                    optionName = "enabled",
                    valueDescription = "<true|false>",
                    description = "whether the dagger magic plugin is enabled"
            ),
            CliOption(
                    optionName = "moduleAllStaticAnnotation",
                    valueDescription = "<classpath>",
                    description = "fully qualified name of the annotation used to define a module in which all " +
                            "non-abstract functions should be static",
                    required = true
            ),
            CliOption(
                    optionName = "bindsAnnotations",
                    valueDescription = "<array-of-string>",
                    description = "a list of fully qualified annotations which must be added in multiples of two, " +
                            "the first element of the pair is the source, and the second element is the replacement." +
                            "The dagger @dagger.Binds annotation is also added where the replacement occurs",
                    required = true,
                    allowMultipleOccurrences = true
            ),
            CliOption(
                    optionName = "providesAnnotations",
                    valueDescription = "<array-of-string>",
                    description = "a list of fully qualified annotations which must be added in multiples of two, " +
                            "the first element of the pair is the source, and the second element is the replacement." +
                            "The dagger @dagger.Provides annotation is also added where the replacement occurs",
                    required = true,
                    allowMultipleOccurrences = true
            )
    )

    override fun processOption(
            option: AbstractCliOption,
            value: String,
            configuration: CompilerConfiguration
    ) = when (option.optionName) {
        "enabled" -> configuration.put(KEY_ENABLED, value.toBoolean())
        "moduleAllStaticAnnotation" -> configuration.put(KEY_KOTLIN_OBJECT_MODULE_ANNOTATION, value)
        "bindsAnnotations" -> configuration.appendList(KEY_BINDS_ANNOTATIONS, value)
        "providesAnnotations" -> configuration.appendList(KEY_PROVIDES_ANNOTATIONS, value)
        else -> error("Unexpected config option ${option.optionName}")
    }
}

val KEY_ENABLED = CompilerConfigurationKey<Boolean>("plugin-enabled")
val KEY_KOTLIN_OBJECT_MODULE_ANNOTATION = CompilerConfigurationKey<String>("all-static-module-annotation")
val KEY_BINDS_ANNOTATIONS = CompilerConfigurationKey<List<String>>("bind-replacement-annotations")
val KEY_PROVIDES_ANNOTATIONS = CompilerConfigurationKey<List<String>>("provide-replacement-annotations")