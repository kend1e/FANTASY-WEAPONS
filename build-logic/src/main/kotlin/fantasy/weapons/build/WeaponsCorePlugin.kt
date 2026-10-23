package fantasy.weapons.build

import fantasy.gradle.generateModConstants
import fantasy.gradle.PlatformExtension
import fantasy.gradle.ResourcesExtension
import fantasy.weapons.definitions.content
import fantasy.gradle.content.weapons.catalogResources
import fantasy.weapons.definitions.mod
import fantasy.weapons.definitions.targets
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

class WeaponsCorePlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        pluginManager.apply("fantasy.build.shared")
        extensions.configure<PlatformExtension> { javaVersion.set(targets.minOf { it.java }) }
        extensions.configure<ResourcesExtension> {
            generate("generateContentCatalog") {
                contents.set(catalogResources(content))
                outputDirectory.set(layout.buildDirectory.dir("generated/resources/catalog"))
            }
        }
        generateModConstants("${mod.group}.shared", mapOf(
            "MOD_ID" to mod.id, "CREATIVE_TAB_TITLE" to content.tabTranslationKey,
        ))
    }
}
