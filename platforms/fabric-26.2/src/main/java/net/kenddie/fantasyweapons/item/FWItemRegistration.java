package net.kenddie.fantasyweapons.item;

import net.kenddie.fantasyweapons.GameVersion;
import net.kenddie.fantasyweapons.shared.ModConstants;
import net.kenddie.fantasyweapons.shared.content.WeaponCatalog;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import java.util.function.Function;

public final class FWItemRegistration {
    public static void register() {
        var catalog = WeaponCatalog.INSTANCE;
        catalog.materials().forEach(id -> register(id, FWItems::material));
        catalog.weapons().forEach(weapon -> register(weapon.id(), props -> FWItems.weapon(weapon.id(), props)));
    }
    private static void register(String id, Function<Item.Properties, Item> factory) {
        var key = ResourceKey.create(BuiltInRegistries.ITEM.key(), GameVersion.id(ModConstants.MOD_ID, id));
        Registry.register(BuiltInRegistries.ITEM, key, factory.apply(new Item.Properties().setId(key)));
    }
    private FWItemRegistration() {}
}
