package net.kenddie.fantasyweapons;

import net.fabricmc.api.ModInitializer;
import net.kenddie.fantasyweapons.item.FWCreativeModTabs;
import net.kenddie.fantasyweapons.item.FWItems;
import net.kenddie.fantasyweapons.config.FWConfig;

import static net.kenddie.fantasyweapons.item.FWItemManager.FW_ITEM_MANAGER;


public class FantasyWeapons implements ModInitializer {
    public static final String MOD_ID = "fantasy_weapons";

    @Override
    public void onInitialize() {
        FWItems.declare();
        FWConfig.register();
        FW_ITEM_MANAGER.register();
        FWCreativeModTabs.register();
    }
}
