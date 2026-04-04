package net.kenddie.fantasyweapons.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.SimpleUnbakedExtraModel;
import net.kenddie.fantasyweapons.FantasyWeapons;
import net.kenddie.fantasyweapons.fabric.model.EpicBakedModel;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.kenddie.fantasyweapons.item.FWItems;

public class FantasyWeaponsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(pluginContext -> {
            Map<ResourceLocation, ResourceLocation> itemToLargeModel = new HashMap<>();

            for (var item : FWItems.getAllItems()) {
                var itemName = item.getDescriptionId().substring(("item." + FantasyWeapons.MOD_ID + ".").length());
                var largeJsonPath = String.format(("/assets/" + FantasyWeapons.MOD_ID + "/models/item/%s_large.json"), itemName);

                try (var inputStream = FantasyWeapons.class.getResourceAsStream(largeJsonPath)) {
                    if (inputStream != null) {
                        var smallResourceLocation = ResourceLocation.fromNamespaceAndPath(FantasyWeapons.MOD_ID, itemName);
                        var largeResourceLocation = ResourceLocation.fromNamespaceAndPath(FantasyWeapons.MOD_ID, "item/" + itemName + "_large");
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
