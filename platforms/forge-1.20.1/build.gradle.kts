import fantasy.weapons.build.WeaponsTestRun

plugins {
    id("fantasy.weapons.platform")
    id("net.minecraftforge.gradle")
}

minecraft.runs.named("gameTestServer") {
    with(sourceSets.test.get()) {
        mods.register("fantasy_weapons") { source(sourceSets.main.get()) }
        mods.register("fantasy_weapons_tests") { source(sourceSets.test.get()) }
        systemProperty("forge.enabledGameTestNamespaces", "fantasy_weapons_tests")
        systemProperty("forge.logging.console.level", "info")
        systemProperty("fantasy_weapons.test.report", project.extensions.getByType<WeaponsTestRun>().report.get().asFile.absolutePath)
        workingDir.set(layout.buildDirectory.dir("run/test"))
    }
}
