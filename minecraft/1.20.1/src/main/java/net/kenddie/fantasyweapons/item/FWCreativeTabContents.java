package net.kenddie.fantasyweapons.item;

import net.kenddie.fantasyweapons.shared.ModConstants;
import net.kenddie.fantasyweapons.shared.content.WeaponCatalog;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class FWCreativeTabContents {
    public static CreativeModeTab.Builder configure(CreativeModeTab.Builder builder) {
        var catalog = WeaponCatalog.INSTANCE;
        return builder.icon(() -> new ItemStack(FWItems.get(catalog.tabIcon())))
                .title(Component.translatable(ModConstants.CREATIVE_TAB_TITLE))
                .displayItems((parameters, output) -> catalog.itemIds().forEach(id -> output.accept(FWItems.get(id))));
    }

    private FWCreativeTabContents() {}
}
