# Architecture Overview

This document describes the unified repository structure for Fantasy Weapons.

---

## Repository Structure

```
FANTASY-WEAPONS/
├── appCommon/                              ← Shared module
│   ├── build.gradle                        ← Pure Java 17, no MC deps
│   └── src/main/
│       ├── java/net/kenddie/fantasyweapons/
│       │   ├── FantasyWeapons.java         ← Constants (MOD_ID, WEAPON_NAMES)
│       │   ├── config/
│       │   │   ├── FWConfig.java           ← ConfigLib-based config
│       │   │   └── FWConfigManager.java    ← YAML loading
│       │   └── shared/
│       │       ├── config/
│       │       │   └── FWWeaponDefaults.java    ← JSON-based defaults
│       │       └── weapon/
│       │           └── FWWeaponAttributes.java  ← Weapon stats record
│       └── resources/
│           ├── weapon_defaults.json        ← Single source of truth for stats
│           ├── assets/fantasy_weapons/
│           │   ├── lang/*.json             ← Language files (3)
│           │   ├── models/item/*.json      ← Item models
│           │   └── textures/item/*.png     ← Textures
│           └── data/fantasy_weapons/
│               └── recipe/*.json           ← Recipes (62)
├── appFabric/
│   ├── 1.20.1/     (Legacy Fabric 1.20.1 — self-contained)
│   ├── 1.21.1/     (Fabric 1.21.1-specific code only)
│   ├── 1.21.4/
│   ├── 1.21.10/
│   └── 1.21.11/
├── appForge/
│   └── 1.20.1/     (Legacy Minecraft Forge 1.20.1 — self-contained)
├── appNeoForge/
│   ├── 1.21.1/     (NeoForge 1.21.1-specific code only)
│   ├── 1.21.4/
│   ├── 1.21.10/
│   └── 1.21.11/
└── settings.gradle                         ← Includes all modules
```

---

## The `appCommon` Module

A **pure Java 17 library** with no Minecraft dependencies. It compiles to a plain JAR that each loader bundles into its final output.

### Dependencies

```gradle
dependencies {
    compileOnly 'com.google.code.gson:gson:2.10.1'
    compileOnly 'de.exlll:configlib-yaml:4.5.0'
}
```

Both are `compileOnly` — each loader bundles its own copy at runtime.

---

## Key Files in `appCommon`

### `FWWeaponAttributes.java`

Record encapsulating weapon statistics:

```java
public record FWWeaponAttributes(
        float damage,       // Base attack damage
        float speed,        // Attack speed modifier
        float reach,        // Reach modifier (future use)
        int durability      // Durability override (0 = use default)
) {
    // Includes Builder with defaults: damage=9.0f, speed=-2.4f, reach=0.0f, durability=0
}
```

### `FWWeaponDefaults.java`

Loads and caches default weapon stats from `weapon_defaults.json`:

```java
public static FWWeaponAttributes get(String weaponName)  // Returns attrs or fallback
public static FWWeaponAttributes fallback()              // damage=9.0, speed=-2.4, reach=0.0, durability=0
```

- Lazy loads and caches the JSON on first access
- Returns fallback values for unknown weapons

### `FWConfig.java`

ConfigLib YAML config. On startup, populates a `Map<String, SwordConfig>` for all weapons using defaults from `FWWeaponDefaults`:

```java
public SwordConfig getSwordConfigOrDefault(String id) {
    return weapons.computeIfAbsent(id, SwordConfig::fromDefaults);
}

public static SwordConfig fromDefaults(String weaponName) {
    FWWeaponAttributes attrs = FWWeaponDefaults.get(weaponName);
    return new SwordConfig(attrs.damage(), attrs.speed());
}
```

### `weapon_defaults.json`

Single source of truth for all 61 weapon default stats:

```json
{
  "weapon_fates_beacon": { "damage": 9.0, "speed": -2.4, "reach": 0.0, "durability": 0 },
  "weapon_dragonslayer_halberd": { "damage": 9.0, "speed": -2.4, "reach": 0.0, "durability": 0 },
  ...
}
```

---

## How Loaders Integrate `appCommon`

All 1.21.x loader `build.gradle` files (8 total) include:

```gradle
dependencies {
    implementation project(':appCommon')
}

processResources {
    from project(':appCommon').sourceSets.main.resources
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

jar {
    from(project(':appCommon').sourceSets.main.output.classesDirs)
}

sourcesJar {
    from project(':appCommon').sourceSets.main.allSource
}
```

NeoForge modules additionally include appCommon in their `neoForge.mods` block:

```gradle
neoForge {
    mods {
        "${mod_id}" {
            sourceSet(sourceSets.main)
            sourceSet(project(':appCommon').sourceSets.main)
        }
    }
}
```

---

## What Stays in Each Loader Version

Each loader directory contains only version/platform-specific code:

| File | Purpose |
|------|---------|
| `fabric.mod.json` / `neoforge.mods.toml` | Loader manifest |
| `FantasyWeaponsFabric.java` / `FantasyWeaponsNeoForge.java` | Entry point |
| `FantasyWeaponsClientFabric.java` / `FantasyWeaponsClientNeoForge.java` | Model loading |
| `FWItems.java` | Item registration (loader-specific APIs) |
| `FWWeaponBasicSwordItem.java` | Weapon class (changes between MC versions) |
| `EpicBakedModel.java` | Large/small model switching |

These can't be unified because:
- Registration APIs differ between Fabric and NeoForge
- Model APIs changed between MC 1.21.1 and 1.21.10+
- Weapon class inheritance changed (`SwordItem` → `Item` with `ToolMaterial`)

---

## Notes

- Legacy 1.20.1 modules (`appFabric/1.20.1`, `appForge/1.20.1`) are self-contained with their own resources and do **not** depend on `appCommon`. They use `recipes/` (plural) and `"item"` in recipe results, which is correct for MC 1.20.x.
- All 1.21.x modules depend on `appCommon` for shared code and resources. They use `recipe/` (singular) and `"id"` in recipe results.
- 1.21.4+ modules apply a `processResources` transformation to convert ingredient format from `{"item":"..."}` to string `"minecraft:foo"`.

No changes needed in any loader version code — they automatically pick up the new weapon from `WEAPON_NAMES`.
