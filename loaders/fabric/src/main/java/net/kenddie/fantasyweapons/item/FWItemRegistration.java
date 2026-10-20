package net.kenddie.fantasyweapons.item;

import net.kenddie.fantasyweapons.shared.ModConstants;
import net.kenddie.fantasyweapons.shared.content.WeaponCatalog;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public final class FWItemRegistration {
    public static void register() {
        var catalog = WeaponCatalog.INSTANCE;
        catalog.materials().forEach(id -> Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(ModConstants.MOD_ID, id), FWItems.material()));
        catalog.weapons().forEach(weapon -> Registry.register(BuiltInRegistries.ITEM,
                new ResourceLocation(ModConstants.MOD_ID, weapon.id()), FWItems.weapon(weapon.id())));
    }

    private FWItemRegistration() {}
}
