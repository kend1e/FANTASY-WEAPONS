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
            context.addModels(pairs.values().stream().map(id -> id.id().withPrefix("item/")).toList());
            context.modifyModelAfterBake().register((small, bake) -> {
                if (small == null) return null;
                if (bake.topLevelId() == null || small == null) return small;
                var largeId = pairs.get(bake.topLevelId());
                if (largeId == null) return small;
                var large = bake.baker().bake(largeId.id().withPrefix("item/"), bake.settings());
                return large == null ? small : new EpicBakedModel(small, large);
            });
        });
    }
}
