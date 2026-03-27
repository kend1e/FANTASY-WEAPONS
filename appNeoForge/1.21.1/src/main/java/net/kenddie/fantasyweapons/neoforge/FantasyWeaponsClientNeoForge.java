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
    private static final Map<ModelResourceLocation, ModelResourceLocation> COMPLIANCES = new HashMap<>();

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        for (var item : FWItems.getAllItems()) {
            var itemName = item.getDescriptionId().substring(("item." + FantasyWeapons.MOD_ID + ".").length());
            var largeJsonPath = String.format("/assets/%s/models/item/%s_large.json", FantasyWeapons.MOD_ID, itemName);

            try (var inputStream = FantasyWeapons.class.getResourceAsStream(largeJsonPath)) {
                if (inputStream != null) {
                    var smallResourceLocation = ResourceLocation.fromNamespaceAndPath(FantasyWeapons.MOD_ID, itemName);
                    var largeResourceLocation = ResourceLocation.fromNamespaceAndPath(FantasyWeapons.MOD_ID, itemName + "_large");

                    var smallModelResourceLocation = new ModelResourceLocation(smallResourceLocation, "inventory");
                    var largeModelResourceLocation = new ModelResourceLocation(largeResourceLocation, "inventory");

                    COMPLIANCES.put(smallModelResourceLocation, largeModelResourceLocation);

                    event.register(largeModelResourceLocation);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        var models = event.getModels();
        for (var compliance : COMPLIANCES.entrySet()) {
            var smallModelResourceLocation = compliance.getKey();
            var smallBakedModel = models.get(smallModelResourceLocation);
            if (smallBakedModel != null) {
                var largeModelResourceLocation = compliance.getValue();
                var largeBakedModel = models.get(largeModelResourceLocation);
                if (largeBakedModel != null) {
                    var epicBakedModel = new EpicBakedModel(smallBakedModel, largeBakedModel);
                    models.put(smallModelResourceLocation, epicBakedModel);
                }
            }
        }
    }
}
