# How to Add New Weapons

All changes are in `appCommon` — no loader-specific code changes needed.

---

## Table of Contents

1. [Quick Checklist](#quick-checklist)
2. [Step 1: Add the Weapon Name](#step-1-add-the-weapon-name)
3. [Step 2: Add Default Stats](#step-2-add-default-stats)
4. [Step 3: Create the Textures](#step-3-create-the-textures)
5. [Step 4: Create the Item Models](#step-4-create-the-item-models)
6. [Step 5: Create the Recipe](#step-5-create-the-recipe)
7. [Step 6: Add Translations](#step-6-add-translations)
8. [Step 7: Build and Test](#step-7-build-and-test)
9. [Complete Example](#complete-example-adding-weapon_moonlight_katana)
10. [Troubleshooting](#troubleshooting)

---

## Quick Checklist

When adding a new weapon, you need to modify/create these files in `appCommon`:

- [ ] `src/main/java/net/kenddie/fantasyweapons/FantasyWeapons.java` - Add name to array
- [ ] `src/main/resources/weapon_defaults.json` - Add default stats
- [ ] `src/main/resources/assets/fantasy_weapons/textures/item/weapon_xxx.png` - Regular texture
- [ ] `src/main/resources/assets/fantasy_weapons/textures/item/weapon_xxx_large.png` - Large texture
- [ ] `src/main/resources/assets/fantasy_weapons/models/item/weapon_xxx.json` - Regular model
- [ ] `src/main/resources/assets/fantasy_weapons/models/item/weapon_xxx_large.json` - Large model
- [ ] `src/main/resources/data/fantasy_weapons/recipe/weapon_xxx.json` - Crafting recipe
- [ ] `src/main/resources/assets/fantasy_weapons/lang/en_us.json` - English name
- [ ] `src/main/resources/assets/fantasy_weapons/lang/ru_ru.json` - Russian name (optional)
- [ ] `src/main/resources/assets/fantasy_weapons/lang/uk_ua.json` - Ukrainian name (optional)

No changes needed in any loader-specific code.

---

## Step 1: Add the Weapon Name

### File: `appCommon/src/main/java/net/kenddie/fantasyweapons/FantasyWeapons.java`

```java
public static final String[] WEAPON_NAMES = {
        // ... existing weapons ...
        "weapon_dancing_blade_of_ranah",
        "weapon_moonlight_katana"          // ← Add new weapon here
};
```

Naming convention: `weapon_` prefix, lowercase, underscores (e.g. `weapon_moonlight_katana`).

---

## Step 2: Add Default Stats

### File: `appCommon/src/main/resources/weapon_defaults.json`

Add an entry for your weapon with its default statistics:

```json
{
  "weapon_dancing_blade_of_ranah": { "damage": 9.0, "speed": -2.4, "reach": 0.0, "durability": 0 },
  "weapon_moonlight_katana": { "damage": 10.0, "speed": -2.2, "reach": 0.0, "durability": 0 }
}
```

### Stats Explained

| Property | Type | Description | Typical Range |
|----------|------|-------------|---------------|
| `damage` | float | Base attack damage | 5.0 - 15.0 |
| `speed` | float | Attack speed modifier (negative = slower) | -3.0 to -1.5 |
| `reach` | float | Attack reach modifier (future use) | 0.0 (not yet implemented) |
| `durability` | int | Custom durability (0 = use Netherite default) | 0 or 500-3000 |

### Stats Reference

For comparison, here are vanilla weapon stats:

| Weapon | Damage | Speed | Effective DPS |
|--------|--------|-------|---------------|
| Wooden Sword | 4 | -2.4 | 6.4 |
| Stone Sword | 5 | -2.4 | 8.0 |
| Iron Sword | 6 | -2.4 | 9.6 |
| Diamond Sword | 7 | -2.4 | 11.2 |
| Netherite Sword | 8 | -2.4 | 12.8 |
| Diamond Axe | 9 | -3.0 | 9.0 |
| Netherite Axe | 10 | -3.0 | 10.0 |

**Fantasy Weapons defaults:** damage=9, speed=-2.4 (between netherite sword and axe)

---

## Step 3: Create the Textures

You need **two textures** for each weapon:

### Regular Texture (Inventory/Ground)
**File:** `appCommon/src/main/resources/assets/fantasy_weapons/textures/item/weapon_moonlight_katana.png`

- **Size:** 16×16 pixels (standard Minecraft item size)
- **Format:** PNG with transparency
- **Use:** Appears in inventory, item frames, ground

### Large Texture (In-Hand)
**File:** `appCommon/src/main/resources/assets/fantasy_weapons/textures/item/weapon_moonlight_katana_large.png`

- **Size:** 32×32 or 48×48 pixels (for dramatic held appearance)
- **Format:** PNG with transparency
- **Use:** Appears when held in first/third person

Design the large version first and scale down for the regular texture.

---

## Step 4: Create the Item Models

You need **two model files** for each weapon:

### Regular Model
**File:** `appCommon/src/main/resources/assets/fantasy_weapons/models/item/weapon_moonlight_katana.json`

```json
{
  "parent": "minecraft:item/handheld",
  "textures": {
    "layer0": "fantasy_weapons:item/weapon_moonlight_katana"
  }
}
```

### Large Model
**File:** `appCommon/src/main/resources/assets/fantasy_weapons/models/item/weapon_moonlight_katana_large.json`

For a **2x scale** weapon (32×32 texture):
```json
{
  "parent": "fantasy_weapons:item/handheld_large_2x",
  "textures": {
    "layer0": "fantasy_weapons:item/weapon_moonlight_katana_large"
  }
}
```

For a **3x scale** weapon (48×48 texture):
```json
{
  "parent": "fantasy_weapons:item/handheld_large_3x",
  "textures": {
    "layer0": "fantasy_weapons:item/weapon_moonlight_katana_large"
  }
}
```

### Available Parent Models

| Parent | Texture Size | Use For |
|--------|--------------|---------|
| `minecraft:item/handheld` | 16×16 | Regular inventory model |
| `fantasy_weapons:item/handheld_large_2x` | 32×32 | Large swords, katanas |
| `fantasy_weapons:item/handheld_large_3x` | 48×48 | Greatswords, halberds |

### Custom Display Transforms

If you need custom positioning, override the display settings:

```json
{
  "parent": "fantasy_weapons:item/handheld_large_2x",
  "textures": {
    "layer0": "fantasy_weapons:item/weapon_moonlight_katana_large"
  },
  "display": {
    "thirdperson_righthand": {
      "rotation": [0, -90, 55],
      "translation": [0, 4.0, 0.5],
      "scale": [0.85, 0.85, 0.85]
    },
    "thirdperson_lefthand": {
      "rotation": [0, 90, -55],
      "translation": [0, 4.0, 0.5],
      "scale": [0.85, 0.85, 0.85]
    },
    "firstperson_righthand": {
      "rotation": [0, -90, 25],
      "translation": [1.13, 3.2, 1.13],
      "scale": [0.68, 0.68, 0.68]
    },
    "firstperson_lefthand": {
      "rotation": [0, 90, -25],
      "translation": [1.13, 3.2, 1.13],
      "scale": [0.68, 0.68, 0.68]
    }
  }
}
```

---

## Step 5: Create the Recipe

### File: `appCommon/src/main/resources/data/fantasy_weapons/recipe/weapon_moonlight_katana.json`

Recipes use **smithing transform** (netherite sword + blazing stone at smithing table):

```json
{
  "type": "minecraft:smithing_transform",
  "category": "equipment",
  "template": {
    "item": "minecraft:iron_ingot"
  },
  "base": {
    "item": "minecraft:netherite_sword"
  },
  "addition": {
    "item": "fantasy_weapons:blazing_stone"
  },
  "result": {
    "id": "fantasy_weapons:weapon_moonlight_katana"
  }
}
```

### Recipe Components

| Component | Purpose | Current Pattern |
|-----------|---------|----------------|
| `template` | Smithing template slot | Varies per weapon |
| `base` | The item being upgraded | `minecraft:netherite_sword` |
| `addition` | The upgrade material | `fantasy_weapons:blazing_stone` |
| `result` | The output weapon | `fantasy_weapons:weapon_xxx` |

---

## Step 6: Add Translations

Add the weapon's display name to all language files:

### English (Required)
**File:** `appCommon/src/main/resources/assets/fantasy_weapons/lang/en_us.json`

```json
{
  "item.fantasy_weapons.weapon_dancing_blade_of_ranah": "Dancing Blade of Ranah",
  "item.fantasy_weapons.weapon_moonlight_katana": "Moonlight Katana"
}
```

### Russian (Optional but appreciated)
**File:** `appCommon/src/main/resources/assets/fantasy_weapons/lang/ru_ru.json`

```json
{
  "item.fantasy_weapons.weapon_moonlight_katana": "Катана Лунного Света"
}
```

### Ukrainian (Optional but appreciated)
**File:** `appCommon/src/main/resources/assets/fantasy_weapons/lang/uk_ua.json`

```json
{
  "item.fantasy_weapons.weapon_moonlight_katana": "Катана Місячного Світла"
}
```

Translation key format: `item.fantasy_weapons.{weapon_name}`

---

## Step 7: Build and Test

### Build All Versions

```bash
./gradlew build
```

### Build Specific Version

```bash
./gradlew :appFabric:1.21.1:build
```

### Test in Development Environment

**Fabric:**
```bash
./gradlew :appFabric:1.21.1:runClient
```

**NeoForge:**
```bash
./gradlew :appNeoForge:1.21.1:runClient
```

### Verify Your Weapon

1. Launch the game
2. Open creative menu
3. Find "Fantasy Weapons" tab
4. Look for your weapon (should be at the end)
5. Test in inventory (small texture)
6. Hold in hand (large texture)
7. Test recipe at smithing table/crafting table

---

## Complete Example: Adding "Weapon_Moonlight_Katana"

Here's everything you need to add for a complete weapon:

### 1. `FantasyWeapons.java` (add to array)
```java
"weapon_moonlight_katana"
```

### 2. `weapon_defaults.json` (add entry)
```json
"weapon_moonlight_katana": { "damage": 10.0, "speed": -2.2, "reach": 0.0, "durability": 0 }
```

### 3. `textures/item/weapon_moonlight_katana.png`
- 16×16 PNG
- Katana design, lunar theme

### 4. `textures/item/weapon_moonlight_katana_large.png`
- 32×32 PNG
- Same design, more detail

### 5. `models/item/weapon_moonlight_katana.json`
```json
{
  "parent": "minecraft:item/handheld",
  "textures": {
    "layer0": "fantasy_weapons:item/weapon_moonlight_katana"
  }
}
```

### 6. `models/item/weapon_moonlight_katana_large.json`
```json
{
  "parent": "fantasy_weapons:item/handheld_large_2x",
  "textures": {
    "layer0": "fantasy_weapons:item/weapon_moonlight_katana_large"
  }
}
```

### 7. `recipe/weapon_moonlight_katana.json`
```json
{
  "type": "minecraft:smithing_transform",
  "category": "equipment",
  "template": { "item": "minecraft:iron_ingot" },
  "base": { "item": "minecraft:netherite_sword" },
  "addition": { "item": "fantasy_weapons:blazing_stone" },
  "result": { "id": "fantasy_weapons:weapon_moonlight_katana" }
}
```

### 8. `lang/en_us.json`
```json
"item.fantasy_weapons.weapon_moonlight_katana": "Moonlight Katana"
```

---

## Troubleshooting

### Weapon doesn't appear in creative menu

**Cause:** Name not in `WEAPON_NAMES` array or typo in name.

**Fix:** Check `FantasyWeapons.java` for exact spelling.

### Weapon has purple/black missing texture

**Cause:** Texture file missing or incorrectly named.

**Fix:** Ensure texture filename exactly matches the weapon name:
- `weapon_moonlight_katana.png` ✓
- `weapon_Moonlight_katana.png` ✗ (case matters)
- `moonlight_katana.png` ✗ (missing prefix)

### Weapon shows small in hand

**Cause:** Large model file missing or not being loaded.

**Fix:** Ensure `weapon_xxx_large.json` exists and references correct texture.

### "Unknown recipe" error

**Cause:** Recipe JSON syntax error or wrong item ID.

**Fix:** 
- Validate JSON syntax
- Verify `result.id` matches `fantasy_weapons:weapon_xxx`
- Check base/addition items exist

### Translation not showing (shows raw key)

**Cause:** Wrong key format or missing from language file.

**Fix:** Ensure key is exactly `item.fantasy_weapons.weapon_xxx`

### Config not showing new weapon

**Cause:** User's existing config file doesn't have the new entry.

**Fix:** Delete `config/fantasy_weapons.yml` to regenerate with new defaults, or manually add the entry.

---

## Summary

Adding a new weapon requires:

1. **1 Java change** - Add name to `WEAPON_NAMES` array
2. **1 JSON change** - Add defaults to `weapon_defaults.json`
3. **2 textures** - Regular and large PNG files
4. **2 models** - Regular and large JSON files
5. **1 recipe** - Smithing or crafting JSON
6. **1+ translations** - At minimum `en_us.json`

Total: **8 files** modified/created, all in `appCommon`.

No changes needed in any of the 8 loader version directories!
