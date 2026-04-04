# How This Repository Works

Technical reference for the Fantasy Weapons repository architecture, build system, and data flow.

---

## Table of Contents

1. [Repository Structure](#repository-structure)
2. [The `appCommon` Module](#the-appcommon-module)
3. [The Loader Modules](#the-loader-modules)
4. [The Build System](#the-build-system)
5. [Data Flow: JSON to Minecraft](#data-flow-json-to-minecraft)
6. [The Config System](#the-config-system)
7. [The Model System (Large/Small Weapons)](#the-model-system-largesmall-weapons)
8. [How JAR Files Are Built](#how-jar-files-are-built)

---

## Repository Structure

```
FANTASY-WEAPONS/
├── appCommon/                      # Shared pure-Java module
│   ├── build.gradle                # Java 17, no MC deps
│   └── src/main/
│       ├── java/                   # Shared Java code
│       └── resources/              # All textures, models, recipes
│
├── appFabric/                      # Fabric loader implementations
│   ├── 1.20.1/                     # Legacy (self-contained, own resources)
│   ├── 1.21.1/
│   ├── 1.21.4/
│   ├── 1.21.10/
│   └── 1.21.11/
│
├── appForge/                       # Minecraft Forge implementations
│   └── 1.20.1/                     # Legacy (self-contained, own resources)
│
├── appNeoForge/                    # NeoForge loader implementations
│   ├── 1.21.1/
│   ├── 1.21.4/
│   ├── 1.21.10/
│   └── 1.21.11/
│
├── settings.gradle                 # Declares all subprojects
├── build.gradle                    # Root project (minimal)
└── gradle.properties               # Global properties
```

Each Minecraft version has its own directory because APIs change between versions (registration, models, weapon class inheritance, resource location classes).

---

## The `appCommon` Module

`appCommon` is a **pure Java 17 library** with no Minecraft dependencies, containing constants, configuration, default values, and all shared resources.

### Package Structure

```
net.kenddie.fantasyweapons/
├── FantasyWeapons.java                 # MOD_ID, BASIC_ITEM_NAMES, WEAPON_NAMES (61 weapons)
├── config/
│   ├── FWConfig.java                   # YAML config (Map<String, SwordConfig>)
│   └── FWConfigManager.java            # ConfigLib YAML wrapper
└── shared/
    ├── config/
    │   └── FWWeaponDefaults.java       # Loads weapon_defaults.json, caches results
    └── weapon/
        └── FWWeaponAttributes.java     # Record: damage, speed, reach, durability
```

### Resources Directory

```
appCommon/src/main/resources/
├── weapon_defaults.json                    # Default stats for all 61 weapons
├── assets/fantasy_weapons/
│   ├── lang/
│   │   ├── en_us.json
│   │   ├── ru_ru.json
│   │   └── uk_ua.json
│   ├── logo.png
│   ├── models/item/
│   │   ├── handheld_large_2x.json         # Base model for 32x32 weapons
│   │   ├── handheld_large_3x.json         # Base model for 48x48 weapons
│   │   ├── weapon_fates_beacon.json       # Per-weapon model
│   │   ├── weapon_fates_beacon_large.json # Per-weapon large model
│   │   └── ...
│   └── textures/item/
│       ├── weapon_fates_beacon.png
│       ├── weapon_fates_beacon_large.png
│       └── ...
└── data/fantasy_weapons/
    └── recipe/                             # 62 smithing recipes
        └── *.json
```

---

## The Loader Modules

Each loader module contains **only** version and platform-specific code.

### Files in Each Loader Version

```
appFabric/1.21.1/src/main/
├── java/net/kenddie/fantasyweapons/
│   ├── fabric/
│   │   ├── FantasyWeaponsFabric.java       # Entry point (ModInitializer)
│   │   ├── FantasyWeaponsClientFabric.java # Model loading (ClientModInitializer)
│   │   └── model/
│   │       └── EpicBakedModel.java         # Large/small model wrapper
│   └── item/
│       ├── FWItems.java                    # Item registration
│       └── FWWeaponBasicSwordItem.java     # Weapon class
└── resources/
    └── fabric.mod.json
```

### Item Registration

**Fabric (direct registry):**
```java
public static void register() {
    for (String name : FantasyWeapons.BASIC_ITEM_NAMES) {
        ALL_ITEMS.add(registerItem(name, new Item(new Item.Properties())));
    }
    for (String name : FantasyWeapons.WEAPON_NAMES) {
        var config = FWConfig.FW_CONFIG.getSwordConfigOrDefault(name);
        ALL_ITEMS.add(registerItem(name, new FWWeaponBasicSwordItem(
                Tiers.NETHERITE, config.damage, config.speed, new Item.Properties())));
    }
}
```

**NeoForge (deferred registration):**
```java
public static final DeferredRegister<Item> ITEMS =
    DeferredRegister.create(Registries.ITEM, FantasyWeapons.MOD_ID);

static {
    for (String name : FantasyWeapons.BASIC_ITEM_NAMES) {
        ALL_ITEMS.add(ITEMS.register(name, () -> new Item(new Item.Properties())));
    }
    for (String name : FantasyWeapons.WEAPON_NAMES) {
        ALL_ITEMS.add(ITEMS.register(name, () -> {
            var config = FWConfig.FW_CONFIG.getSwordConfigOrDefault(name);
            return new FWWeaponBasicSwordItem(
                    Tiers.NETHERITE, config.damage, config.speed, new Item.Properties());
        }));
    }
}

public static void register(IEventBus bus) {
    ITEMS.register(bus);
    TABS.register(bus);
}
```

> **Note:** 1.21.1 uses `Tiers.NETHERITE` and `SwordItem`. 1.21.4+ uses `ToolMaterial.NETHERITE` and `Item` — see [05_NEOFORGE_API_CHANGES.md](05_NEOFORGE_API_CHANGES.md) and [06_FABRIC_API_CHANGES.md](06_FABRIC_API_CHANGES.md).

---

## The Build System

### `settings.gradle`

```gradle
include(':appCommon')

include(':appFabric:1.20.1')
include(':appFabric:1.21.1')
include(':appFabric:1.21.4')
include(':appFabric:1.21.10')
include(':appFabric:1.21.11')

include(':appForge:1.20.1')

include(':appNeoForge:1.21.1')
include(':appNeoForge:1.21.4')
include(':appNeoForge:1.21.10')
include(':appNeoForge:1.21.11')
```

- `include(':appFabric:1.21.1')` maps to `appFabric/1.21.1/build.gradle`
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

### Fabric `build.gradle`

```gradle
plugins {
    id 'fabric-loom' version '...'
    id 'com.github.johnrengelman.shadow' version '8.1.1'
}

dependencies {
    minecraft "com.mojang:minecraft:${project.minecraft_version}"
    mappings loom.officialMojangMappings()
    modImplementation "net.fabricmc:fabric-loader:${project.loader_version}"
    modImplementation "net.fabricmc.fabric-api:fabric-api:${project.fabric_version}"

    implementation project(':appCommon')
    shadow "de.exlll:configlib-yaml:4.5.0"
    implementation "de.exlll:configlib-yaml:4.5.0"
}

processResources {
    from project(':appCommon').sourceSets.main.resources
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

shadowJar {
    configurations = [project.configurations.shadow]
    archiveClassifier = 'dev-shadow'
    relocate("de.exlll", "net.kenddie.exlll")
    relocate("org.snakeyaml", "net.kenddie.snakeyaml")
}

remapJar {
    inputFile.set shadowJar.archiveFile
    dependsOn shadowJar
}

jar {
    from(project(':appCommon').sourceSets.main.output.classesDirs)
}
```

### NeoForge `build.gradle`

```gradle
plugins {
    id 'net.neoforged.moddev' version '...'
}

dependencies {
    implementation "net.neoforged:neoforge:${project.neoforge_version}"
    implementation project(':appCommon')

    jarJar("de.exlll:configlib-yaml:[4.5.0,5.0.0)") {
        jarJar.pin(it, "4.5.0")
    }
}

neoForge {
    neoFormVersion = project.neoform_version

    mods {
        "${mod_id}" {
            sourceSet(sourceSets.main)
            sourceSet(project(':appCommon').sourceSets.main)
        }
    }
}

jar {
    from(project(':appCommon').sourceSets.main.output.classesDirs)
}
```

> **Note:** The 1.21.x loader modules get all their resources (textures, models, recipes, lang files) from `appCommon`. The legacy 1.20.1 modules (`appFabric/1.20.1`, `appForge/1.20.1`) are self-contained and do **not** depend on `appCommon`.

---

## Data Flow: JSON to Minecraft

```
weapon_defaults.json
    ↓ FWWeaponDefaults.load() — parses JSON via Gson, caches in static map
    ↓
FWConfig constructor
    ↓ calls SwordConfig.fromDefaults() for each weapon
    ↓ FWConfigManager writes/merges config/fantasy_weapons.yml
    ↓
FWItems registration loop
    ↓ FWConfig.FW_CONFIG.getSwordConfigOrDefault(weaponName)
    ↓
new FWWeaponBasicSwordItem(tier/material, config.damage, config.speed, properties)
    ↓
Item registered with Minecraft
```

User overrides are preserved — if `config/fantasy_weapons.yml` already exists, ConfigLib merges user values with defaults and adds entries for any new weapons.

---

## The Config System

Uses [ConfigLib](https://github.com/Exlll/ConfigLib) for YAML configuration.

```java
@Configuration
public class FWConfig {
    public static final FWConfig FW_CONFIG = FWConfigManager.updateAndLoadConfig(
        Path.of("config/fantasy_weapons.yml"), FWConfig.class);

    public Map<String, SwordConfig> weapons;

    public FWConfig() {
        this.weapons = new HashMap<>();
        for (String name : FantasyWeapons.WEAPON_NAMES) {
            weapons.put(name, SwordConfig.fromDefaults(name));
        }
    }

    public SwordConfig getSwordConfigOrDefault(String id) {
        return weapons.computeIfAbsent(id, SwordConfig::fromDefaults);
    }
}
```

Generated config file (`config/fantasy_weapons.yml`):
```yaml
weapons:
  weapon_fates_beacon:
    damage: 9.0
    speed: -2.4
  weapon_dragonslayer_halberd:
    damage: 9.0
    speed: -2.4
  # ... all 61 weapons
```

---

## The Model System (Large/Small Weapons)

Each weapon has two models — a small one (16x16) for inventory/ground and a large one (32x32 or 48x48) for held-in-hand. `EpicBakedModel` wraps both and delegates based on display context:

```java
if (context == FIRST_PERSON_LEFT_HAND  || context == FIRST_PERSON_RIGHT_HAND ||
    context == THIRD_PERSON_LEFT_HAND  || context == THIRD_PERSON_RIGHT_HAND) {
    // use large model
} else {
    // use small model
}
```

The model loading and wrapping APIs differ significantly between MC versions and loaders — see [05_NEOFORGE_API_CHANGES.md](05_NEOFORGE_API_CHANGES.md) and [06_FABRIC_API_CHANGES.md](06_FABRIC_API_CHANGES.md).

---

## How JAR Files Are Built

### Fabric Build Pipeline

```
1. :appCommon:compileJava           → shared Java
2. :appCommon:processResources      → shared resources
3. :appFabric:1.21.1:compileJava   → version-specific code (appCommon on classpath)
4. :appFabric:1.21.1:processResources
   └── Merges appFabric/1.21.1/resources + appCommon/resources
5. :appFabric:1.21.1:jar           → classes from both modules
6. :appFabric:1.21.1:shadowJar     → embeds + relocates ConfigLib
7. :appFabric:1.21.1:remapJar      → production mappings
   └── Output: build/libs/fantasy-weapons-fabric-1.21.1-X.X.X.jar
```

### Final JAR Contents

```
fantasy-weapons-fabric-1.21.1-X.X.X.jar
├── net/kenddie/fantasyweapons/
│   ├── FantasyWeapons.class              (appCommon)
│   ├── config/*.class                    (appCommon)
│   ├── shared/**/*.class                 (appCommon)
│   ├── fabric/*.class                    (appFabric/1.21.1)
│   └── item/*.class                      (appFabric/1.21.1)
├── net/kenddie/exlll/                    (relocated ConfigLib)
├── net/kenddie/snakeyaml/                (relocated SnakeYAML)
├── assets/fantasy_weapons/               (appCommon)
├── data/fantasy_weapons/                 (appCommon)
├── weapon_defaults.json                  (appCommon)
└── fabric.mod.json                       (appFabric/1.21.1)
```
