# Fabric API Changes: 1.21.1 to 1.21.10

This document provides an exhaustive breakdown of all API changes between Fabric for Minecraft 1.21.1 and Fabric for Minecraft 1.21.10 as they affect the Fantasy Weapons mod. Understanding these changes is essential for porting mods between these versions.

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [Item System Changes](#item-system-changes)
3. [Tool Material System Changes](#tool-material-system-changes)
4. [Model Loading Plugin Changes](#model-loading-plugin-changes)
5. [Baked Model Changes](#baked-model-changes)
6. [Registration System Changes](#registration-system-changes)
7. [Complete Migration Guide](#complete-migration-guide)
8. [Code Comparison Tables](#code-comparison-tables)

---

## Executive Summary

### Major Breaking Changes

| Category | 1.21.1 | 1.21.10 |
|----------|--------|---------|
| Weapon Base Class | `extends SwordItem` | `extends Item` |
| Material System | `Tier` interface | `ToolMaterial` class |
| Attribute Application | `SwordItem.createAttributes()` | `ToolMaterial.applySwordProperties()` |
| Model Interface | `BakedModel` | `ItemModel` |
| Model Plugin Callback | `modifyModelAfterBake()` | `modifyItemModelAfterBake()` |
| Model Wrapper | `EpicBakedModel implements BakedModel` | `EpicBakedModel implements ItemModel` |
| Render Context | `RenderContext.itemTransformationMode()` | `ItemDisplayContext` parameter |

### Impact Assessment

- **FWWeaponBasicSwordItem.java**: Complete rewrite required
- **FWItems.java**: Minor changes (material type)
- **FantasyWeaponsClientFabric.java**: Major rewrite required
- **EpicBakedModel.java**: Complete rewrite required (different interface)

---

## Item System Changes

### Weapon Class Inheritance

#### 1.21.1: SwordItem Extension
```java
package net.kenddie.fantasyweapons.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class FWWeaponBasicSwordItem extends SwordItem {

    private final float attackDamage;

    public FWWeaponBasicSwordItem(Tier tier, float damage, float speed, Item.Properties properties) {
        super(tier, properties.attributes(SwordItem.createAttributes(tier, (int) damage, speed)));
        this.attackDamage = damage;
    }

    public float getDamage() {
        return attackDamage;
    }
}
```

**Key characteristics in 1.21.1:**
- Extends `SwordItem` directly
- Uses `Tier` interface (e.g., `Tiers.NETHERITE`)
- Calls `SwordItem.createAttributes()` static method
- Applies attributes via `properties.attributes()`
- Inherits all SwordItem behavior automatically

#### 1.21.10: Item Extension with ToolMaterial
```java
package net.kenddie.fantasyweapons.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public class FWWeaponBasicSwordItem extends Item {

    private final float attackDamage;

    public FWWeaponBasicSwordItem(ToolMaterial material, float damage, float speed, Item.Properties properties) {
        super(material.applySwordProperties(properties, damage, speed));
        this.attackDamage = damage;
    }

    public float getDamage() {
        return attackDamage;
    }
}
```

**Key characteristics in 1.21.10:**
- Extends `Item` directly (not SwordItem)
- Uses `ToolMaterial` class (e.g., `ToolMaterial.NETHERITE`)
- Calls `material.applySwordProperties()` instance method
- Material method handles all attribute application
- Tool behavior is data-driven, not inheritance-driven

### Why This Change Matters

Mojang decoupled tool behavior from class hierarchy in 1.21.10:

1. **Flexibility**: Any Item can have sword properties
2. **Data-driven**: Stats come from the material, not the class
3. **Cleaner composition**: No need to extend specific tool classes

---

## Tool Material System Changes

### Tier vs ToolMaterial

#### 1.21.1: Tier Interface
```java
public interface Tier {
    int getUses();                           // Durability
    float getSpeed();                        // Mining speed
    float getAttackDamageBonus();           // Base damage bonus
    int getLevel();                          // Harvest level
    int getEnchantmentValue();              // Enchantability
    Ingredient getRepairIngredient();       // Repair material
}

// Usage
Tiers.NETHERITE   // Enum implementing Tier
Tiers.DIAMOND
Tiers.IRON
Tiers.STONE
Tiers.WOOD
```

#### 1.21.10: ToolMaterial Class
```java
public final class ToolMaterial {
    // Static instances
    public static final ToolMaterial NETHERITE = ...;
    public static final ToolMaterial DIAMOND = ...;
    public static final ToolMaterial IRON = ...;
    public static final ToolMaterial STONE = ...;
    public static final ToolMaterial WOOD = ...;
    
    // Instance methods
    public Item.Properties applySwordProperties(Item.Properties props, float damage, float speed);
    public Item.Properties applyAxeProperties(Item.Properties props, float damage, float speed);
    public Item.Properties applyPickaxeProperties(Item.Properties props, float damage, float speed);
    public Item.Properties applyShovelProperties(Item.Properties props, float damage, float speed);
    public Item.Properties applyHoeProperties(Item.Properties props, float damage, float speed);
}
```

### Migration Pattern

```java
// 1.21.1
import net.minecraft.world.item.Tiers;
new FWWeaponBasicSwordItem(Tiers.NETHERITE, damage, speed, props);

// 1.21.10
import net.minecraft.world.item.ToolMaterial;
new FWWeaponBasicSwordItem(ToolMaterial.NETHERITE, damage, speed, props);
```

---

## Model Loading Plugin Changes

### Complete Plugin API Comparison

#### 1.21.1: ModelLoadingPlugin
```java
package net.kenddie.fantasyweapons.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import java.util.HashMap;
import java.util.Map;

public class FantasyWeaponsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(pluginContext -> {
            Map<ModelResourceLocation, ResourceLocation> compliances = new HashMap<>();

            for (var item : FWItems.getAllItems()) {
                var itemName = item.getDescriptionId().substring(("item." + MOD_ID + ".").length());
                var largeJsonPath = String.format("/assets/%s/models/item/%s_large.json", MOD_ID, itemName);

                try (var inputStream = FantasyWeapons.class.getResourceAsStream(largeJsonPath)) {
                    if (inputStream != null) {
                        var smallResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, itemName);
                        var largeResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, "item/" + itemName + "_large");

                        var smallModelResourceLocation = new ModelResourceLocation(smallResourceLocation, "inventory");

                        compliances.put(smallModelResourceLocation, largeResourceLocation);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Register all large models to be loaded
            pluginContext.addModels(compliances.values());

            // After baking, wrap small models with our custom model
            pluginContext.modifyModelAfterBake().register((smallBakedModel, bakeContext) -> {
                for (var compliance : compliances.entrySet()) {
                    var smallModelResourceLocation = compliance.getKey();
                    var topLevelId = bakeContext.topLevelId();
                    
                    if (topLevelId != null && topLevelId.equals(smallModelResourceLocation)) {
                        var largeResourceLoc = compliance.getValue();
                        var largeBakedModel = bakeContext.baker().bake(largeResourceLoc, bakeContext.settings());
                        
                        if (largeBakedModel != null) {
                            return new EpicBakedModel(smallBakedModel, largeBakedModel);
                        }
                    }
                }
                return smallBakedModel;
            });
        });
    }
}
```

#### 1.21.10: ModelLoadingPlugin (Updated API)
```java
package net.kenddie.fantasyweapons.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.resources.ResourceLocation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FantasyWeaponsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(pluginContext -> {
            Map<ResourceLocation, ResourceLocation> itemToLargeModel = new HashMap<>();

            for (var item : FWItems.getAllItems()) {
                var itemName = item.getDescriptionId().substring(("item." + MOD_ID + ".").length());
                var largeJsonPath = String.format("/assets/%s/models/item/%s_large.json", MOD_ID, itemName);

                try (var inputStream = FantasyWeapons.class.getResourceAsStream(largeJsonPath)) {
                    if (inputStream != null) {
                        var smallResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, itemName);
                        var largeResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, "item/" + itemName + "_large");
                        itemToLargeModel.put(smallResourceLocation, largeResourceLocation);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Note: No addModels() call needed in 1.21.10

            // Use the new modifyItemModelAfterBake() instead of modifyModelAfterBake()
            pluginContext.modifyItemModelAfterBake().register((itemModel, bakeContext) -> {
                var itemId = bakeContext.itemId();
                var largeModelId = itemToLargeModel.get(itemId);
                
                if (largeModelId != null) {
                    // Manually bake the large model into an ItemModel
                    var baker = bakeContext.bakeContext().blockModelBaker();
                    var resolved = baker.getModel(largeModelId);
                    var textureSlots = resolved.getTopTextureSlots();
                    var quads = resolved.bakeTopGeometry(textureSlots, baker, BlockModelRotation.X0_Y0);
                    var renderProps = ModelRenderProperties.fromResolvedModel(baker, resolved, textureSlots);
                    
                    ItemModel largeItemModel = new BlockModelWrapper(List.of(), quads.getAll(), renderProps);
                    return new EpicBakedModel(itemModel, largeItemModel);
                }
                return itemModel;
            });
        });
    }
}
```

### Key API Changes

| Aspect | 1.21.1 | 1.21.10 |
|--------|--------|---------|
| Register models | `pluginContext.addModels(locations)` | Not needed / automatic |
| Modify callback | `modifyModelAfterBake()` | `modifyItemModelAfterBake()` |
| Model parameter | `BakedModel` | `ItemModel` |
| Context method | `bakeContext.topLevelId()` | `bakeContext.itemId()` |
| Baker access | `bakeContext.baker().bake()` | `bakeContext.bakeContext().blockModelBaker()` |
| Model wrapping | Direct bake call | Manual geometry baking via `BlockModelWrapper` |

---

## Baked Model Changes

### Interface Comparison

#### 1.21.1: BakedModel Interface
```java
public interface BakedModel {
    // Required methods
    List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction dir, RandomSource random);
    boolean useAmbientOcclusion();
    boolean isGui3d();
    boolean usesBlockLight();
    boolean isCustomRenderer();
    TextureAtlasSprite getParticleIcon();
    ItemOverrides getOverrides();
    ItemTransforms getTransforms();
}
```

#### 1.21.10: ItemModel Interface
```java
public interface ItemModel {
    // Single required method
    void update(
        ItemStackRenderState renderState,
        ItemStack stack,
        ItemModelResolver resolver,
        ItemDisplayContext displayContext,
        @Nullable ClientLevel level,
        @Nullable ItemOwner owner,
        int seed
    );
}
```

### EpicBakedModel Comparison

#### 1.21.1 Version (Full BakedModel Implementation)
```java
package net.kenddie.fantasyweapons.fabric.model;

import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.renderer.VanillaModelEncoder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;
import java.util.function.Supplier;

public record EpicBakedModel(BakedModel smallBakedModel, BakedModel largeBakedModel) implements BakedModel {
    
    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<RandomSource> randomSupplier, RenderContext renderContext) {
        BakedModel bakedModel;

        // Check display context from render context
        var transformationMode = renderContext.itemTransformationMode();
        if (transformationMode == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || 
            transformationMode == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || 
            transformationMode == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || 
            transformationMode == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            bakedModel = largeBakedModel;
        } else {
            bakedModel = smallBakedModel;
        }

        VanillaModelEncoder.emitItemQuads(bakedModel, null, randomSupplier, renderContext);
    }

    @Override
    public ItemOverrides getOverrides() {
        return largeBakedModel.getOverrides();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return largeBakedModel.getParticleIcon();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState blockState, Direction direction, RandomSource randomSource) {
        return largeBakedModel.getQuads(blockState, direction, randomSource);
    }

    @Override
    public ItemTransforms getTransforms() {
        return largeBakedModel.getTransforms();
    }

    @Override
    public boolean isCustomRenderer() {
        return largeBakedModel.isCustomRenderer();
    }

    @Override
    public boolean isGui3d() {
        return largeBakedModel.isGui3d();
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;  // Required for Fabric Renderer API
    }

    @Override
    public boolean useAmbientOcclusion() {
        return largeBakedModel.useAmbientOcclusion();
    }

    @Override
    public boolean usesBlockLight() {
        return largeBakedModel.usesBlockLight();
    }
}
```

#### 1.21.10 Version (Simple ItemModel Implementation)
```java
package net.kenddie.fantasyweapons.fabric.model;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record EpicBakedModel(ItemModel smallItemModel, ItemModel largeItemModel) implements ItemModel {
    
    @Override
    public void update(ItemStackRenderState renderState, ItemStack itemStack, ItemModelResolver resolver,
                       ItemDisplayContext displayContext, ClientLevel level, ItemOwner owner, int seed) {
        // Display context is now a direct parameter
        if (displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || 
            displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || 
            displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || 
            displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            largeItemModel.update(renderState, itemStack, resolver, displayContext, level, owner, seed);
        } else {
            smallItemModel.update(renderState, itemStack, resolver, displayContext, level, owner, seed);
        }
    }
}
```

### Key Differences

| Aspect | 1.21.1 | 1.21.10 |
|--------|--------|---------|
| Methods to implement | 10+ | 1 |
| Display context access | `renderContext.itemTransformationMode()` | Direct `displayContext` parameter |
| Quad emission | `emitItemQuads()` manual implementation | Delegated via `update()` |
| Fabric Renderer | Required `isVanillaAdapter() = false` | Not applicable |
| Model type | `BakedModel` | `ItemModel` |

---

## Registration System Changes

### Item Registration (Unchanged)

The item registration pattern in Fabric remains the same:

```java
// Both 1.21.1 and 1.21.10
Registry.register(
    BuiltInRegistries.ITEM,
    ResourceLocation.fromNamespaceAndPath(MOD_ID, name),
    new FWWeaponBasicSwordItem(material, damage, speed, new Item.Properties())
);
```

### Creative Tab Registration (Unchanged)

```java
// Both 1.21.1 and 1.21.10
Registry.register(
    BuiltInRegistries.CREATIVE_MODE_TAB,
    ResourceLocation.fromNamespaceAndPath(MOD_ID, "fw_tab"),
    FabricItemGroup.builder()
        .title(Component.translatable("itemGroup." + MOD_ID + ".fw_tab"))
        .icon(() -> new ItemStack(ALL_ITEMS.get(1)))
        .displayItems((ctx, entries) -> ALL_ITEMS.forEach(item -> entries.accept(new ItemStack(item))))
        .build()
);
```

---

## Complete Migration Guide

### Step-by-Step Migration

#### 1. Update FWWeaponBasicSwordItem.java

```diff
  package net.kenddie.fantasyweapons.item;

- import net.minecraft.world.item.SwordItem;
- import net.minecraft.world.item.Tier;
+ import net.minecraft.world.item.Item;
+ import net.minecraft.world.item.ToolMaterial;

- public class FWWeaponBasicSwordItem extends SwordItem {
+ public class FWWeaponBasicSwordItem extends Item {

      private final float attackDamage;

-     public FWWeaponBasicSwordItem(Tier tier, float damage, float speed, Item.Properties properties) {
-         super(tier, properties.attributes(SwordItem.createAttributes(tier, (int) damage, speed)));
+     public FWWeaponBasicSwordItem(ToolMaterial material, float damage, float speed, Item.Properties properties) {
+         super(material.applySwordProperties(properties, damage, speed));
          this.attackDamage = damage;
      }

      public float getDamage() {
          return attackDamage;
      }
  }
```

#### 2. Update FWItems.java

```diff
  package net.kenddie.fantasyweapons.item;

- import net.minecraft.world.item.Tiers;
+ import net.minecraft.world.item.ToolMaterial;

  // In the registration loop:
  ALL_ITEMS.add(registerItem(name, new FWWeaponBasicSwordItem(
-         Tiers.NETHERITE, config.damage, config.speed, new Item.Properties())));
+         ToolMaterial.NETHERITE, config.damage, config.speed, new Item.Properties())));
```

#### 3. Replace EpicBakedModel.java

Delete the old file and create the new version:

```java
package net.kenddie.fantasyweapons.fabric.model;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record EpicBakedModel(ItemModel smallItemModel, ItemModel largeItemModel) implements ItemModel {
    @Override
    public void update(ItemStackRenderState renderState, ItemStack itemStack, ItemModelResolver resolver,
                       ItemDisplayContext displayContext, ClientLevel level, ItemOwner owner, int seed) {
        if (displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ||
            displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ||
            displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND ||
            displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            largeItemModel.update(renderState, itemStack, resolver, displayContext, level, owner, seed);
        } else {
            smallItemModel.update(renderState, itemStack, resolver, displayContext, level, owner, seed);
        }
    }
}
```

#### 4. Update FantasyWeaponsClientFabric.java

Replace the plugin registration with the 1.21.10 version (see full code above).

Key changes:
- Remove `addModels()` call
- Change `modifyModelAfterBake()` to `modifyItemModelAfterBake()`
- Change `bakeContext.topLevelId()` to `bakeContext.itemId()`
- Update model baking to use `blockModelBaker()` and `BlockModelWrapper`

---

## Code Comparison Tables

### Import Statements

| Purpose | 1.21.1 | 1.21.10 |
|---------|--------|---------|
| Weapon base | `SwordItem` | `Item` |
| Material | `Tier`, `Tiers` | `ToolMaterial` |
| Model interface | `BakedModel` | `ItemModel` |
| Model wrapper | - | `BlockModelWrapper` |
| Render properties | - | `ModelRenderProperties` |
| Render context | `RenderContext` | `ItemStackRenderState` |
| Model ID | `ModelResourceLocation` | `ResourceLocation` |

### Method Signatures

| Action | 1.21.1 | 1.21.10 |
|--------|--------|---------|
| Create attributes | `SwordItem.createAttributes(tier, dmg, spd)` | `material.applySwordProperties(props, dmg, spd)` |
| Emit quads | `emitItemQuads(stack, random, ctx)` | `update(state, stack, resolver, ctx, level, owner, seed)` |
| Get context | `ctx.itemTransformationMode()` | Direct `displayContext` param |
| Bake model | `bakeContext.baker().bake(loc, settings)` | `bakeContext.bakeContext().blockModelBaker().getModel(loc)` |

### Plugin Callbacks

| Purpose | 1.21.1 | 1.21.10 |
|---------|--------|---------|
| Add extra models | `pluginContext.addModels(locs)` | Not needed |
| Modify baked | `modifyModelAfterBake().register()` | `modifyItemModelAfterBake().register()` |
| Model type | `BakedModel` | `ItemModel` |
| Context access | `bakeContext.topLevelId()` | `bakeContext.itemId()` |

### Fabric-Specific Considerations

| Aspect | 1.21.1 | 1.21.10 |
|--------|--------|---------|
| Renderer API | Fabric Renderer v1 | Updated, different interface |
| VanillaModelEncoder | Used for quad emission | Not needed |
| isVanillaAdapter | Must return false | N/A |

---

## Summary

The migration from Fabric 1.21.1 to 1.21.10 involves:

1. **Weapon class restructuring**: `SwordItem` → `Item` with `ToolMaterial`
2. **Model interface change**: `BakedModel` → `ItemModel`
3. **Plugin API updates**: New callbacks for item-specific model modification
4. **Simpler model wrapper**: 1 method vs 10+ methods to implement
5. **Direct display context**: No longer need to extract from render context

The new API is cleaner and more focused, but requires rewriting all model-related code.
