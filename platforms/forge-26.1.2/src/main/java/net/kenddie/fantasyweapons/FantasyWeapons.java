package net.kenddie.fantasyweapons;

import net.kenddie.fantasyweapons.config.FWConfig;
import net.kenddie.fantasyweapons.item.FWCreativeModTabs;
import net.kenddie.fantasyweapons.item.FWItemRegistration;
import net.kenddie.fantasyweapons.shared.ModConstants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FantasyWeapons.MOD_ID)
public class FantasyWeapons {
    public static final String MOD_ID = ModConstants.MOD_ID;

    public FantasyWeapons(FMLJavaModLoadingContext context) {
        var bus = context.getModBusGroup();
        context.registerConfig(ModConfig.Type.SERVER, FWConfig.SPEC, MOD_ID + "-server.toml");
        FWItemRegistration.register(bus);
        FWCreativeModTabs.register(bus);
    }
}
