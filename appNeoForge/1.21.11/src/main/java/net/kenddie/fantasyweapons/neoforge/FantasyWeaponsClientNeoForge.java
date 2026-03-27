package net.kenddie.fantasyweapons.neoforge;

import net.kenddie.fantasyweapons.FantasyWeapons;
import net.kenddie.fantasyweapons.neoforge.model.EpicBakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.kenddie.fantasyweapons.item.FWItems;

public class FantasyWeaponsClientNeoForge {
    private static final Map<Identifier, StandaloneModelKey<ItemModel>> LARGE_MODEL_KEYS = new HashMap<>();
    private static final Method DETECT_RENDER_TYPE;

    static {
        try {
            DETECT_RENDER_TYPE = BlockModelWrapper.class.getDeclaredMethod("detectRenderType", List.class);
            DETECT_RENDER_TYPE.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to find BlockModelWrapper.detectRenderType", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Function<ItemStack, RenderType> detectRenderType(List<BakedQuad> quads) {
        try {
            return (Function<ItemStack, RenderType>) DETECT_RENDER_TYPE.invoke(null, quads);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke BlockModelWrapper.detectRenderType", e);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRegisterStandalone(ModelEvent.RegisterStandalone event) {
        for (var item : FWItems.getAllItems()) {
            var itemName = item.getDescriptionId().substring(("item." + FantasyWeapons.MOD_ID + ".").length());
            var largeJsonPath = String.format("/assets/%s/models/item/%s_large.json", FantasyWeapons.MOD_ID, itemName);

            try (var inputStream = FantasyWeapons.class.getResourceAsStream(largeJsonPath)) {
                if (inputStream != null) {
                    var smallRL = Identifier.fromNamespaceAndPath(FantasyWeapons.MOD_ID, itemName);
                    var largeModelLoc = Identifier.fromNamespaceAndPath(FantasyWeapons.MOD_ID, "item/" + itemName + "_large");

                    var key = new StandaloneModelKey<ItemModel>(() -> "fantasy_weapons_large_" + itemName);
                    var unbaked = new SimpleUnbakedStandaloneModel<>(largeModelLoc, (resolved, baker) -> {
                        var textureSlots = resolved.getTopTextureSlots();
                        var quads = resolved.bakeTopGeometry(textureSlots, baker, BlockModelRotation.IDENTITY);
                        var renderProps = ModelRenderProperties.fromResolvedModel(baker, resolved, textureSlots);
                        var allQuads = quads.getAll();
                        return (ItemModel) new BlockModelWrapper(List.of(), allQuads, renderProps,
                                detectRenderType(allQuads));
                    });

                    event.register(key, unbaked);
                    LARGE_MODEL_KEYS.put(smallRL, key);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        var itemModels = event.getBakingResult().itemStackModels();
        var standaloneModels = event.getBakingResult().standaloneModels();
        for (var entry : LARGE_MODEL_KEYS.entrySet()) {
            var smallRL = entry.getKey();
            var key = entry.getValue();

            var smallModel = itemModels.get(smallRL);
            var largeModel = standaloneModels.get(key);

            if (smallModel != null && largeModel != null) {
                itemModels.put(smallRL, new EpicBakedModel(smallModel, largeModel));
            }
        }
    }
}
