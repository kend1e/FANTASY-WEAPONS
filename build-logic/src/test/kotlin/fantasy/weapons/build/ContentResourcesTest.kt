package fantasy.weapons.build

import com.google.gson.JsonParser
import fantasy.weapons.definitions.content
import fantasy.weapons.definitions.languages
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class ContentResourcesTest {
    @Test fun preservesCatalogAndTranslationCoverage() {
        val files = contentResources(content, languages, "1.20.1")
        val large = JsonParser.parseString(files.getValue("assets/fantasy_weapons/models/item/weapon_savage_sword_large.json")).asJsonObject
        assertEquals("fantasy_weapons:item/weapon_savage_sword_large", large.getAsJsonObject("textures").get("layer0").asString)
        assertFalse(files.containsKey("assets/fantasy_weapons/models/item/weapon_wakizashi_large.json"))
        val resources = File(System.getProperty("staticResourcesDirectory"))
        files.filterKeys { "/models/item/" in it }.forEach { (_, text) ->
            val model = JsonParser.parseString(text).asJsonObject
            val parent = model.get("parent").asString
            if (parent.startsWith("${content.namespace}:")) {
                val parentPath = "assets/${content.namespace}/models/${parent.substringAfter(':')}.json"
                assertTrue("Missing model parent: $parent", parentPath in files || resources.resolve(parentPath).isFile)
            }
            model.getAsJsonObject("textures").entrySet().forEach { (_, texture) ->
                val path = texture.asString.substringAfter(':')
                assertTrue("Missing texture: $path", resources.resolve("assets/${content.namespace}/textures/$path.png").isFile)
            }
        }
        val preview = File(System.getProperty("contentPreviewDirectory"))
        preview.deleteRecursively()
        files.forEach { (path, contents) -> preview.resolve(path).apply { parentFile.mkdirs(); writeText(contents) } }
        catalogResources(content).forEach { (path, contents) -> preview.resolve(path).writeText(contents) }
    }

    @Test fun preservesUpgradeBases() {
        val files = contentResources(content, languages, "1.20.1")
        val recipe = JsonParser.parseString(files.getValue("data/fantasy_weapons/recipes/weapon_infected_sharp_sword.json")).asJsonObject
        assertEquals("fantasy_weapons:weapon_sharp_sword", recipe.getAsJsonObject("base").get("item").asString)
    }

    @Test fun rejectsDuplicateItemsAndInvalidPatterns() {
        assertThrows(IllegalArgumentException::class.java) {
            contentCatalog("test", "a", "minecraft:stick", "minecraft:stone") {
                weapon("a", "minecraft:coal"); weapon("a", "minecraft:coal")
            }
        }
        assertThrows(IllegalArgumentException::class.java) { ShapedRecipe(listOf("XX"), mapOf('Y' to "minecraft:stone")) }
    }

    @Test fun rejectsMissingTranslationsAndUnsupportedFormats() {
        assertThrows(IllegalArgumentException::class.java) {
            contentResources(content, translations(content.namespace) { language("en_us") { entry(content.tabTranslationKey, "Weapons") } }, "1.20.1")
        }
        assertThrows(IllegalArgumentException::class.java) { contentResources(content, languages, "1.21.1") }
    }
}
