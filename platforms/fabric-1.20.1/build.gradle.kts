import fantasy.weapons.build.WeaponsTestRun

plugins {
    id("fantasy.weapons.platform")
    id("fabric-loom")
}

fabricApi.configureTests {
    createSourceSet.set(false)
    enableClientGameTests.set(false)
}
loom.mods {
    register("fantasy_weapons") { sourceSet(sourceSets.main.get()) }
    register("fantasy_weapons_tests") { sourceSet(sourceSets.test.get()) }
}
loom.runs.named("gameTest") {
    source(sourceSets.test.get())
    runDir("build/run/test")
    property("fabric-api.gametest.report-file", project.extensions.getByType<WeaponsTestRun>().report.get().asFile.absolutePath)
}
