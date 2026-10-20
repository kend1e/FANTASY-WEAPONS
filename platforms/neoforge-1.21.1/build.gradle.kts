import fantasy.weapons.build.WeaponsTestRun
import org.slf4j.event.Level

plugins {
    id("fantasy.weapons.platform")
    id("net.neoforged.moddev")
}

val testReport = extensions.getByType<WeaponsTestRun>().report

neoForge {
    mods.register("fantasy_weapons_tests") { sourceSet(sourceSets.test.get()) }
    runs.named("gameTestServer") {
        logLevel = Level.INFO
        sourceSet.set(sourceSets.test.get())
        gameDirectory.set(layout.buildDirectory.dir("run/test"))
        systemProperty("neoforge.enabledGameTestNamespaces", "fantasy_weapons_tests")
        systemProperty("fantasy_weapons.test.report", testReport.get().asFile.absolutePath)
    }
    runs.matching { it.name != "gameTestServer" }.configureEach {
        loadedMods.set(setOf(mods.getByName("fantasy_weapons")))
    }
}
