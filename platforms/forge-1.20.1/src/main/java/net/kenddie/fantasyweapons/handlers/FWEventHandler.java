package net.kenddie.fantasyweapons.handlers;

import net.kenddie.fantasyweapons.model.EpicBakedModel;
import net.kenddie.fantasyweapons.model.ForgeModelLocations;
import net.kenddie.fantasyweapons.model.WeaponModels;
import net.kenddie.fantasyweapons.shared.ModConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class FWEventHandler {
    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        WeaponModels.pairs().values().forEach(id -> event.register(ForgeModelLocations.additional(id)));
    }

    @SubscribeEvent
    public static void bakeModels(ModelEvent.ModifyBakingResult event) {
        var models = event.getModels();
        WeaponModels.pairs().forEach((smallId, largeId) -> {
            var small = models.get(smallId);
            var large = models.get(ForgeModelLocations.additional(largeId));
            if (small != null && large != null) models.put(smallId, new EpicBakedModel(small, large));
        });
    }

    private FWEventHandler() {}
}
