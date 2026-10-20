package net.kenddie.fantasyweapons.item;

import net.kenddie.fantasyweapons.config.FWConfig;
import net.kenddie.fantasyweapons.item.weapon.lib.FWWeaponBasicSwordItem;
import net.kenddie.fantasyweapons.shared.ModConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.kenddie.fantasyweapons.GameVersion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;

public final class FWItems {
    public static Item material() { return new Item(new Item.Properties()); }

    public static Item weapon(String id) {
        return new FWWeaponBasicSwordItem(Tiers.NETHERITE, () -> FWConfig.weapon(id), new Item.Properties());
    }

    public static Item get(String id) {
        return BuiltInRegistries.ITEM.get(GameVersion.id(ModConstants.MOD_ID, id));
    }

    private FWItems() {}
}
