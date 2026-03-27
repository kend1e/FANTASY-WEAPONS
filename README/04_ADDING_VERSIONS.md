# How to Add New Minecraft Versions

This document provides comprehensive instructions for adding support for a new Minecraft version to Fantasy Weapons. This is typically needed when Mojang releases a new Minecraft version and you want the mod to support it.

---

## Table of Contents

1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Step 1: Identify the Target Version](#step-1-identify-the-target-version)
4. [Step 2: Choose a Base Version to Copy](#step-2-choose-a-base-version-to-copy)
5. [Step 3: Create the New Version Directory](#step-3-create-the-new-version-directory)
6. [Step 4: Update gradle.properties](#step-4-update-gradleproperties)
7. [Step 5: Update build.gradle](#step-5-update-buildgradle)
8. [Step 6: Update settings.gradle](#step-6-update-settingsgradle)
9. [Step 7: Fix API Changes](#step-7-fix-api-changes)
10. [Step 8: Test the Build](#step-8-test-the-build)
11. [Step 9: Test In-Game](#step-9-test-in-game)
12. [Complete Example: Adding Fabric 1.22.0](#complete-example-adding-fabric-1220)
13. [Version-Specific Considerations](#version-specific-considerations)

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
2. **NeoForge version** - Check [NeoForge Downloads](https://neoforged.net/)
3. **NeoForm version** - Usually same as MC version
4. **ModDev plugin version** (optional) - Check [NeoForge ModDev](https://github.com/neoforged/ModDevGradle)

---

## Step 1: Identify the Target Version

Determine exactly what version you're adding:

```
Minecraft Version: 1.22.0
Loader: Fabric (or NeoForge)
Full Directory Name: appFabric/1.22.0 (or appNeoForge/1.22.0)
```

### Version Naming Convention

- Use the **exact Minecraft version** (e.g., `1.21.1`, not `1.21`)
- Create separate directories for different loaders
- Snapshot versions: Use the snapshot name (e.g., `24w36a`)

---

## Step 2: Choose a Base Version to Copy

Copy from the **closest existing version** to minimize API changes:

| Adding | Copy From |
|--------|-----------|
| Fabric 1.22.0 | appFabric/1.21.11 (or latest 1.21.x) |
| NeoForge 1.22.0 | appNeoForge/1.21.11 (or latest 1.21.x) |
| Fabric 1.21.12 | appFabric/1.21.11 |
| NeoForge 1.21.5 | appNeoForge/1.21.4 |

**Why this matters:** Copying from a closer version means fewer API changes to fix.

---

## Step 3: Create the New Version Directory

### Copy the Directory

```powershell
# For Fabric
Copy-Item -Recurse appFabric/1.21.11 appFabric/1.22.0

# For NeoForge
Copy-Item -Recurse appNeoForge/1.21.11 appNeoForge/1.22.0
```

### Clean the Build Directory

```powershell
# Remove old build artifacts
Remove-Item -Recurse appFabric/1.22.0/build -ErrorAction SilentlyContinue
Remove-Item -Recurse appFabric/1.22.0/.gradle -ErrorAction SilentlyContinue
Remove-Item appFabric/1.22.0/*.lock -ErrorAction SilentlyContinue
```

### Directory Structure After Copy

```
appFabric/
├── 1.21.11/           # Original
│   ├── build.gradle
│   ├── gradle.properties
│   └── src/main/...
└── 1.22.0/            # New copy
    ├── build.gradle
    ├── gradle.properties
    └── src/main/...
```

---

## Step 4: Update gradle.properties

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

## Step 5: Update build.gradle

Usually you **don't need to change `build.gradle`** unless:

1. Plugin versions need updating
2. New dependencies are required
3. API changes require build config changes

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

## Step 6: Update settings.gradle

### File: `FANTASY-WEAPONS/settings.gradle`

Add your new version to the appropriate section:

```gradle
// Shared pure-Java module
include(':appCommon')

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

- Keep versions in ascending order for readability
- The module name `:appFabric:1.22.0` maps to directory `appFabric/1.22.0/`
- Commit `settings.gradle` changes separately if needed

---

## Step 7: Fix API Changes

This is where most of the work happens. Run a test build to see what breaks:

```powershell
./gradlew :appFabric:1.22.0:build 2>&1 | Out-File build-errors.txt
```

### Common API Changes

#### Item Registration

**1.21.x:**
```java
new Item(new Item.Properties())
```

**Potential 1.22.x change:**
```java
new Item(new Item.Properties().setId(resourceKey))
```

#### Resource Locations

**1.21.1:**
```java
ResourceLocation.fromNamespaceAndPath("mod_id", "path")
```

**1.21.10+ (may change again):**
```java
Identifier.of("mod_id", "path")
```

#### Sword Items

**1.21.1:**
```java
public class Weapon extends SwordItem {
    public Weapon(Tier tier, float damage, float speed, Properties props) {
        super(tier, props.attributes(SwordItem.createAttributes(tier, (int) damage, speed)));
    }
}
```

**1.21.10+:**
```java
public class Weapon extends Item {
    public Weapon(ToolMaterial material, float damage, float speed, Properties props) {
        super(material.applySwordProperties(props, damage, speed));
    }
}
```

#### Model APIs (Fabric)

**1.21.1:**
```java
pluginContext.modifyModelAfterBake().register((model, ctx) -> {
    return new EpicBakedModel(model, largeModel);
});
```

**1.21.10+:**
```java
pluginContext.modifyItemModelAfterBake().register((itemModel, ctx) -> {
    return new EpicBakedModel(itemModel, largeModel);
});
```

#### Model APIs (NeoForge)

**1.21.1:**
```java
@SubscribeEvent
public void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
    var models = event.getModels();
    models.put(location, new EpicBakedModel(small, large));
}
```

**1.21.10+:**
```java
@SubscribeEvent
public void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
    var itemModels = event.getBakingResult().itemStackModels();
    itemModels.put(location, new EpicBakedModel(small, large));
}
```

### How to Research API Changes

1. **Check Fabric API changelogs:** https://github.com/FabricMC/fabric/blob/1.22/CHANGELOG.md
2. **Check NeoForge changelogs:** https://github.com/neoforged/NeoForge/blob/1.22.x/CHANGELOG.md
3. **Search Fabric Discord:** https://discord.gg/v6v4pMv
4. **Search NeoForge Discord:** https://discord.gg/neoforged
5. **Compare with other mods:** Find a similar mod that already supports the new version

---

## Step 8: Test the Build

### Build the New Version

```powershell
./gradlew :appFabric:1.22.0:build
```

### Expected Output

```
> Task :appCommon:compileJava UP-TO-DATE
> Task :appCommon:processResources UP-TO-DATE
> Task :appFabric:1.22.0:compileJava
> Task :appFabric:1.22.0:processResources
> Task :appFabric:1.22.0:classes
> Task :appFabric:1.22.0:jar
> Task :appFabric:1.22.0:shadowJar
> Task :appFabric:1.22.0:remapJar
> Task :appFabric:1.22.0:build

BUILD SUCCESSFUL
```

### If Build Fails

1. Read error messages carefully
2. Fix one error at a time
3. Rebuild after each fix
4. Commit working changes incrementally

---

## Step 9: Test In-Game

### Launch Development Client

```powershell
# Fabric
./gradlew :appFabric:1.22.0:runClient

# NeoForge
./gradlew :appNeoForge:1.22.0:runClient
```

### Test Checklist

- [ ] Game launches without crashes
- [ ] Creative tab "Fantasy Weapons" appears
- [ ] All weapons are listed
- [ ] Weapons show correct textures in inventory
- [ ] Weapons show large textures when held
- [ ] Config file generates correctly
- [ ] Recipes work at smithing table
- [ ] No console errors related to the mod

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

## Version-Specific Considerations

### Fabric Version Notes

| MC Version | Key Changes |
|------------|-------------|
| 1.21.1 | Baseline |
| 1.21.4 | ToolMaterial replaces Tier, SwordItem constructor changed |
| 1.21.10 | Weapon extends Item (not SwordItem), ModelLoadingPlugin API changed |
| 1.21.11 | Minor API cleanups |

### NeoForge Version Notes

| MC Version | Key Changes |
|------------|-------------|
| 1.21.1 | Baseline |
| 1.21.4 | ToolMaterial, ModelEvent changes |
| 1.21.10 | StandaloneModelKey, ItemModel API |
| 1.21.11 | Beta, minor changes |

### Snapshot Versions

When adding snapshot support:

1. Use the snapshot identifier (e.g., `24w36a`)
2. Expect frequent breaking changes
3. Consider maintaining in a separate branch
4. Mark clearly as experimental

### Removing Old Versions

When dropping support for old MC versions:

1. Remove the directory (`appFabric/1.20.1/`)
2. Remove from `settings.gradle`
3. Update any documentation
4. Consider keeping the last supported version for each MC major version

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

**Fix:** Research the change (see Step 7) and update your code accordingly.

### Run fails with "Mixin injection failed"

**Cause:** Internal Minecraft code changed, breaking mixins.

**Fix:** Review and update mixin targets. This mod doesn't use mixins, but if you add them, be aware of this.

---

## Summary

Adding a new Minecraft version:

1. **Copy** closest existing version directory
2. **Update** `gradle.properties` with new versions
3. **Add** to `settings.gradle`
4. **Build** and fix any API errors
5. **Test** in-game thoroughly

The unified structure means `appCommon` never needs changes—all version-specific code stays in the loader directories.
