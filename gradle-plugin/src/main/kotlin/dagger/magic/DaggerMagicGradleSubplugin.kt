package dagger.magic

import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinGradleSubplugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class DaggerMagicGradleSubplugin : KotlinGradleSubplugin<AbstractCompile> {

    override fun isApplicable(project: Project, task: AbstractCompile) =
            project.plugins.hasPlugin(DaggerMagicGradlePlugin::class.java)

    override fun apply(
            project: Project,
            kotlinCompile: AbstractCompile,
            javaCompile: AbstractCompile?,
            variantData: Any?,
            androidProjectHandler: Any?,
            kotlinCompilation: KotlinCompilation<KotlinCommonOptions>?
    ): List<SubpluginOption> {
        val extension = project.extensions.findByType(DaggerMagicGradleExtension::class.java)
                ?: DaggerMagicGradleExtension()

        return listOf(SubpluginOption(key = "enabled", value = extension.enabled.toString()))
                .addOptionList("bindsAnnotations", extension.bindsAnnotations)
                .addOptionList("providesAnnotations", extension.providesAnnotations)
    }

    private fun List<SubpluginOption>.addOptionList(
            key: String,
            annotationsList: List<String>): List<SubpluginOption> {

        return plus(annotationsList.map {
            SubpluginOption(key = key, value = it)
        })
    }

    override fun getCompilerPluginId(): String = "dagger-magic"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
            groupId = "net.lachlanmckee",
            artifactId = "dagger-magic-kotlin-plugin",
            version = "0.0.4"
    )
}
