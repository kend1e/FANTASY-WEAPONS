package net.kenddie.fantasyweapons.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import static net.kenddie.fantasyweapons.item.FWItemManager.FW_ITEM_MANAGER;

public record FWDeclaredItem(ResourceLocation id) {

    public Item get() {
        return FW_ITEM_MANAGER.getRegistered(id);
    }
}
