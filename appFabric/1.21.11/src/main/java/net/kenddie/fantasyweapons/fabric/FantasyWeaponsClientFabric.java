package net.kenddie.fantasyweapons.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.SimpleUnbakedExtraModel;
import net.kenddie.fantasyweapons.FantasyWeapons;
import net.kenddie.fantasyweapons.fabric.model.EpicBakedModel;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.kenddie.fantasyweapons.item.FWItems;

public class FantasyWeaponsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(pluginContext -> {
            Map<Identifier, Identifier> itemToLargeModel = new HashMap<>();

            for (var item : FWItems.getAllItems()) {
                var itemName = item.getDescriptionId().substring(("item." + FantasyWeapons.MOD_ID + ".").length());
                var largeJsonPath = String.format(("/assets/" + FantasyWeapons.MOD_ID + "/models/item/%s_large.json"), itemName);

                try (var inputStream = FantasyWeapons.class.getResourceAsStream(largeJsonPath)) {
                    if (inputStream != null) {
                        var smallResourceLocation = Identifier.fromNamespaceAndPath(FantasyWeapons.MOD_ID, itemName);
                        var largeResourceLocation = Identifier.fromNamespaceAndPath(FantasyWeapons.MOD_ID, "item/" + itemName + "_large");
                        itemToLargeModel.put(smallResourceLocation, largeResourceLocation);
                        pluginContext.addModel(
                                ExtraModelKey.create(largeResourceLocation::toString),
                                SimpleUnbakedExtraModel.blockStateModel(largeResourceLocation)
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            pluginContext.modifyItemModelAfterBake().register((itemModel, bakeContext) -> {
                var itemId = bakeContext.itemId();
                var largeModelId = itemToLargeModel.get(itemId);
                if (largeModelId != null) {
                    var largeUnbaked = new BlockModelWrapper.Unbaked(largeModelId, List.of());
                    ItemModel largeItemModel = largeUnbaked.bake(bakeContext.bakeContext());
                    return new EpicBakedModel(itemModel, largeItemModel);
                }
                return itemModel;
            });
        });
    }
}
