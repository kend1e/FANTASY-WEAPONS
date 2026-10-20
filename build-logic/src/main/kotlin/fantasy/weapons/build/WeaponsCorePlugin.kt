package fantasy.weapons.build

import fantasy.gradle.GenerateFiles
import fantasy.gradle.PlatformExtension
import fantasy.gradle.ResourcesExtension
import fantasy.weapons.definitions.content
import fantasy.weapons.definitions.mod
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.*

class WeaponsCorePlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        pluginManager.apply("fantasy.build.shared")
        extensions.configure<PlatformExtension> { javaVersion.set(17) }
        extensions.configure<ResourcesExtension> {
            generate("generateContentCatalog") {
                contents.set(catalogResources(content))
                outputDirectory.set(layout.buildDirectory.dir("generated/resources/catalog"))
            }
        }
        val constants = tasks.register<GenerateFiles>("generateModConstants") {
            contents.set(mapOf("net/kenddie/fantasyweapons/shared/ModConstants.java" to """
                package net.kenddie.fantasyweapons.shared;

                public final class ModConstants {
                    public static final String MOD_ID = "${mod.id}";
                    public static final String CREATIVE_TAB_TITLE = "${content.tabTranslationKey}";
                    private ModConstants() {}
                }
            """.trimIndent()))
            outputDirectory.set(layout.buildDirectory.dir("generated/sources/constants"))
        }
        extensions.configure<SourceSetContainer> {
            named("main") { java.srcDir(constants.flatMap { it.outputDirectory }) }
        }
        artifacts.add("sharedSources", constants.flatMap { it.outputDirectory }) { type = "directory" }
    }
}
