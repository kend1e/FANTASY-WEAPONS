package net.kenddie.fantasyweapons.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.kenddie.fantasyweapons.shared.ModConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public final class FWCreativeModTabs {
    public static void register() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, new ResourceLocation(ModConstants.MOD_ID, ".fw_tab"),
                FWCreativeTabContents.configure(FabricItemGroup.builder()).build());
    }

    private FWCreativeModTabs() {}
}
