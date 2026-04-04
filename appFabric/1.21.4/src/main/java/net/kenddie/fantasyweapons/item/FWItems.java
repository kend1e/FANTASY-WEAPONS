package net.kenddie.fantasyweapons.item;

import net.kenddie.fantasyweapons.FantasyWeapons;
import net.kenddie.fantasyweapons.config.FWConfig;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class FWItems {

    private static final List<Item> ALL_ITEMS = new ArrayList<>();

    public static void register() {
        for (String name : FantasyWeapons.BASIC_ITEM_NAMES) {
            ALL_ITEMS.add(registerItem(name, Item::new));
        }
        for (String name : FantasyWeapons.WEAPON_NAMES) {
            var config = FWConfig.FW_CONFIG.getSwordConfigOrDefault(name);
            ALL_ITEMS.add(registerItem(name, properties -> new FWWeaponBasicSwordItem(
                    ToolMaterial.NETHERITE, config.damage, config.speed, properties)));
        }
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
                ResourceLocation.fromNamespaceAndPath(FantasyWeapons.MOD_ID, "fw_tab"),
                FabricItemGroup.builder()
                        .title(Component.translatable("itemGroup." + FantasyWeapons.MOD_ID + ".fw_tab"))
                        .icon(() -> new ItemStack(ALL_ITEMS.get(1)))
                        .displayItems((ctx, entries) -> ALL_ITEMS.forEach(item -> entries.accept(new ItemStack(item))))
                        .build());
    }

    private static Item registerItem(String name, Function<Item.Properties, Item> factory) {
        var key = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FantasyWeapons.MOD_ID, name));
        return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(new Item.Properties().setId(key)));
    }

    public static List<Item> getAllItems() {
        return ALL_ITEMS;
    }

    private FWItems() {
    }
}
