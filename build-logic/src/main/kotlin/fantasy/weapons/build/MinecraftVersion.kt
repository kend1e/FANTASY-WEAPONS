package fantasy.weapons.build

import fantasy.gradle.MinecraftRuntime
import fantasy.gradle.Loader

data class MinecraftVersion(
    val runtime: MinecraftRuntime,
    val dataPackFormat: Int,
    val resourcePackFormat: Int,
    val swordTags: Boolean,
    val recipeDirectory: String,
    val resultField: String,
    val structureDirectory: String,
    val tests: Map<Loader, TestRegistration>,
) {
    fun packMetadata(description: String): String = json(mapOf("pack" to buildMap<String, Any> {
        put("description", description)
        put("pack_format", dataPackFormat)
        if (resourcePackFormat != dataPackFormat) put("supported_formats", mapOf(
            "min_inclusive" to minOf(dataPackFormat, resourcePackFormat),
            "max_inclusive" to maxOf(dataPackFormat, resourcePackFormat),
        ))
    }))
}
