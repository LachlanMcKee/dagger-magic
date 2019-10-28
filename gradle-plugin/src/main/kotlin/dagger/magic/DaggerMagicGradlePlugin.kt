package dagger.magic

import org.gradle.api.Plugin
import org.gradle.api.Project

class DaggerMagicGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("daggerMagic", DaggerMagicGradleExtension::class.java)
    }
}