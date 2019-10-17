package dagger.magic

open class DaggerMagicGradleExtension {
    var enabled: Boolean = true
    var moduleAllStaticAnnotation: String = ""
    var providesAnnotations: List<String> = emptyList()
    var bindsAnnotations: List<String> = emptyList()
}
