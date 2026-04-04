package net.kenddie.fantasyweapons.fabric;

import net.fabricmc.api.ModInitializer;
import net.kenddie.fantasyweapons.item.FWItems;

public class FantasyWeaponsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FWItems.register();
    }
}
