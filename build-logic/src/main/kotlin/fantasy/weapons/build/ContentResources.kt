package fantasy.weapons.build

import com.google.gson.GsonBuilder

private val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
internal fun json(value: Any): String = gson.toJson(value) + "\n"

fun catalogResources(content: ContentCatalog): Map<String, String> = mapOf(
    "weapon_catalog.json" to json(mapOf(
        "tabIcon" to content.tabIcon,
        "materials" to content.materials.map { it.id },
        "weapons" to content.weapons.map { mapOf("id" to it.id, "largeModel" to (it.largeModel != null)) },
    )),
)

fun contentResources(content: ContentCatalog, languages: TranslationCatalog, minecraft: String): Map<String, String> {
    require(minecraft == "1.20.1") { "No resource format declared for Minecraft $minecraft" }
    languages.validate(content)
    return buildMap {
        val assets = "assets/${content.namespace}"
        val recipes = "data/${content.namespace}/recipes"
        fun model(id: String, parent: String) = json(mapOf(
            "parent" to parent, "textures" to mapOf("layer0" to "${content.namespace}:item/$id"),
        ))
        for (material in content.materials) {
            put("$assets/models/item/${material.id}.json", model(material.id, "item/generated"))
            put("$recipes/${material.id}.json", json(mapOf(
                "type" to "minecraft:crafting_shaped", "pattern" to material.recipe.pattern,
                "key" to material.recipe.ingredients.mapKeys { it.key.toString() }.mapValues { mapOf("item" to it.value) },
                "result" to mapOf("item" to "${content.namespace}:${material.id}", "count" to material.recipe.count),
            )))
        }
        for (weapon in content.weapons) {
            put("$assets/models/item/${weapon.id}.json", model(weapon.id, "item/handheld"))
            weapon.largeModel?.let { large ->
                put("$assets/models/item/${weapon.id}_large.json", model("${weapon.id}_large", "${content.namespace}:item/${large.parent}"))
            }
            put("$recipes/${weapon.id}.json", json(mapOf(
                "type" to "minecraft:smithing_transform", "category" to "equipment",
                "template" to mapOf("item" to weapon.template), "base" to mapOf("item" to weapon.base),
                "addition" to mapOf("item" to content.addition),
                "result" to mapOf("item" to "${content.namespace}:${weapon.id}"),
            )))
        }
        languages.languages.forEach { (locale, entries) -> put("$assets/lang/$locale.json", json(entries)) }
    }
}
