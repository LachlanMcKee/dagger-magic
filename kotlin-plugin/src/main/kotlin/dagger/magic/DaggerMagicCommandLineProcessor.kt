package dagger.magic

import dagger.magic.DaggerMagicKeys.KEY_BINDS_ANNOTATIONS
import dagger.magic.DaggerMagicKeys.KEY_ENABLED
import dagger.magic.DaggerMagicKeys.KEY_PROVIDES_ANNOTATIONS
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration

class DaggerMagicCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = "dagger-magic"

    override val pluginOptions: Collection<AbstractCliOption> = listOf(
            CliOption(
                    optionName = "enabled",
                    valueDescription = "<true|false>",
                    description = "whether the dagger magic plugin is enabled"
            ),
            CliOption(
                    optionName = "bindsAnnotations",
                    valueDescription = "<array-of-string>",
                    description = """
                        "a list of fully qualified annotations which must be added in multiples of
                        two, the first element of the pair is the source, and the second element is
                        the replacement. The dagger @dagger.Binds annotation is also added where 
                        the replacement occurs
                    """.trimIndent(),
                    required = false,
                    allowMultipleOccurrences = true
            ),
            CliOption(
                    optionName = "providesAnnotations",
                    valueDescription = "<array-of-string>",
                    description = """
                        "a list of fully qualified annotations which must be added in multiples of
                        two, the first element of the pair is the source, and the second element is
                        the replacement. The dagger @dagger.Provides annotation is also added where 
                        the replacement occurs
                    """.trimIndent(),
                    required = false,
                    allowMultipleOccurrences = true
            )
    )

    override fun processOption(
            option: AbstractCliOption,
            value: String,
            configuration: CompilerConfiguration
    ) = when (option.optionName) {
        "enabled" -> configuration.put(KEY_ENABLED, value.toBoolean())
        "bindsAnnotations" -> configuration.appendList(KEY_BINDS_ANNOTATIONS, value)
        "providesAnnotations" -> configuration.appendList(KEY_PROVIDES_ANNOTATIONS, value)
        else -> error("Unexpected config option ${option.optionName}")
    }
}
