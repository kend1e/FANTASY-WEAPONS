package net.kenddie.fantasyweapons.item;

import net.kenddie.fantasyweapons.GameVersion;
import net.kenddie.fantasyweapons.shared.ModConstants;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.CreativeModeTabEvent;

public final class FWCreativeModTabs {
    public static void register(IEventBus bus) {
        bus.addListener((CreativeModeTabEvent.Register event) -> event.registerCreativeModeTab(
                GameVersion.id(ModConstants.MOD_ID, "fw_tab"), FWCreativeTabContents::configure));
    }
    private FWCreativeModTabs() {}
}
