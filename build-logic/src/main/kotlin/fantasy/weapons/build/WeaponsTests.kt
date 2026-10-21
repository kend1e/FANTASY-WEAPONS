package fantasy.weapons.build

import fantasy.gradle.Loader
import fantasy.gradle.Target
import fantasy.gradle.minecraft.MinecraftVersions
import fantasy.gradle.resources.json
import fantasy.gradle.testing.*
import fantasy.weapons.definitions.*
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

fun Project.configureWeaponsTests() {
    validateTestCatalog(weaponTests, targets)
    val target = extensions.getByType<Target>()
    val version = MinecraftVersions[target.minecraft]
    val profile = providers.gradleProperty("testProfile").getOrElse("defaults")
    require(profile in setOf("defaults", "configured")) { "Unknown test profile: $profile" }
    val changed = content.weapons.first().id
    val damage = 17.0
    val speed = -1.25
    val configPath = when (target.loader) {
        Loader.FABRIC -> "config/${mod.id}.json"
        else -> "${version.loader(target.loader).testServerConfigDirectory}/${mod.id}-server.toml"
    }
    configureModTests(TestSuite(
        module = TestModule("${mod.id}_tests", "net.kenddie.fantasyweapons.test", "WeaponTests", "${mod.id}.test.report"),
        cases = weaponTests,
        sourceDirectories = listOf("tests/common/java", "tests/minecraft/${target.minecraft}/java",
            "loaders/${target.loader.id}/src/test/java", "tests/client/java"),
        profile = profile,
        resources = mapOf("weapon_test_expectations.json" to json(mapOf(
            "changed" to if (profile == "configured") changed else "", "damage" to damage, "speed" to speed,
            "addition" to content.addition, "weapons" to content.weapons, "materials" to content.materials))),
        configFiles = mapOf(configPath to if (target.loader == Loader.FABRIC)
            json(mapOf("weapons" to mapOf(changed to mapOf("damage" to damage, "speed" to speed))))
            else content.weapons.joinToString("\n") { weapon ->
                val fixtureDamage = if (weapon.id == changed) damage else 9.0
                val fixtureSpeed = if (weapon.id == changed) speed else -2.4
                "[weapons.${weapon.id}]\ndamage = $fixtureDamage\nspeed = $fixtureSpeed\n"
            }),
        configured = profile == "configured",
        modId = mod.id,
        displayName = "${mod.name} Tests",
        license = mod.license,
    ))
}
