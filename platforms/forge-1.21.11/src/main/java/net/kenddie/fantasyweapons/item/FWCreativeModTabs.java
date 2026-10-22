package net.kenddie.fantasyweapons.item;

import net.kenddie.fantasyweapons.shared.ModConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;

public final class FWCreativeModTabs {
    public static void register(BusGroup bus) {
        DeferredRegister<CreativeModeTab> tabs = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModConstants.MOD_ID);
        tabs.register("fw_tab", () -> FWCreativeTabContents.configure(CreativeModeTab.builder()).build());
        tabs.register(bus);
    }

    private FWCreativeModTabs() {}
}
