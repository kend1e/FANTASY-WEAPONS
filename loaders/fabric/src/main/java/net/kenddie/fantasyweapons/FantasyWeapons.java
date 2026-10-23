package net.kenddie.fantasyweapons;

import net.fabricmc.api.ModInitializer;
import net.kenddie.fantasyweapons.config.FWConfig;
import net.kenddie.fantasyweapons.item.FWCreativeModTabs;
import net.kenddie.fantasyweapons.item.FWItemRegistration;
import net.kenddie.fantasyweapons.shared.ModConstants;

public class FantasyWeapons implements ModInitializer {
    public static final String MOD_ID = ModConstants.MOD_ID;

    @Override
    public void onInitialize() {
        FWConfig.register();
        FWItemRegistration.register();
        FWCreativeModTabs.register();
    }
}
