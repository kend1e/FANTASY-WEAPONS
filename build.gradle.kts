plugins { base }

val modules = subprojects.filter { it.path == ":core" || it.path.startsWith(":platforms:") }
for (taskName in listOf("build", "assemble", "check", "clean")) {
    tasks.named(taskName) { dependsOn(modules.map { "${it.path}:$taskName" }) }
}
tasks.named("check") { dependsOn(gradle.includedBuild("build-logic").task(":check")) }
tasks.register("generateResources") {
    group = "build"
    description = "Generates catalogs, recipes, item models, metadata and translations."
    dependsOn(modules.map { "${it.path}:generateResources" })
}

tasks.register("test") {
    group = "verification"
    description = "Runs unit tests and Minecraft server tests."
    dependsOn(modules.map { "${it.path}:test" })
}
