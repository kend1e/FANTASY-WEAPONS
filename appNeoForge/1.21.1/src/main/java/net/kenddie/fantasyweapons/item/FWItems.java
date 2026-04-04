package net.kenddie.fantasyweapons.item;

import net.kenddie.fantasyweapons.FantasyWeapons;
import net.kenddie.fantasyweapons.config.FWConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public final class FWItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, FantasyWeapons.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FantasyWeapons.MOD_ID);
    private static final List<DeferredHolder<Item, ? extends Item>> ALL_ITEMS = new ArrayList<>();

    static {
        for (String name : FantasyWeapons.BASIC_ITEM_NAMES) {
            ALL_ITEMS.add(ITEMS.register(name, () -> new Item(new Item.Properties())));
        }
        for (String name : FantasyWeapons.WEAPON_NAMES) {
            ALL_ITEMS.add(ITEMS.register(name, () -> {
                var config = FWConfig.FW_CONFIG.getSwordConfigOrDefault(name);
                return new FWWeaponBasicSwordItem(Tiers.NETHERITE, config.damage, config.speed, new Item.Properties());
            }));
        }
        TABS.register("fw_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + FantasyWeapons.MOD_ID + ".fw_tab"))
                .icon(() -> new ItemStack(ALL_ITEMS.get(1).get()))
                .displayItems((ctx, entries) -> ALL_ITEMS.forEach(h -> entries.accept(new ItemStack(h.get()))))
                .build());
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        TABS.register(bus);
    }

    public static List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        ALL_ITEMS.forEach(h -> items.add(h.get()));
        return items;
    }

    private FWItems() {
    }
}
