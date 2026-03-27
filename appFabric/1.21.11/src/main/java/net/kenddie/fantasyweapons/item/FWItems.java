package net.kenddie.fantasyweapons.item;

import net.kenddie.fantasyweapons.FantasyWeapons;
import net.kenddie.fantasyweapons.config.FWConfig;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;

import java.util.ArrayList;
import java.util.List;

public final class FWItems {

    private static final List<Item> ALL_ITEMS = new ArrayList<>();

    public static void register() {
        for (String name : FantasyWeapons.BASIC_ITEM_NAMES) {
            ALL_ITEMS.add(registerItem(name, new Item(new Item.Properties())));
        }
        for (String name : FantasyWeapons.WEAPON_NAMES) {
            var config = FWConfig.FW_CONFIG.getSwordConfigOrDefault(name);
            ALL_ITEMS.add(registerItem(name, new FWWeaponBasicSwordItem(
                    ToolMaterial.NETHERITE, config.damage, config.speed, new Item.Properties())));
        }
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
                Identifier.fromNamespaceAndPath(FantasyWeapons.MOD_ID, "fw_tab"),
                FabricItemGroup.builder()
                        .title(Component.translatable("itemGroup." + FantasyWeapons.MOD_ID + ".fw_tab"))
                        .icon(() -> new ItemStack(ALL_ITEMS.get(1)))
                        .displayItems((ctx, entries) -> ALL_ITEMS.forEach(item -> entries.accept(new ItemStack(item))))
                        .build());
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM,
                Identifier.fromNamespaceAndPath(FantasyWeapons.MOD_ID, name), item);
    }

    public static List<Item> getAllItems() {
        return ALL_ITEMS;
    }

    private FWItems() {
    }
}
