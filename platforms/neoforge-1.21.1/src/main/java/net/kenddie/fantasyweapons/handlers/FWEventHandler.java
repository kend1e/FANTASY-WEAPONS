package net.kenddie.fantasyweapons.handlers;

import net.kenddie.fantasyweapons.model.EpicBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.kenddie.fantasyweapons.model.WeaponModels;
import net.kenddie.fantasyweapons.shared.ModConstants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = ModConstants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class FWEventHandler {
    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        WeaponModels.pairs().values().forEach(id -> event.register(standalone(id)));
    }

    @SubscribeEvent
    public static void bakeModels(ModelEvent.ModifyBakingResult event) {
        var models = event.getModels();
        WeaponModels.pairs().forEach((smallId, largeId) -> {
            var small = models.get(smallId);
            var large = models.get(standalone(largeId));
            if (small != null && large != null) models.put(smallId, new EpicBakedModel(small, large));
        });
    }

    private static ModelResourceLocation standalone(ModelResourceLocation model) {
        return new ModelResourceLocation(model.id().withPrefix("item/"), ModelResourceLocation.STANDALONE_VARIANT);
    }

    private FWEventHandler() {}
}
