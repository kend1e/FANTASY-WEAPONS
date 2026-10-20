package fantasy.weapons.build

enum class TestRegistration(
    val runTask: String,
    val configDirectory: String,
    private val reportPrefix: String,
    val fmlPackage: String?,
    private val gameTestPackage: String?,
) {
    FORGE("runTestGameTestServer", "world/serverconfig", "", "net.minecraftforge.fml", "net.minecraftforge.gametest"),
    FORGE_QUALIFIED_TEMPLATE("runTestGameTestServer", "world/serverconfig", "fantasy_weapons_tests.", "net.minecraftforge.fml", "net.minecraftforge.gametest"),
    NEOFORGE("runGameTestServer", "config", "", "net.neoforged.fml", "net.neoforged.neoforge.gametest"),
    FABRIC("runGameTest", "config", "weapontests.", null, null);

    val fabric get() = this == FABRIC
    val metadataFile get() = if (this == NEOFORGE) "neoforge.mods.toml" else "mods.toml"
    fun reportName(id: String) = reportPrefix + id

    fun annotation(): String = when (this) {
        FABRIC -> "@net.minecraft.gametest.framework.GameTest(template = \"fabric-gametest-api-v1:empty\")"
        FORGE_QUALIFIED_TEMPLATE -> "@net.minecraft.gametest.framework.GameTest(template = \"fantasy_weapons_tests:empty\")"
        else -> "@net.minecraft.gametest.framework.GameTest(template = \"empty\", templateNamespace = \"fantasy_weapons_tests\")"
    }

    fun holder(): String = if (fabric) "" else buildString {
        append("@$gameTestPackage.GameTestHolder(\"fantasy_weapons_tests\")")
        if (this@TestRegistration != FORGE_QUALIFIED_TEMPLATE)
            append("\n@$gameTestPackage.PrefixGameTestTemplate(false)")
    }
}
