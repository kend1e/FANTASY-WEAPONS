package net.kenddie.fantasyweapons.item;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.kenddie.fantasyweapons.FantasyWeapons;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import static net.kenddie.fantasyweapons.item.FWItemManager.FW_ITEM_MANAGER;

@SuppressWarnings("WeakerAccess")
public class FWCreativeModTabs {

    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(FantasyWeapons.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> FANTASY_WEAPONS_TAB = TABS.register("fw_tab", () ->
            CreativeTabRegistry.create(builder ->
                    builder.title(Component.translatable("itemGroup." + FantasyWeapons.MOD_ID + ".fw_tab"))
                            .icon(() -> new ItemStack(FWItems.WEAPON_ECLIPSE_SOLDIER_SPEAR.get()))
                            .displayItems((context, entries) -> {
                                for (var item : FW_ITEM_MANAGER.getRegisteredItems()) {
                                    entries.accept(new ItemStack(item));
                                }
                            })
            )
    );

    private FWCreativeModTabs() {
    }

    public static void register() {
        TABS.register();
    }
}
