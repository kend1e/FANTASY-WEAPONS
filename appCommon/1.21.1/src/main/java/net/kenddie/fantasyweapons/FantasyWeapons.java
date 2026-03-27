package net.kenddie.fantasyweapons;

import net.kenddie.fantasyweapons.item.FWCreativeModTabs;
import net.kenddie.fantasyweapons.item.FWItems;

import static net.kenddie.fantasyweapons.item.FWItemManager.FW_ITEM_MANAGER;

public class FantasyWeapons {
    public static final String MOD_ID = "fantasy_weapons";

    public static void init() {
        FWItems.declare();
        FW_ITEM_MANAGER.register();
        FWCreativeModTabs.register();
    }
}
