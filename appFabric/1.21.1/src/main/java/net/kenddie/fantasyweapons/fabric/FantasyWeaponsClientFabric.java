package net.kenddie.fantasyweapons.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.kenddie.fantasyweapons.FantasyWeapons;
import net.kenddie.fantasyweapons.fabric.model.EpicBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

import net.kenddie.fantasyweapons.item.FWItems;

public class FantasyWeaponsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(pluginContext -> {
            Map<ModelResourceLocation, ResourceLocation> compliances = new HashMap<>();

            for (var item : FWItems.getAllItems()) {
                var itemName = item.getDescriptionId().substring(("item." + FantasyWeapons.MOD_ID + ".").length());
                var largeJsonPath = String.format(("/assets/" + FantasyWeapons.MOD_ID + "/models/item/%s_large.json"), itemName);

                try (var inputStream = FantasyWeapons.class.getResourceAsStream(largeJsonPath)) {
                    if (inputStream != null) {
                        var smallResourceLocation = ResourceLocation.fromNamespaceAndPath(FantasyWeapons.MOD_ID, itemName);
                        var largeResourceLocation = ResourceLocation.fromNamespaceAndPath(FantasyWeapons.MOD_ID, "item/" + itemName + "_large");

                        var smallModelResourceLocation = new ModelResourceLocation(smallResourceLocation, "inventory");

                        compliances.put(smallModelResourceLocation, largeResourceLocation);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            pluginContext.addModels(compliances.values());

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
