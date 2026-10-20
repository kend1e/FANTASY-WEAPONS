package fantasy.weapons.build

import fantasy.gradle.Loader
import fantasy.gradle.PlatformExtension
import fantasy.gradle.ResourcesExtension
import fantasy.gradle.configureMod
import fantasy.weapons.definitions.content
import fantasy.weapons.definitions.languages
import fantasy.weapons.definitions.mod
import fantasy.weapons.definitions.targets
import fantasy.gradle.ExportRelease
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.*

class WeaponsPlatformPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        val target = targets.single { it.path == path }
        configureMod(mod, target)
        pluginManager.apply("fantasy.build.${target.loader.id}")
        val root = rootProject.layout.projectDirectory
        extensions.configure<PlatformExtension> {
            sources.from(root.dir("minecraft/${target.minecraft}/src/main/java"))
            sources.from(root.dir("loaders/${target.loader.id}/src/main/java"))
        }
        dependencies.add("sharedSourcesDependencies", dependencies.project(mapOf("path" to ":core", "configuration" to "sharedSources")))
        dependencies.add("sharedResourcesDependencies", dependencies.project(mapOf("path" to ":core", "configuration" to "sharedResources")))
        extensions.configure<ResourcesExtension> {
            sources.from(root.dir("resources"))
            generate("generateContentResources") {
                contents.set(contentResources(content, languages, target.minecraft))
                outputDirectory.set(layout.buildDirectory.dir("generated/resources/content"))
            }
        }
        tasks.named("generateResources") { dependsOn(configurations.named("sharedResourceFiles")) }
        extensions.configure<SourceSetContainer> {
            named("test") { java.srcDir(root.dir("loaders/${target.loader.id}/src/test/java")) }
        }
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        dependencies.add("testImplementation", libs.findLibrary("junit").get())
        configureWeaponsTests()
        if (target.loader == Loader.FORGE) extensions.configure<PublishingExtension> {
            val release = tasks.named<ExportRelease>("exportRelease")
            publications.register<MavenPublication>("mavenJava") {
                artifactId = mod.id
                artifact(release.flatMap { it.destination }) { builtBy(release) }
            }
            repositories.maven { url = layout.projectDirectory.dir("mcmodsrepo").asFile.toURI() }
        }
    }
}
