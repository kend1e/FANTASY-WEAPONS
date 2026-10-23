package net.kenddie.fantasyweapons.item;

import net.kenddie.fantasyweapons.shared.ModConstants;
import net.kenddie.fantasyweapons.shared.content.WeaponCatalog;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FWItemRegistration {
    public static void register(IEventBus bus) {
        DeferredRegister.Items items = DeferredRegister.createItems(ModConstants.MOD_ID);
        var catalog = WeaponCatalog.INSTANCE;
        catalog.materials().forEach(id -> items.registerItem(id, FWItems::material));
        catalog.weapons().forEach(weapon -> items.registerItem(weapon.id(), props -> FWItems.weapon(weapon.id(), props)));
        items.register(bus);
    }

    private FWItemRegistration() {}
}
