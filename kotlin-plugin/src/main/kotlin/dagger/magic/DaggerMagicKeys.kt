package dagger.magic

import org.jetbrains.kotlin.config.CompilerConfigurationKey

typealias BooleanConfigurationKey = CompilerConfigurationKey<Boolean>
typealias ListConfigurationKey = CompilerConfigurationKey<List<String>>

object DaggerMagicKeys {
    val KEY_ENABLED = BooleanConfigurationKey("plugin-enabled")
    val KEY_BINDS_ANNOTATIONS = ListConfigurationKey("bind-replacement-annotations")
    val KEY_PROVIDES_ANNOTATIONS = ListConfigurationKey("provide-replacement-annotations")
}