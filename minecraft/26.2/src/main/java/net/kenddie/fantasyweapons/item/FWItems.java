package net.kenddie.fantasyweapons.item;

import net.kenddie.fantasyweapons.config.FWConfig;
import net.kenddie.fantasyweapons.item.weapon.lib.FWWeaponBasicSwordItem;
import net.kenddie.fantasyweapons.shared.ModConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.kenddie.fantasyweapons.GameVersion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public final class FWItems {
    public static Item material(Item.Properties properties) { return new Item(properties); }

    public static Item weapon(String id, Item.Properties properties) {
        return new FWWeaponBasicSwordItem(ToolMaterial.NETHERITE, () -> FWConfig.weapon(id), properties);
    }

    public static Item get(String id) {
        return BuiltInRegistries.ITEM.getValue(GameVersion.id(id.contains(":") ? id : ModConstants.MOD_ID + ":" + id));
    }

    private FWItems() {}
}
