package net.kenddie.fantasyweapons;

import net.kenddie.fantasyweapons.config.FWConfig;
import net.kenddie.fantasyweapons.item.FWCreativeModTabs;
import net.kenddie.fantasyweapons.item.FWItemRegistration;
import net.kenddie.fantasyweapons.shared.ModConstants;
import net.kenddie.fantasyweapons.shared.content.WeaponCatalog;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(ModConstants.MOD_ID)
public class FantasyWeapons {
    public FantasyWeapons(IEventBus bus, ModContainer container) {
        FWConfig.register(bus, container, WeaponCatalog.INSTANCE.weaponIds());
        FWItemRegistration.register(bus);
        FWCreativeModTabs.register(bus);
    }
}
