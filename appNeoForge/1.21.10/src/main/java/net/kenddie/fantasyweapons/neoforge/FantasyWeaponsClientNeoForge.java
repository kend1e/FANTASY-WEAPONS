package net.kenddie.fantasyweapons.neoforge;

import net.kenddie.fantasyweapons.FantasyWeapons;
import net.kenddie.fantasyweapons.neoforge.model.EpicBakedModel;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.kenddie.fantasyweapons.item.FWItems;

public class FantasyWeaponsClientNeoForge {
    private static final Map<ResourceLocation, StandaloneModelKey<ItemModel>> LARGE_MODEL_KEYS = new HashMap<>();

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRegisterStandalone(ModelEvent.RegisterStandalone event) {
        for (var item : FWItems.getAllItems()) {
            var itemName = item.getDescriptionId().substring(("item." + FantasyWeapons.MOD_ID + ".").length());
            var largeJsonPath = String.format("/assets/%s/models/item/%s_large.json", FantasyWeapons.MOD_ID, itemName);

            try (var inputStream = FantasyWeapons.class.getResourceAsStream(largeJsonPath)) {
                if (inputStream != null) {
                    var smallRL = ResourceLocation.fromNamespaceAndPath(FantasyWeapons.MOD_ID, itemName);
                    var largeModelLoc = ResourceLocation.fromNamespaceAndPath(FantasyWeapons.MOD_ID, "item/" + itemName + "_large");

                    var key = new StandaloneModelKey<ItemModel>(() -> "fantasy_weapons_large_" + itemName);
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
