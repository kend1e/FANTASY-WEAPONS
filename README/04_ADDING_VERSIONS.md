# How to Add New Minecraft Versions

Adding a new Minecraft version involves copying an existing version directory, updating version numbers, fixing API changes, and registering the module in `settings.gradle`.

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Step 1: Copy a Base Version](#step-1-copy-a-base-version)
3. [Step 2: Update gradle.properties](#step-2-update-gradleproperties)
4. [Step 3: Update build.gradle](#step-3-update-buildgradle)
5. [Step 4: Update settings.gradle](#step-4-update-settingsgradle)
6. [Step 5: Fix API Changes](#step-5-fix-api-changes)
7. [Step 6: Build and Test](#step-6-build-and-test)
8. [Complete Example: Adding Fabric 1.22.0](#complete-example-adding-fabric-1220)
9. [Version Notes](#version-notes)
10. [Troubleshooting](#troubleshooting)

---

## Overview

Adding a new Minecraft version involves:

1. Copying an existing version directory as a template
2. Updating version numbers in `gradle.properties`
3. Updating dependencies in `build.gradle`
4. Adding the new module to `settings.gradle`
5. Fixing any API changes introduced in the new MC version
6. Testing the build and in-game functionality

### Time Estimate

- **Minor version (1.21.X → 1.21.Y)**: 15-30 minutes
- **Major version (1.21 → 1.22)**: 1-4 hours depending on API changes

---

## Prerequisites

Before starting, gather this information:

### For Fabric

1. **Minecraft version** (e.g., `1.22.0`)
2. **Fabric Loader version** - Check [Fabric Downloads](https://fabricmc.net/use/)
3. **Fabric API version** - Check [Fabric API](https://modrinth.com/mod/fabric-api)
4. **Loom version** (optional) - Check [Fabric Loom](https://fabricmc.net/develop/)

### For NeoForge

1. **Minecraft version** (e.g., `1.22.0`)
2. **NeoForge version** — [NeoForge Downloads](https://neoforged.net/)
3. **NeoForm version**

---

## Step 2: Update gradle.properties

### File: `appFabric/1.22.0/gradle.properties` (or NeoForge equivalent)

### Fabric Example

**Before (1.21.11):**
```properties
# Mod Properties
mod_version=0.4.0
maven_group=net.kenddie
archives_base_name=fantasy-weapons-fabric-1.21.11

# Minecraft/Fabric Properties
minecraft_version=1.21.4
loader_version=0.16.9
fabric_version=0.141.3+1.21.4
```

**After (1.22.0):**
```properties
# Mod Properties
mod_version=0.4.0
maven_group=net.kenddie
archives_base_name=fantasy-weapons-fabric-1.22.0

# Minecraft/Fabric Properties
minecraft_version=1.22.0
loader_version=0.17.0
fabric_version=0.150.0+1.22.0
```

### NeoForge Example

**Before (1.21.11):**
```properties
# Mod Properties
mod_version=0.4.0
maven_group=net.kenddie
archives_base_name=fantasy-weapons-neoforge-1.21.11
mod_id=fantasy_weapons

# NeoForge Properties
minecraft_version=1.21.4
neoforge_version=21.11.38-beta
neoform_version=1.21.4-20250127.140937
```

**After (1.22.0):**
```properties
# Mod Properties
mod_version=0.4.0
maven_group=net.kenddie
archives_base_name=fantasy-weapons-neoforge-1.22.0
mod_id=fantasy_weapons

# NeoForge Properties
minecraft_version=1.22.0
neoforge_version=22.0.1
neoform_version=1.22.0-20260401.120000
```

### Finding Version Numbers

**Fabric Loader:**
```bash
curl -s https://meta.fabricmc.net/v2/versions/loader | jq '.[0].version'
```

**Fabric API:**
- Visit https://modrinth.com/mod/fabric-api/versions
- Filter by your MC version
- Copy the version string (e.g., `0.150.0+1.22.0`)

**NeoForge:**
- Visit https://neoforged.net/
- Look for your MC version
- Note the NeoForge version (e.g., `22.0.1`)

---

## Step 3: Update build.gradle

Usually **no changes needed** unless plugin versions need updating for the new MC version.

### Potential Plugin Updates

**Fabric Loom:**
```gradle
plugins {
    id 'fabric-loom' version '1.14.0'    // Update if needed
}
```

**NeoForge ModDev:**
```gradle
plugins {
    id 'net.neoforged.moddev' version '2.1.0'    // Update if needed
}
```

### Check Plugin Compatibility

Before updating plugins, verify they support your target MC version:
- Fabric Loom: https://fabricmc.net/develop/
- NeoForge ModDev: https://github.com/neoforged/ModDevGradle/releases

---

## Step 4: Update settings.gradle

### File: `FANTASY-WEAPONS/settings.gradle`

Add your new version to the appropriate section:

```gradle
// Shared pure-Java module
include(':appCommon')

// Legacy 1.20.1 modules
include(':appFabric:1.20.1')
include(':appForge:1.20.1')

// Fabric loaders
include(':appFabric:1.21.1')
include(':appFabric:1.21.4')
include(':appFabric:1.21.10')
include(':appFabric:1.21.11')
include(':appFabric:1.22.0')     // ← Add new version

// NeoForge loaders
include(':appNeoForge:1.21.1')
include(':appNeoForge:1.21.4')
include(':appNeoForge:1.21.10')
include(':appNeoForge:1.21.11')
include(':appNeoForge:1.22.0')    // ← Add new version
```

### Important Notes

- Keep versions in ascending order
- The module name `:appFabric:1.22.0` maps to directory `appFabric/1.22.0/`

---

## Step 5: Fix API Changes

Build and fix errors:

```powershell
./gradlew :appFabric:1.22.0:build
```

See [05_NEOFORGE_API_CHANGES.md](05_NEOFORGE_API_CHANGES.md) and [06_FABRIC_API_CHANGES.md](06_FABRIC_API_CHANGES.md) for known changes between versions.

### Research Resources

- Fabric API changelogs: https://github.com/FabricMC/fabric
- NeoForge changelogs: https://github.com/neoforged/NeoForge
- Fabric Discord: https://discord.gg/v6v4pMv
- NeoForge Discord: https://discord.gg/neoforged

---

## Step 6: Build and Test

```powershell
# Build
./gradlew :appFabric:1.22.0:build

# Run client
./gradlew :appFabric:1.22.0:runClient   # Fabric
./gradlew :appNeoForge:1.22.0:runClient  # NeoForge
```

### Test Checklist

- [ ] Game launches without crashes
- [ ] Creative tab "Fantasy Weapons" appears with all weapons
- [ ] Weapons show correct textures in inventory and when held
- [ ] Config file generates correctly
- [ ] Recipes work at smithing table

---

## Complete Example: Adding Fabric 1.22.0

### Step 1: Copy Directory

```powershell
Copy-Item -Recurse appFabric/1.21.11 appFabric/1.22.0
Remove-Item -Recurse appFabric/1.22.0/build -ErrorAction SilentlyContinue
```

### Step 2: Update gradle.properties

```properties
mod_version=0.4.0
maven_group=net.kenddie
archives_base_name=fantasy-weapons-fabric-1.22.0

minecraft_version=1.22.0
loader_version=0.17.0
fabric_version=0.150.0+1.22.0
```

### Step 3: Update settings.gradle

```gradle
include(':appFabric:1.21.11')
include(':appFabric:1.22.0')     // Add this line
```

### Step 4: Build and Fix

```powershell
./gradlew :appFabric:1.22.0:build
```

### Step 5: Fix Errors (hypothetical example)

If `ResourceLocation` changed to `Identifier`:

**FWItems.java:**
```java
// Before
import net.minecraft.resources.ResourceLocation;
ResourceLocation.fromNamespaceAndPath(MOD_ID, name)

// After
import net.minecraft.resources.Identifier;
Identifier.of(MOD_ID, name)
```

### Step 6: Test

```powershell
./gradlew :appFabric:1.22.0:runClient
```

---

## Version Notes

### Fabric

| MC Version | Key Changes |
|------------|-------------|
| 1.20.1 | Legacy, self-contained (own resources, `recipes/` plural, Fabric Loader) |
| 1.21.1 | Baseline for appCommon integration |
| 1.21.4 | ToolMaterial replaces Tier, SwordItem constructor changed |
| 1.21.10 | Weapon extends Item (not SwordItem), ModelLoadingPlugin API changed |
| 1.21.11 | Minor API cleanups |

### NeoForge / Forge

| MC Version | Key Changes |
|------------|-------------|
| 1.20.1 | Legacy Minecraft Forge, self-contained (own resources, `recipes/` plural) |
| 1.21.1 | Baseline NeoForge with appCommon integration |
| 1.21.4 | ToolMaterial, ModelEvent changes |
| 1.21.10 | StandaloneModelKey, ItemModel API |
| 1.21.11 | Beta, minor changes |

### Removing Old Versions

1. Delete the directory (e.g. `appFabric/1.20.1/`)
2. Remove from `settings.gradle`

---

## Troubleshooting

### "Could not resolve net.minecraft:minecraft:1.22.0"

**Cause:** Mappings or MC JAR not available yet.

**Fix:** Wait for Mojang/Fabric/NeoForge to publish mappings. Check:
- Fabric: https://fabricmc.net/develop/
- NeoForge: https://neoforged.net/

### "Could not find fabric-api:0.150.0+1.22.0"

**Cause:** Fabric API version doesn't exist yet.

**Fix:** Check Modrinth for available versions. Use the latest available.

### "Method X does not exist"

**Cause:** API changed in new MC version.

**Fix:** Research the change (see Step 5) and update your code accordingly.

### Run fails with "Mixin injection failed"

**Cause:** Internal Minecraft code changed, breaking mixins.

**Fix:** Review and update mixin targets. This mod doesn't use mixins, but if you add them, be aware of this.

---

## Summary

1. **Copy** closest existing version directory
2. **Update** `gradle.properties` with new version numbers
3. **Add** to `settings.gradle`
4. **Build** and fix API errors
5. **Test** in-game

`appCommon` never needs changes — all version-specific code stays in the loader directories.
