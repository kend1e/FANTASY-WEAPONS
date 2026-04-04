# NeoForge API Changes: 1.21.1 to 1.21.10

This document provides an exhaustive breakdown of all API changes between NeoForge for Minecraft 1.21.1 and NeoForge for Minecraft 1.21.10 as they affect the Fantasy Weapons mod. Understanding these changes is essential for porting mods between these versions.

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [Item System Changes](#item-system-changes)
3. [Tool Material System Changes](#tool-material-system-changes)
4. [Model System Changes](#model-system-changes)
5. [Registration System Changes](#registration-system-changes)
6. [Client Event System Changes](#client-event-system-changes)
7. [Resource Location Changes](#resource-location-changes)
8. [Complete Migration Guide](#complete-migration-guide)
9. [Code Comparison Tables](#code-comparison-tables)

---

## Executive Summary

### Major Breaking Changes

| Category | 1.21.1 | 1.21.10 |
|----------|--------|---------|
| Weapon Base Class | `extends SwordItem` | `extends Item` |
| Material System | `Tier` interface | `ToolMaterial` class |
| Attribute Application | `createAttributes()` static | `applySwordProperties()` instance |
| Model Type | `BakedModel` | `ItemModel` |
| Model Registration | `ModelEvent.RegisterAdditional` | `ModelEvent.RegisterStandalone` |
| Model Events | `event.getModels()` | `event.getBakingResult().itemStackModels()` |

### Impact Assessment

- **FWWeaponBasicSwordItem.java**: Complete rewrite required
- **FWItems.java**: Minor changes (material type)
- **FantasyWeaponsClientNeoForge.java**: Major rewrite required
- **EpicBakedModel.java**: Complete rewrite required (different interface)

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
new FWWeaponBasicSwordItem(Tiers.NETHERITE, damage, speed, props);

// 1.21.10
new FWWeaponBasicSwordItem(ToolMaterial.NETHERITE, damage, speed, props);
```

`ToolMaterial.applySwordProperties()` internally sets durability, attributes, enchantability, and repair ingredients:
public Item.Properties applySwordProperties(Item.Properties props, float damage, float speed) {
    return props
        .durability(this.durability)
        .attributes(this.createSwordAttributes(damage, speed))
        .enchantable(this.enchantmentValue)
        .repairable(this.repairIngredient);
}
```

---

## Model System Changes

### BakedModel (1.21.1) vs ItemModel (1.21.10)

#### 1.21.1: BakedModel Interface
```java
public interface BakedModel {
    List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random);
    boolean useAmbientOcclusion();
    boolean isGui3d();
    boolean usesBlockLight();
    boolean isCustomRenderer();
    TextureAtlasSprite getParticleIcon();
    ItemOverrides getOverrides();
    ItemTransforms getTransforms();
    
    // NeoForge extension
    BakedModel applyTransform(ItemDisplayContext context, PoseStack pose, boolean leftHand);
}
```

#### 1.21.10: ItemModel Interface
```java
public interface ItemModel {
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

### EpicBakedModel

**1.21.1** (implements `BakedModel` — 10+ methods):
```java
package net.kenddie.fantasyweapons.neoforge.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;

@SuppressWarnings("deprecation")
public record EpicBakedModel(BakedModel smallBakedModel, BakedModel largeBakedModel) implements BakedModel {
    
    @Override
    public BakedModel applyTransform(ItemDisplayContext context, PoseStack poseStack, boolean leftHand) {
        BakedModel model;
        if (context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ||
            context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ||
            context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND ||
            context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            model = largeBakedModel;
        } else {
            model = smallBakedModel;
        }
        return model.applyTransform(context, poseStack, leftHand);
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
    public List<BakedQuad> getQuads(BlockState state, Direction direction, RandomSource random) {
        return largeBakedModel.getQuads(state, direction, random);
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
    public boolean useAmbientOcclusion() {
        return largeBakedModel.useAmbientOcclusion();
    }

    @Override
    public boolean usesBlockLight() {
        return largeBakedModel.usesBlockLight();
    }
}
```

**1.21.10** (implements `ItemModel` — 1 method):
```java

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record EpicBakedModel(ItemModel smallItemModel, ItemModel largeItemModel) implements ItemModel {
    
    @Override
    public void update(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver resolver,
                       ItemDisplayContext context, ClientLevel level, ItemOwner owner, int seed) {
        if (context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ||
            context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ||
            context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND ||
            context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            largeItemModel.update(renderState, stack, resolver, context, level, owner, seed);
        } else {
            smallItemModel.update(renderState, stack, resolver, context, level, owner, seed);
        }
    }
}
```

---

## Registration System Changes

### 1.21.1: RegisterAdditional + ModifyBakingResult
```java
package net.kenddie.fantasyweapons.neoforge;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import java.util.HashMap;
import java.util.Map;

public class FantasyWeaponsClientNeoForge {
    private static final Map<ModelResourceLocation, ModelResourceLocation> COMPLIANCES = new HashMap<>();

    @SubscribeEvent
    public void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        for (var item : FWItems.getAllItems()) {
            var itemName = item.getDescriptionId().substring(("item." + MOD_ID + ".").length());
            var largeJsonPath = String.format("/assets/%s/models/item/%s_large.json", MOD_ID, itemName);

            try (var inputStream = FantasyWeapons.class.getResourceAsStream(largeJsonPath)) {
                if (inputStream != null) {
                    var smallRL = ResourceLocation.fromNamespaceAndPath(MOD_ID, itemName);
                    var largeRL = ResourceLocation.fromNamespaceAndPath(MOD_ID, itemName + "_large");

                    var smallModelRL = new ModelResourceLocation(smallRL, "inventory");
                    var largeModelRL = new ModelResourceLocation(largeRL, "inventory");

                    COMPLIANCES.put(smallModelRL, largeModelRL);

                    event.register(largeModelRL);  // Register the large model
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
```

### 1.21.10: RegisterStandalone + ModifyBakingResult
```java
package net.kenddie.fantasyweapons.neoforge;

import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FantasyWeaponsClientNeoForge {
    private static final Map<ResourceLocation, StandaloneModelKey<ItemModel>> LARGE_MODEL_KEYS = new HashMap<>();

    @SubscribeEvent
    public void onRegisterStandalone(ModelEvent.RegisterStandalone event) {
        for (var item : FWItems.getAllItems()) {
            var itemName = item.getDescriptionId().substring(("item." + MOD_ID + ".").length());
            var largeJsonPath = String.format("/assets/%s/models/item/%s_large.json", MOD_ID, itemName);

            try (var inputStream = FantasyWeapons.class.getResourceAsStream(largeJsonPath)) {
                if (inputStream != null) {
                    var smallRL = ResourceLocation.fromNamespaceAndPath(MOD_ID, itemName);
                    var largeModelLoc = ResourceLocation.fromNamespaceAndPath(MOD_ID, "item/" + itemName + "_large");

                    // Create a unique key for this model
                    var key = new StandaloneModelKey<ItemModel>(() -> "fantasy_weapons_large_" + itemName);
                    
                    // Create an unbaked model that knows how to bake itself
                    var unbaked = new SimpleUnbakedStandaloneModel<>(largeModelLoc, (resolved, baker) -> {
                        var textureSlots = resolved.getTopTextureSlots();
                        var quads = resolved.bakeTopGeometry(textureSlots, baker, BlockModelRotation.X0_Y0);
                        var renderProps = ModelRenderProperties.fromResolvedModel(baker, resolved, textureSlots);
                        return (ItemModel) new BlockModelWrapper(List.of(), quads.getAll(), renderProps);
                    });

                    event.register(key, unbaked);
                    LARGE_MODEL_KEYS.put(smallRL, key);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
```

### Key Differences

| Aspect | 1.21.1 | 1.21.10 |
|--------|--------|---------|
| Event | `ModelEvent.RegisterAdditional` | `ModelEvent.RegisterStandalone` |
| Storage | `ModelResourceLocation` | `StandaloneModelKey<ItemModel>` |
| Registration | `event.register(modelRL)` | `event.register(key, unbakedModel)` |


---

## Client Event System Changes

### ModifyBakingResult

**1.21.1:**
```java
@SubscribeEvent
public void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
    var models = event.getModels();
    
    for (var entry : COMPLIANCES.entrySet()) {
        var smallModelRL = entry.getKey();
        var smallBakedModel = models.get(smallModelRL);
        
        if (smallBakedModel != null) {
            var largeModelRL = entry.getValue();
            var largeBakedModel = models.get(largeModelRL);
            
            if (largeBakedModel != null) {
                var epicModel = new EpicBakedModel(smallBakedModel, largeBakedModel);
                models.put(smallModelRL, epicModel);
            }
        }
    }
}
```

**1.21.10:**
```java
@SubscribeEvent
public void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
    var bakingResult = event.getBakingResult();
    var itemModels = bakingResult.itemStackModels();        // Map<ResourceLocation, ItemModel>
    var standaloneModels = bakingResult.standaloneModels(); // Map<StandaloneModelKey<?>, Object>
    
    for (var entry : LARGE_MODEL_KEYS.entrySet()) {
        var smallRL = entry.getKey();
        var key = entry.getValue();

        var smallModel = itemModels.get(smallRL);
        var largeModel = standaloneModels.get(key);

        if (smallModel != null && largeModel != null) {
            itemModels.put(smallRL, new EpicBakedModel(smallModel, (ItemModel) largeModel));
        }
    }
}
```

### API Differences

| Aspect | 1.21.1 | 1.21.10 |
|--------|--------|---------|
| Getting models | `event.getModels()` | `event.getBakingResult().itemStackModels()` |
| Model map type | `Map<ModelResourceLocation, BakedModel>` | `Map<ResourceLocation, ItemModel>` |
| Key type | `ModelResourceLocation` | `ResourceLocation` (for items) |
| Large model access | Direct from models map | From `standaloneModels()` |

---

## Migration Guide

### Step-by-Step Migration

#### 1. Update FWWeaponBasicSwordItem.java

```java
// Change import
- import net.minecraft.world.item.SwordItem;
- import net.minecraft.world.item.Tier;
+ import net.minecraft.world.item.Item;
+ import net.minecraft.world.item.ToolMaterial;

// Change class declaration
- public class FWWeaponBasicSwordItem extends SwordItem {
+ public class FWWeaponBasicSwordItem extends Item {

// Change constructor
- public FWWeaponBasicSwordItem(Tier tier, float damage, float speed, Item.Properties properties) {
-     super(tier, properties.attributes(SwordItem.createAttributes(tier, (int) damage, speed)));
+ public FWWeaponBasicSwordItem(ToolMaterial material, float damage, float speed, Item.Properties properties) {
+     super(material.applySwordProperties(properties, damage, speed));
      this.attackDamage = damage;
  }
```

#### 2. Update FWItems.java

```java
// Change import
- import net.minecraft.world.item.Tiers;
+ import net.minecraft.world.item.ToolMaterial;

// Change item creation
- new FWWeaponBasicSwordItem(Tiers.NETHERITE, config.damage, config.speed, new Item.Properties())
+ new FWWeaponBasicSwordItem(ToolMaterial.NETHERITE, config.damage, config.speed, new Item.Properties())
```

#### 3. Replace EpicBakedModel.java

Replace the entire file with the 1.21.10 version (see above).

#### 4. Update FantasyWeaponsClientNeoForge.java

Replace the model registration logic with the 1.21.10 version (see above).

---

## Quick Reference Tables

### Import Statements

| Purpose | 1.21.1 | 1.21.10 |
|---------|--------|---------|
| Weapon base | `SwordItem` | `Item` |
| Material | `Tier`, `Tiers` | `ToolMaterial` |
| Model | `BakedModel` | `ItemModel` |
| Model location | `ModelResourceLocation` | `ResourceLocation` |
| Model registration | - | `StandaloneModelKey`, `SimpleUnbakedStandaloneModel` |
| Render state | - | `ItemStackRenderState` |

### Method Signatures

| Action | 1.21.1 | 1.21.10 |
|--------|--------|---------|
| Create attributes | `SwordItem.createAttributes(tier, damage, speed)` | `material.applySwordProperties(props, damage, speed)` |
| Apply transform | `applyTransform(context, pose, leftHand)` | `update(state, stack, resolver, context, level, owner, seed)` |
| Get models | `event.getModels()` | `event.getBakingResult().itemStackModels()` |
| Register model | `event.register(modelRL)` | `event.register(key, unbaked)` |

### Event Names

| Purpose | 1.21.1 | 1.21.10 |
|---------|--------|---------|
| Register extra models | `ModelEvent.RegisterAdditional` | `ModelEvent.RegisterStandalone` |
| Modify baked models | `ModelEvent.ModifyBakingResult` | `ModelEvent.ModifyBakingResult` (same name, different API) |

---

The migration requires significant model-related rewrites, but the new `ItemModel` API is much simpler (1 method vs 10+).
