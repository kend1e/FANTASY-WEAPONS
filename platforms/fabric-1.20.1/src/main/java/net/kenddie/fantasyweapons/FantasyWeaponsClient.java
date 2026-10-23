package net.kenddie.fantasyweapons;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.kenddie.fantasyweapons.model.EpicBakedModel;
import net.kenddie.fantasyweapons.model.WeaponModels;

public class FantasyWeaponsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(context -> {
            var pairs = WeaponModels.pairs();
            context.addModels(pairs.values());
            context.modifyModelAfterBake().register((small, bake) -> {
                if (small == null) return null;
                var largeId = pairs.get(bake.id());
                if (largeId == null) return small;
                var large = bake.baker().bake(largeId, bake.settings());
                return large == null ? small : new EpicBakedModel(small, large);
            });
        });
    }
}
