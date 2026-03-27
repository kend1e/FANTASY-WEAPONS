package net.kenddie.fantasyweapons.neoforge;

import net.kenddie.fantasyweapons.FantasyWeapons;
import net.kenddie.fantasyweapons.neoforge.model.EpicBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.HashMap;
import java.util.Map;

import net.kenddie.fantasyweapons.item.FWItems;

public class FantasyWeaponsClientNeoForge {
    private static final Map<ResourceLocation, ResourceLocation> COMPLIANCES = new HashMap<>();

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        for (var item : FWItems.getAllItems()) {
            var itemName = item.getDescriptionId().substring(("item." + FantasyWeapons.MOD_ID + ".").length());
            var largeJsonPath = String.format("/assets/%s/models/item/%s_large.json", FantasyWeapons.MOD_ID, itemName);

            try (var inputStream = FantasyWeapons.class.getResourceAsStream(largeJsonPath)) {
                if (inputStream != null) {
                    var smallResourceLocation = ResourceLocation.fromNamespaceAndPath(FantasyWeapons.MOD_ID, itemName);
                    var largeResourceLocation = ResourceLocation.fromNamespaceAndPath(FantasyWeapons.MOD_ID, "item/" + itemName + "_large");

                    COMPLIANCES.put(smallResourceLocation, largeResourceLocation);

                    event.register(largeResourceLocation);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        var bakingResult = event.getBakingResult();
        var blockStateModels = bakingResult.blockStateModels();
        var standaloneModels = bakingResult.standaloneModels();
        for (var compliance : COMPLIANCES.entrySet()) {
            var smallResourceLocation = compliance.getKey();
            var largeResourceLocation = compliance.getValue();

            var smallModelResourceLocation = new ModelResourceLocation(smallResourceLocation, "inventory");
            var smallBakedModel = blockStateModels.get(smallModelResourceLocation);
            var largeBakedModel = standaloneModels.get(largeResourceLocation);

            if (smallBakedModel != null && largeBakedModel != null) {
                var epicBakedModel = new EpicBakedModel(smallBakedModel, largeBakedModel);
                blockStateModels.put(smallModelResourceLocation, epicBakedModel);
            }
        }
    }
}
