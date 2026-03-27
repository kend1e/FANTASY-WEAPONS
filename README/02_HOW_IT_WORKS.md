# How This Repository Works

This document provides a complete, detailed explanation of the Fantasy Weapons repository architecture, build system, data flow, and runtime behavior.

---

## Table of Contents

1. [Repository Structure Overview](#repository-structure-overview)
2. [The `appCommon` Module](#the-appcommon-module)
3. [The Loader Modules](#the-loader-modules)
4. [The Build System](#the-build-system)
5. [Data Flow: From JSON to Minecraft](#data-flow-from-json-to-minecraft)
6. [The Config System](#the-config-system)
7. [The Model System (Large/Small Weapons)](#the-model-system-largesmall-weapons)
8. [How JAR Files Are Built](#how-jar-files-are-built)

---

## Repository Structure Overview

```
FANTASY-WEAPONS/
├── appCommon/                      # Shared pure-Java module
│   ├── build.gradle                # Java 17 compilation only
│   └── src/main/
│       ├── java/                   # Shared Java code
│       └── resources/              # All textures, models, recipes
│
├── appFabric/                      # Fabric loader implementations
│   ├── 1.21.1/                     # MC 1.21.1 + Fabric
│   ├── 1.21.4/                     # MC 1.21.4 + Fabric
│   ├── 1.21.10/                    # MC 1.21.10 + Fabric (renamed from 1.21.3 snapshots)
│   └── 1.21.11/                    # MC 1.21.11 + Fabric (renamed from 1.21.4 snapshots)
│
├── appNeoForge/                    # NeoForge loader implementations
│   ├── 1.21.1/                     # MC 1.21.1 + NeoForge
│   ├── 1.21.4/                     # MC 1.21.4 + NeoForge
│   ├── 1.21.10/                    # MC 1.21.10 + NeoForge
│   └── 1.21.11/                    # MC 1.21.11 + NeoForge
│
├── settings.gradle                 # Declares all subprojects
├── build.gradle                    # Root project (minimal)
└── gradle.properties               # Global properties
```

### Understanding the Version-per-Directory Pattern

Each Minecraft version has its own directory because **Minecraft's APIs change between versions**. While the core logic is shared via `appCommon`, the following are version-specific:

- Registration APIs (how items get registered)
- Model APIs (how models are loaded and rendered)
- Weapon class inheritance (SwordItem vs Item)
- Resource location classes (ResourceLocation vs Identifier)

---

## The `appCommon` Module

### Purpose

`appCommon` is a **pure Java library** with no Minecraft dependencies. It contains:
- Constants (mod ID, weapon names)
- Configuration classes
- Default values loading
- All shared resources (textures, models, recipes, languages)

### Package Structure

```
net.kenddie.fantasyweapons/
├── FantasyWeapons.java                 # Constants
├── config/
│   ├── FWConfig.java                   # YAML config definition
│   └── FWConfigManager.java            # ConfigLib wrapper
└── shared/
    ├── config/
    │   └── FWWeaponDefaults.java       # JSON defaults loader
    └── weapon/
        └── FWWeaponAttributes.java     # Weapon stats record
```

### `FantasyWeapons.java`

This class holds all constants used across the mod:

```java
public final class FantasyWeapons {
    public static final String MOD_ID = "fantasy_weapons";

    public static final String[] BASIC_ITEM_NAMES = {
        "blazing_stone"
    };

    public static final String[] WEAPON_NAMES = {
        "weapon_fates_beacon",
        "weapon_dragonslayer_halberd",
        "weapon_eclipse_soldier_spear",
        // ... 61 weapons total
        "weapon_dancing_blade_of_ranah"
    };
}
```

**How it's used:**
- `MOD_ID` is used in resource locations, registry names, config paths
- `BASIC_ITEM_NAMES` defines non-weapon items (materials, etc.)
- `WEAPON_NAMES` is iterated during registration to create all weapons

### `FWWeaponAttributes.java`

A Java record that encapsulates weapon statistics:

```java
public record FWWeaponAttributes(
    float damage,       // Attack damage value
    float speed,        // Attack speed modifier (negative = slower)
    float reach,        // Future: attack reach
    int durability      // 0 = use material default
) {
    public static Builder builder() { ... }
}
```

**Design rationale:**
- Records are immutable, preventing accidental modification
- Builder pattern for optional/default values
- Matches the structure of `weapon_defaults.json`

### `FWWeaponDefaults.java`

Singleton that loads and caches weapon defaults from JSON:

```java
public final class FWWeaponDefaults {
    private static final String RESOURCE_PATH = "/weapon_defaults.json";
    private static Map<String, FWWeaponAttributes> cache;

    public static FWWeaponAttributes get(String weaponName) {
        Map<String, FWWeaponAttributes> all = load();
        FWWeaponAttributes attrs = all.get(weaponName);
        return attrs != null ? attrs : fallback();
    }

    public static FWWeaponAttributes fallback() {
        return new FWWeaponAttributes(9.0f, -2.4f, 0.0f, 0);
    }
}
```

**Key behaviors:**
1. **Lazy loading** - JSON is only parsed when first accessed
2. **Caching** - Subsequent calls return cached results
3. **Fallback** - Unknown weapons get sensible default values
4. **Graceful failure** - Missing JSON file returns empty map

### Resources Directory

```
appCommon/src/main/resources/
├── weapon_defaults.json                    # Default stats for all weapons
├── assets/fantasy_weapons/
│   ├── lang/
│   │   ├── en_us.json                     # English translations
│   │   ├── ru_ru.json                     # Russian translations
│   │   └── uk_ua.json                     # Ukrainian translations
│   ├── logo.png                           # Mod logo
│   └── models/item/
│       ├── handheld_large_2x.json         # Base model for 2x weapons
│       ├── handheld_large_3x.json         # Base model for 3x weapons
│       ├── weapon_fates_beacon.json       # Regular model
│       ├── weapon_fates_beacon_large.json # Large (in-hand) model
│       └── ...
│   └── textures/item/
│       ├── weapon_fates_beacon.png        # Regular texture
│       ├── weapon_fates_beacon_large.png  # Large texture
│       └── ...
└── data/fantasy_weapons/
    └── recipes/
        ├── weapon_fates_beacon.json       # Smithing recipe
        └── ...
```

---

## The Loader Modules

Each loader module (`appFabric/X.XX.X` or `appNeoForge/X.XX.X`) contains **only** version and loader-specific code.

### Common Files in Each Loader Version

```
appFabric/1.21.1/
├── build.gradle                           # Dependencies, plugin versions
├── gradle.properties                      # MC version, loader version
├── src/main/
│   ├── java/net/kenddie/fantasyweapons/
│   │   ├── fabric/
│   │   │   ├── FantasyWeaponsFabric.java       # Entry point
│   │   │   ├── FantasyWeaponsClientFabric.java # Client init
│   │   │   └── model/
│   │   │       └── EpicBakedModel.java         # Model wrapper
│   │   └── item/
│   │       ├── FWItems.java                    # Item registration
│   │       └── FWWeaponBasicSwordItem.java     # Weapon class
│   └── resources/
│       └── fabric.mod.json                     # Fabric mod manifest
```

### Entry Point Classes

**Fabric:** `FantasyWeaponsFabric.java`
```java
public class FantasyWeaponsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FWItems.register();  // Register all items
    }
}
```

**NeoForge:** `FantasyWeaponsNeoForge.java`
```java
@Mod(FantasyWeapons.MOD_ID)
public class FantasyWeaponsNeoForge {
    public FantasyWeaponsNeoForge(IEventBus modEventBus) {
        FWItems.register(modEventBus);    // Pass event bus for registration
        modEventBus.register(new FantasyWeaponsClientNeoForge());
    }
}
```

### Item Registration

**Fabric Pattern (direct registry):**
```java
public static void register() {
    for (String name : FantasyWeapons.WEAPON_NAMES) {
        var config = FWConfig.FW_CONFIG.getSwordConfigOrDefault(name);
        ALL_ITEMS.add(Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(FantasyWeapons.MOD_ID, name),
            new FWWeaponBasicSwordItem(Tiers.NETHERITE, config.damage, config.speed, new Item.Properties())
        ));
    }
}
```

**NeoForge Pattern (deferred registration):**
```java
public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, FantasyWeapons.MOD_ID);

static {
    for (String name : FantasyWeapons.WEAPON_NAMES) {
        ALL_ITEMS.add(ITEMS.register(name, () -> {
            var config = FWConfig.FW_CONFIG.getSwordConfigOrDefault(name);
            return new FWWeaponBasicSwordItem(Tiers.NETHERITE, config.damage, config.speed, new Item.Properties());
        }));
    }
}

public static void register(IEventBus bus) {
    ITEMS.register(bus);
}
```

---

## The Build System

### Root `build.gradle`

```gradle
plugins {
    id 'idea'
}
```

The root project is minimal. All real build logic is in subprojects.

### Root `settings.gradle`

```gradle
include(':appCommon')

include(':appFabric:1.21.1')
include(':appFabric:1.21.4')
include(':appFabric:1.21.10')
include(':appFabric:1.21.11')

include(':appNeoForge:1.21.1')
include(':appNeoForge:1.21.4')
include(':appNeoForge:1.21.10')
include(':appNeoForge:1.21.11')
```

**How nested includes work:**
- `include(':appFabric:1.21.1')` looks for `appFabric/1.21.1/build.gradle`
- The project name becomes `:appFabric:1.21.1`
- Build with `gradlew :appFabric:1.21.1:build`

### `appCommon/build.gradle`

```gradle
plugins {
    id 'java-library'
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    compileOnly 'com.google.code.gson:gson:2.10.1'
    compileOnly 'de.exlll:configlib-yaml:4.5.0'
}
```

**Key points:**
- `java-library` plugin for pure Java
- Java 17 (minimum for MC 1.20.5+)
- Dependencies are `compileOnly` — each loader bundles its own copy

### Fabric `build.gradle`

```gradle
plugins {
    id 'fabric-loom' version '1.13.6'
    id 'com.github.johnrengelman.shadow' version '8.1.1'
}

dependencies {
    minecraft "com.mojang:minecraft:${project.minecraft_version}"
    mappings loom.officialMojangMappings()
    modImplementation "net.fabricmc:fabric-loader:${project.loader_version}"
    modImplementation "net.fabricmc.fabric-api:fabric-api:${project.fabric_version}"

    implementation project(':appCommon')                    // Depend on appCommon
    shadow "de.exlll:configlib-yaml:4.5.0"
    implementation "de.exlll:configlib-yaml:4.5.0"
}

processResources {
    from project(':appCommon').sourceSets.main.resources    // Copy shared resources
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

shadowJar {
    configurations = [project.configurations.shadow]
    archiveClassifier = 'dev-shadow'
    relocate("de.exlll", "net.kenddie.exlll")               // Relocate to avoid conflicts
    relocate("org.snakeyaml", "net.kenddie.snakeyaml")
}

remapJar {
    inputFile.set shadowJar.archiveFile                     // Remap the shadowed JAR
    dependsOn shadowJar
}

jar {
    from(project(':appCommon').sourceSets.main.output.classesDirs)  // Bundle appCommon classes
}
```

### NeoForge `build.gradle`

```gradle
plugins {
    id 'net.neoforged.moddev' version '2.0.107'
}

dependencies {
    implementation "net.neoforged:neoforge:${project.neoforge_version}"
    implementation project(':appCommon')

    jarJar("de.exlll:configlib-yaml:[4.5.0,5.0.0)") {       // Bundle range
        jarJar.pin(it, "4.5.0")
    }
}

neoForge {
    neoFormVersion = project.neoform_version

    runs {
        client { ... }
        server { ... }
    }

    mods {
        "${mod_id}" {
            sourceSet(sourceSets.main)
            sourceSet(project(':appCommon').sourceSets.main)  // Include appCommon in mod
        }
    }
}

jar {
    from(project(':appCommon').sourceSets.main.output.classesDirs)  // Bundle appCommon classes
}
```

---

## Data Flow: From JSON to Minecraft

Here's the complete flow of how a weapon's damage value goes from JSON to the game:

```
weapon_defaults.json
    ↓ (at class load)
FWWeaponDefaults.load()
    ↓ (parses JSON via Gson)
Map<String, FWWeaponAttributes>
    ↓ (stores in static cache)
    
FWConfig initialization
    ↓ (calls fromDefaults() for each weapon)
FWConfig.FW_CONFIG.weapons Map<String, SwordConfig>
    ↓ (FWConfigManager writes config/fantasy_weapons.yml if missing)
    ↓ (or merges user values from existing file)
    
FWItems registration
    ↓ (loops WEAPON_NAMES)
FWConfig.FW_CONFIG.getSwordConfigOrDefault(weaponName)
    ↓ (returns SwordConfig)
    
new FWWeaponBasicSwordItem(tier, config.damage, config.speed, properties)
    ↓ (constructor applies attributes)
    
Item registered with Minecraft
```

### Step-by-Step Example

1. **JSON loaded:**
   ```json
   { "weapon_dark_lord_sword": { "damage": 9.0, "speed": -2.4 } }
   ```

2. **FWWeaponDefaults parses:**
   ```java
   FWWeaponAttributes attrs = FWWeaponDefaults.get("weapon_dark_lord_sword");
   // attrs.damage() == 9.0
   // attrs.speed() == -2.4
   ```

3. **FWConfig creates SwordConfig:**
   ```java
   SwordConfig config = SwordConfig.fromDefaults("weapon_dark_lord_sword");
   // config.damage == 9.0
   // config.speed == -2.4
   ```

4. **YAML written/updated:**
   ```yaml
   weapons:
     weapon_dark_lord_sword:
       damage: 9.0
       speed: -2.4
   ```

5. **User can edit YAML to override:**
   ```yaml
   weapons:
     weapon_dark_lord_sword:
       damage: 12.0    # Custom damage
       speed: -2.0     # Faster attack
   ```

6. **FWItems creates weapon:**
   ```java
   SwordConfig config = FW_CONFIG.getSwordConfigOrDefault("weapon_dark_lord_sword");
   new FWWeaponBasicSwordItem(Tiers.NETHERITE, config.damage, config.speed, props);
   ```

---

## The Config System

### How ConfigLib Works

The mod uses [ConfigLib](https://github.com/Exlll/ConfigLib) for YAML configuration:

```java
@Configuration
public class FWConfig {
    public Map<String, SwordConfig> weapons;

    @Configuration
    public static class SwordConfig {
        public float damage = 9;
        public float speed = -2.4f;
    }
}
```

### Config Loading Flow

```java
public static final FWConfig FW_CONFIG = FWConfigManager.updateAndLoadConfig(
    Path.of("config/fantasy_weapons.yml"), FWConfig.class);
```

1. If `config/fantasy_weapons.yml` doesn't exist:
   - Create fresh `FWConfig` instance
   - All weapons get defaults from `FWWeaponDefaults`
   - Write the config file

2. If `config/fantasy_weapons.yml` exists:
   - Load existing config
   - Merge with defaults (adds new weapons)
   - Preserve user customizations
   - Update file with any new entries

### Example Config File

```yaml
weapons:
  weapon_fates_beacon:
    damage: 9.0
    speed: -2.4
  weapon_dragonslayer_halberd:
    damage: 12.0     # User increased damage
    speed: -2.8      # User decreased speed
  # ... 61 weapons
```

---

## The Model System (Large/Small Weapons)

Fantasy Weapons uses a dual-model system where weapons appear larger when held in hand but smaller in inventory/ground/item frames.

### How It Works

1. **Two models per weapon:**
   - `weapon_dark_lord_sword.json` - Small (16x16) for inventory
   - `weapon_dark_lord_sword_large.json` - Large (32x32 or 48x48) for hands

2. **Model switching logic:**
   ```java
   if (displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ||
       displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ||
       displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND ||
       displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
       return largeModel;
   } else {
       return smallModel;
   }
   ```

3. **EpicBakedModel wrapper:**
   ```java
   public record EpicBakedModel(Model smallModel, Model largeModel) implements Model {
       // Delegates to appropriate model based on context
   }
   ```

### Fabric Implementation (1.21.1)

```java
ModelLoadingPlugin.register(pluginContext -> {
    // Register large models as additional models
    pluginContext.addModels(largeModelLocations);

    // After baking, wrap small models with EpicBakedModel
    pluginContext.modifyModelAfterBake().register((small, context) -> {
        var large = context.baker().bake(largeLocation, settings);
        return new EpicBakedModel(small, large);
    });
});
```

### NeoForge Implementation (1.21.1)

```java
@SubscribeEvent
public void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
    // Register large models
    event.register(largeModelLocation);
}

@SubscribeEvent
public void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
    // Replace small models with EpicBakedModel wrappers
    var models = event.getModels();
    models.put(smallLoc, new EpicBakedModel(models.get(smallLoc), models.get(largeLoc)));
}
```

---

## How JAR Files Are Built

### Fabric JAR Build Process

```
1. :appCommon:compileJava
   └── Compiles shared Java code

2. :appCommon:processResources
   └── Copies resources (textures, models, etc.)

3. :appFabric:1.21.1:compileJava
   └── Compiles Fabric 1.21.1 code (with appCommon on classpath)

4. :appFabric:1.21.1:processResources
   └── Copies resources from:
       - appFabric/1.21.1/src/main/resources (fabric.mod.json)
       - appCommon/src/main/resources (all shared resources)

5. :appFabric:1.21.1:jar
   └── Creates JAR with:
       - All compiled classes from appFabric/1.21.1
       - All compiled classes from appCommon
       - All resources

6. :appFabric:1.21.1:shadowJar
   └── Embeds and relocates ConfigLib:
       - de.exlll.configlib → net.kenddie.exlll.configlib
       - org.snakeyaml → net.kenddie.snakeyaml

7. :appFabric:1.21.1:remapJar
   └── Remaps the shadowed JAR to production mappings
   └── Output: build/libs/fantasy-weapons-fabric-1.21.1-X.X.X.jar
```

### Final JAR Contents

```
fantasy-weapons-fabric-1.21.1-X.X.X.jar
├── net/kenddie/fantasyweapons/
│   ├── FantasyWeapons.class              (from appCommon)
│   ├── config/
│   │   ├── FWConfig.class                (from appCommon)
│   │   └── FWConfigManager.class         (from appCommon)
│   ├── shared/
│   │   ├── config/FWWeaponDefaults.class (from appCommon)
│   │   └── weapon/FWWeaponAttributes.class (from appCommon)
│   ├── fabric/
│   │   ├── FantasyWeaponsFabric.class    (from appFabric/1.21.1)
│   │   └── ...
│   └── item/
│       ├── FWItems.class                 (from appFabric/1.21.1)
│       └── FWWeaponBasicSwordItem.class  (from appFabric/1.21.1)
├── net/kenddie/exlll/                    (relocated ConfigLib)
├── net/kenddie/snakeyaml/                (relocated SnakeYAML)
├── assets/fantasy_weapons/               (from appCommon)
│   ├── lang/*.json
│   ├── models/item/*.json
│   └── textures/item/*.png
├── data/fantasy_weapons/                 (from appCommon)
│   └── recipes/*.json
├── weapon_defaults.json                  (from appCommon)
└── fabric.mod.json                       (from appFabric/1.21.1)
```

---

## Summary

The repository architecture ensures:

1. **Single source of truth** - All shared code and resources in `appCommon`
2. **Minimal duplication** - Only truly version-specific code in loader modules
3. **Easy maintenance** - Update one place, affects all versions
4. **Clean builds** - Each JAR is self-contained with all dependencies
5. **User customization** - YAML config with sensible JSON-based defaults
