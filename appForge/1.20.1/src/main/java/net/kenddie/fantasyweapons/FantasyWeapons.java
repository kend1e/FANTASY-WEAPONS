package net.kenddie.fantasyweapons;

import net.kenddie.fantasyweapons.handlers.FWEventHandler;
import net.kenddie.fantasyweapons.item.FWCreativeModTabs;
import net.kenddie.fantasyweapons.item.FWItems;
import net.kenddie.fantasyweapons.config.FWConfig;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static net.kenddie.fantasyweapons.item.FWItemManager.FW_ITEM_MANAGER;

@Mod(FantasyWeapons.MOD_ID)
public class FantasyWeapons {
    public static final String MOD_ID = "fantasy_weapons";

    public FantasyWeapons() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        var modEventHandler = new FWEventHandler();
        modEventBus.register(modEventHandler);
        FWItems.declare();
        FWConfig.register(modEventBus, FW_ITEM_MANAGER.getDeclaredSwords().stream().map(id -> id.getPath()).toList());
        FW_ITEM_MANAGER.register(modEventBus);
        FWCreativeModTabs.register(modEventBus);
    }
}
