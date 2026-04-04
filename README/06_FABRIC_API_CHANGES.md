# Fabric API Changes: 1.21.1 to 1.21.10

All API changes between Fabric 1.21.1 and 1.21.10 that affect Fantasy Weapons.

---

## Table of Contents

1. [Summary](#summary)
2. [Item System Changes](#item-system-changes)
3. [Tool Material System Changes](#tool-material-system-changes)
4. [Model Loading Plugin Changes](#model-loading-plugin-changes)
5. [Baked Model Changes](#baked-model-changes)
6. [Migration Guide](#migration-guide)
7. [Quick Reference Tables](#quick-reference-tables)

---

## Summary

| Category | 1.21.1 | 1.21.10 |
|----------|--------|---------|
| Weapon Base Class | `extends SwordItem` | `extends Item` |
| Material System | `Tier` interface | `ToolMaterial` class |
| Attribute Application | `SwordItem.createAttributes()` | `ToolMaterial.applySwordProperties()` |
| Model Interface | `BakedModel` | `ItemModel` |
| Model Plugin Callback | `modifyModelAfterBake()` | `modifyItemModelAfterBake()` |
| Model Wrapper | `EpicBakedModel implements BakedModel` | `EpicBakedModel implements ItemModel` |
| Render Context | `RenderContext.itemTransformationMode()` | `ItemDisplayContext` parameter |

Files affected:
- **FWWeaponBasicSwordItem.java**: Complete rewrite (`SwordItem` → `Item`)
- **FWItems.java**: Minor changes (material type)
- **FantasyWeaponsClientFabric.java**: Major rewrite (new plugin API)
- **EpicBakedModel.java**: Complete rewrite (`BakedModel` → `ItemModel`)

---

## Item System Changes

### Weapon Class Inheritance

**1.21.1:**
```java
public class FWWeaponBasicSwordItem extends SwordItem {
    private final float attackDamage;

    public FWWeaponBasicSwordItem(Tier tier, float damage, float speed, Item.Properties properties) {
        super(tier, properties.attributes(SwordItem.createAttributes(tier, (int) damage, speed)));
        this.attackDamage = damage;
    }

    public float getDamage() { return attackDamage; }
}
```

**1.21.10:**
```java
public class FWWeaponBasicSwordItem extends Item {
    private final float attackDamage;

    public FWWeaponBasicSwordItem(ToolMaterial material, float damage, float speed, Item.Properties properties) {
        super(material.applySwordProperties(properties, damage, speed));
        this.attackDamage = damage;
    }

    public float getDamage() { return attackDamage; }
}
```

---

## Tool Material System Changes

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

### 1.21.1: ModelLoadingPlugin
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

### 1.21.10: ModelLoadingPlugin (Updated API)
```java
package net.kenddie.fantasyweapons.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.SimpleUnbakedExtraModel;
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

            // Register large models with the model loader (required since 1.21.10)
            pluginContext.addModel(
                    ExtraModelKey.create(largeResourceLocation::toString),
                    SimpleUnbakedExtraModel.blockStateModel(largeResourceLocation)
            );

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
| Register models | `pluginContext.addModels(locations)` | `pluginContext.addModel(ExtraModelKey, SimpleUnbakedExtraModel)` |
| Modify callback | `modifyModelAfterBake()` | `modifyItemModelAfterBake()` |
| Model parameter | `BakedModel` | `ItemModel` |
| Context method | `bakeContext.topLevelId()` | `bakeContext.itemId()` |
| Baker access | `bakeContext.baker().bake()` | `bakeContext.bakeContext().blockModelBaker()` |
| Model wrapping | Direct bake call | Manual geometry baking via `BlockModelWrapper` |

---

## Baked Model Changes

1.21.1 uses `BakedModel` (10+ methods to implement). 1.21.10 uses `ItemModel` (single `update()` method with `ItemDisplayContext` as a direct parameter).

### EpicBakedModel: 1.21.1
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

### EpicBakedModel: 1.21.10
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

| Aspect | 1.21.1 | 1.21.10 |
|--------|--------|---------|
| Methods to implement | 10+ | 1 |
| Display context access | `renderContext.itemTransformationMode()` | Direct `displayContext` parameter |
| Quad emission | `emitItemQuads()` manual implementation | Delegated via `update()` |
| Fabric Renderer | Required `isVanillaAdapter() = false` | Not applicable |
| Model type | `BakedModel` | `ItemModel` |

Item and creative tab registration are unchanged between 1.21.1 and 1.21.10.

---

## Migration Guide

### 1. Update FWWeaponBasicSwordItem.java

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

### 2. Update FWItems.java

```diff
  package net.kenddie.fantasyweapons.item;

- import net.minecraft.world.item.Tiers;
+ import net.minecraft.world.item.ToolMaterial;

  // In the registration loop:
  ALL_ITEMS.add(registerItem(name, new FWWeaponBasicSwordItem(
-         Tiers.NETHERITE, config.damage, config.speed, new Item.Properties())));
+         ToolMaterial.NETHERITE, config.damage, config.speed, new Item.Properties())));
```

### 3. Replace EpicBakedModel.java

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

### 4. Update FantasyWeaponsClientFabric.java

- Remove `addModels()` call
- `modifyModelAfterBake()` → `modifyItemModelAfterBake()`
- `bakeContext.topLevelId()` → `bakeContext.itemId()`
- Bake via `blockModelBaker()` and `BlockModelWrapper`

---

## Quick Reference Tables

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

### Fabric Renderer

| Aspect | 1.21.1 | 1.21.10 |
|--------|--------|---------|
| Renderer API | Fabric Renderer v1 | Updated, different interface |
| VanillaModelEncoder | Used for quad emission | Not needed |
| isVanillaAdapter | Must return false | N/A |
