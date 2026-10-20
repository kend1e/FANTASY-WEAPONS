package fantasy.weapons.definitions

import fantasy.gradle.Loader
import fantasy.gradle.MinecraftRuntime
import fantasy.weapons.build.MinecraftVersion
import fantasy.weapons.build.TestRegistration

val minecraftVersions = mapOf(
    "1.20.1" to MinecraftVersion(
        runtime = MinecraftRuntime("1.20.1", 17),
        dataPackFormat = 15, resourcePackFormat = 15,
        recipeDirectory = "recipes", resultField = "item", structureDirectory = "structures",
        swordTags = false,
        tests = mapOf(Loader.FORGE to TestRegistration.FORGE, Loader.FABRIC to TestRegistration.FABRIC),
    ),
    "1.21.1" to MinecraftVersion(
        runtime = MinecraftRuntime("1.21.1", 21),
        dataPackFormat = 48, resourcePackFormat = 34,
        recipeDirectory = "recipe", resultField = "id", structureDirectory = "structure",
        swordTags = true,
        tests = mapOf(
            Loader.FORGE to TestRegistration.FORGE_QUALIFIED_TEMPLATE,
            Loader.NEOFORGE to TestRegistration.NEOFORGE,
            Loader.FABRIC to TestRegistration.FABRIC,
        ),
    ),
)
