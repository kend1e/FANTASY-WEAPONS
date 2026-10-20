package net.kenddie.fantasyweapons.item;

import net.kenddie.fantasyweapons.shared.ModConstants;
import net.kenddie.fantasyweapons.shared.content.WeaponCatalog;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class FWItemRegistration {
    public static void register(IEventBus bus) {
        DeferredRegister<Item> items = DeferredRegister.create(ForgeRegistries.ITEMS, ModConstants.MOD_ID);
        var catalog = WeaponCatalog.INSTANCE;
        catalog.materials().forEach(id -> items.register(id, FWItems::material));
        catalog.weapons().forEach(weapon -> items.register(weapon.id(), () -> FWItems.weapon(weapon.id())));
        items.register(bus);
    }

    private FWItemRegistration() {}
}
