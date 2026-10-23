package fantasy.weapons.build

import fantasy.gradle.includeModTargets
import fantasy.weapons.definitions.mod
import fantasy.weapons.definitions.targets
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class WeaponsSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        settings.rootProject.name = mod.releaseName
        settings.include(":core")
        settings.includeModTargets(targets)
    }
}
