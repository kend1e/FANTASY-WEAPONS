package net.kenddie.fantasyweapons.item;

import net.minecraft.world.item.Item;

import net.kenddie.fantasyweapons.shared.ModConstants;
import net.kenddie.fantasyweapons.shared.content.WeaponCatalog;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.kenddie.fantasyweapons.GameVersion;

public final class FWItemRegistration {
    public static void register() {
        var catalog = WeaponCatalog.INSTANCE;
        catalog.materials().forEach(id -> Registry.register(BuiltInRegistries.ITEM, GameVersion.id(ModConstants.MOD_ID, id), FWItems.material(new Item.Properties())));
        catalog.weapons().forEach(weapon -> Registry.register(BuiltInRegistries.ITEM,
                GameVersion.id(ModConstants.MOD_ID, weapon.id()), FWItems.weapon(weapon.id(), new Item.Properties())));
    }

    private FWItemRegistration() {}
}
