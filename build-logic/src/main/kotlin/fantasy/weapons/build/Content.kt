package fantasy.weapons.build

@DslMarker
annotation class ContentDsl

enum class LargeModel(val parent: String) {
    DOUBLE("handheld_large_2x"), TRIPLE("handheld_large_3x")
}

data class ShapedRecipe(val pattern: List<String>, val ingredients: Map<Char, String>, val count: Int = 1) {
    init {
        require(pattern.size in 1..3 && pattern.first().length in 1..3 && pattern.all { it.length == pattern.first().length }) { "Invalid crafting pattern" }
        require(pattern.flatMap(String::toList).filter { it != ' ' }.toSet() == ingredients.keys && ' ' !in ingredients) { "Pattern symbols must match ingredients" }
        require(count in 1..64) { "Invalid result count" }
        ingredients.values.forEach(::validateResourceId)
    }
}

data class MaterialDefinition(val id: String, val recipe: ShapedRecipe)
data class WeaponDefinition(val id: String, val template: String, val base: String, val largeModel: LargeModel?)
data class ContentCatalog(
    val namespace: String, val tabIcon: String, val addition: String,
    val materials: List<MaterialDefinition>, val weapons: List<WeaponDefinition>,
) {
    val itemIds get() = materials.map { it.id } + weapons.map { it.id }
    val tabTranslationKey get() = creativeTabTranslationKey(namespace)

    init {
        require(namespace.matches(Regex("[a-z][a-z0-9_]*"))) { "Invalid namespace: $namespace" }
        require(itemIds.isNotEmpty() && itemIds.distinct().size == itemIds.size) { "Duplicate or empty item catalog" }
        itemIds.forEach { require(it.matches(Regex("[a-z0-9_]+"))) { "Invalid item ID: $it" } }
        require(tabIcon in itemIds) { "Unknown creative tab icon: $tabIcon" }
        (listOf(addition) + weapons.flatMap { listOf(it.template, it.base) } + materials.flatMap { it.recipe.ingredients.values }).forEach { id ->
            validateResourceId(id)
            require(!id.startsWith("$namespace:") || id.substringAfter(':') in itemIds) { "Unknown local ingredient: $id" }
        }
    }
}

internal fun validateResourceId(id: String) {
    require(id.matches(Regex("[a-z0-9_.-]+:[a-z0-9_./-]+"))) { "Invalid resource ID: $id" }
}

@ContentDsl
class ContentBuilder internal constructor(
    private val namespace: String, private val tabIcon: String,
    private val base: String, private val addition: String,
) {
    private val materials = mutableListOf<MaterialDefinition>()
    private val weapons = mutableListOf<WeaponDefinition>()
    fun material(id: String, recipe: ShapedRecipe) { materials += MaterialDefinition(id, recipe) }
    fun weapon(id: String, template: String, largeModel: LargeModel? = LargeModel.TRIPLE, base: String = this.base) {
        weapons += WeaponDefinition(id, template, base, largeModel)
    }
    internal fun build() = ContentCatalog(namespace, tabIcon, addition, materials.toList(), weapons.toList())
}

fun contentCatalog(namespace: String, tabIcon: String, base: String, addition: String, configure: ContentBuilder.() -> Unit): ContentCatalog =
    ContentBuilder(namespace, tabIcon, base, addition).apply(configure).build()

internal fun creativeTabTranslationKey(namespace: String) = "itemGroup.$namespace.fw_tab"
