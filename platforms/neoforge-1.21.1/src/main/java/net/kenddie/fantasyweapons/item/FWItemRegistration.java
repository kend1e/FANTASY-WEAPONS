package net.kenddie.fantasyweapons.item;

import net.kenddie.fantasyweapons.shared.ModConstants;
import net.kenddie.fantasyweapons.shared.content.WeaponCatalog;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

public final class FWItemRegistration {
    public static void register(IEventBus bus) {
        DeferredRegister<Item> items = DeferredRegister.create(Registries.ITEM, ModConstants.MOD_ID);
        var catalog = WeaponCatalog.INSTANCE;
        catalog.materials().forEach(id -> items.register(id, () -> FWItems.material(new Item.Properties())));
        catalog.weapons().forEach(weapon -> items.register(weapon.id(), () -> FWItems.weapon(weapon.id(), new Item.Properties())));
        items.register(bus);
    }

    private FWItemRegistration() {}
}
