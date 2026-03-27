package net.kenddie.fantasyweapons.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.kenddie.fantasyweapons.FantasyWeapons;
import net.kenddie.fantasyweapons.config.FWConfig.SwordConfig;
import net.kenddie.fantasyweapons.item.weapon.lib.FWWeaponBasicSwordItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;

import java.util.*;

import static net.kenddie.fantasyweapons.config.FWConfig.FW_CONFIG;

/**
 * Manages all items in the mod.
 */
public class FWItemManager {

    /**
     * The singleton instance of ItemManager.
     */
    public static final FWItemManager FW_ITEM_MANAGER = new FWItemManager();

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(FantasyWeapons.MOD_ID, Registries.ITEM);

    private final Set<ResourceLocation> declaredBasic = new HashSet<>();
    private final Set<ResourceLocation> declaredSwords = new HashSet<>();
    private final Map<ResourceLocation, RegistrySupplier<Item>> suppliers = new HashMap<>();

    /**
     * Use {@link #FW_ITEM_MANAGER} to get the singleton instance.
     */
    private FWItemManager() {
    }

    /**
     * Declares a basic item to be registered.
     *
     * @param path The item's identifier
     */
    public FWDeclaredItem declare(String path) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(FantasyWeapons.MOD_ID, path);
        this.declaredBasic.add(id);
        return new FWDeclaredItem(id);
    }

    /**
     * Declares a basic sword item to be registered.
     *
     * @param path The item's identifier
     */
    public FWDeclaredItem declareSword(String path) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(FantasyWeapons.MOD_ID, path);
        this.declaredSwords.add(id);
        return new FWDeclaredItem(id);
    }

    /**
     * Returns the registered sword item with the given path.
     *
     * @param id The item's identifier
     * @return The registered sword item
     */
    public Item getRegistered(ResourceLocation id) {
        RegistrySupplier<Item> supplier = suppliers.get(id);
        return supplier != null ? supplier.get() : null;
    }

    /**
     * Returns the registered items.
     */
    public List<Item> getRegisteredItems() {
        List<Item> items = new ArrayList<>();
        for (RegistrySupplier<Item> supplier : suppliers.values()) {
            items.add(supplier.get());
        }
        return items;
    }

    /**
     * Returns the declared swords.
     */
    public Set<ResourceLocation> getDeclaredSwords() {
        return declaredSwords;
    }

    /**
     * Registers all declared items via Architectury's DeferredRegister.
     */
    public void register() {
        registerBasic();
        registerSwords();
        ITEMS.register();
    }

    private void registerBasic() {
        for (ResourceLocation id : declaredBasic) {
            RegistrySupplier<Item> supplier = ITEMS.register(id, () -> new Item(new Item.Properties()));
            suppliers.put(id, supplier);
        }
    }

    private void registerSwords() {
        for (ResourceLocation id : declaredSwords) {
            RegistrySupplier<Item> supplier = ITEMS.register(id, () -> {
                SwordConfig config = FW_CONFIG.getSwordConfigOrDefault(id.getPath());
                return new FWWeaponBasicSwordItem(Tiers.NETHERITE, config.damage, config.speed, new Item.Properties());
            });
            suppliers.put(id, supplier);
        }
    }
}
