import fantasy.weapons.build.WeaponsTestRun

plugins {
    id("fantasy.weapons.platform")
    id("net.minecraftforge.gradle")
}

val testReport = extensions.getByType<WeaponsTestRun>().report

minecraft.runs.named("gameTestServer") {
    with(sourceSets.test.get()) {
        systemProperty("forge.enabledGameTestNamespaces", "fantasy_weapons_tests")
        systemProperty("forge.logging.console.level", "info")
        systemProperty("fantasy_weapons.test.report", testReport.get().asFile.absolutePath)
        workingDir.set(layout.buildDirectory.dir("run/test"))
    }
}
