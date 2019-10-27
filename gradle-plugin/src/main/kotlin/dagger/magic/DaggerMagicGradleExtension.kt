package dagger.magic

open class DaggerMagicGradleExtension {
    var enabled: Boolean = true
    var providesAnnotations: List<String> = emptyList()
    var bindsAnnotations: List<String> = emptyList()
}
