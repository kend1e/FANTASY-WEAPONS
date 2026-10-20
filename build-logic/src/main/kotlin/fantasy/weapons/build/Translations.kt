package fantasy.weapons.build

data class TranslationCatalog(val namespace: String, val languages: Map<String, Map<String, String>>) {
    fun validate(content: ContentCatalog) {
        require(namespace == content.namespace && "en_us" in languages) { "English translations and matching namespace are required" }
        val expected = content.itemIds.map { "item.$namespace.$it" }.toSet()
        languages.forEach { (locale, entries) ->
            val items = entries.keys.filter { it.startsWith("item.$namespace.") }.toSet()
            require(items == expected && content.tabTranslationKey in entries) {
                "$locale: missing translations ${expected - items}; unknown translations ${items - expected}"
            }
        }
    }
}

@ContentDsl
class LanguageBuilder internal constructor(private val namespace: String) {
    private val entries = linkedMapOf<String, String>()
    fun tabTitle(name: String) = entry(creativeTabTranslationKey(namespace), name)
    fun item(id: String, name: String) = entry("item.$namespace.$id", name)
    fun entry(key: String, value: String) {
        require(value.isNotBlank()) { "Empty translation: $key" }
        require(entries.putIfAbsent(key, value) == null) { "Duplicate translation: $key" }
    }
    internal fun build(): Map<String, String> = entries.toMap()
}

@ContentDsl
class TranslationsBuilder internal constructor(private val namespace: String) {
    private val languages = linkedMapOf<String, Map<String, String>>()
    fun language(locale: String, configure: LanguageBuilder.() -> Unit) {
        require(locale.matches(Regex("[a-z][a-z0-9_-]*"))) { "Invalid locale: $locale" }
        require(locale !in languages) { "Duplicate locale: $locale" }
        languages[locale] = LanguageBuilder(namespace).apply(configure).build()
    }
    internal fun build() = TranslationCatalog(namespace, languages.toMap())
}

fun translations(namespace: String, configure: TranslationsBuilder.() -> Unit): TranslationCatalog =
    TranslationsBuilder(namespace).apply(configure).build()
