package net.kenddie.fantasyweapons.item;

import net.kenddie.fantasyweapons.shared.ModConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FWCreativeModTabs {
    public static void register(IEventBus bus) {
        DeferredRegister<CreativeModeTab> tabs = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModConstants.MOD_ID);
        tabs.register("fw_tab", () -> FWCreativeTabContents.configure(CreativeModeTab.builder()).build());
        tabs.register(bus);
    }

    private FWCreativeModTabs() {}
}
