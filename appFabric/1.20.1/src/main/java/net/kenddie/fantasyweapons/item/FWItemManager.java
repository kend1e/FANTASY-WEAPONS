package net.kenddie.fantasyweapons.item;

import net.kenddie.fantasyweapons.FantasyWeapons;
import net.kenddie.fantasyweapons.config.FWConfig;
import net.kenddie.fantasyweapons.item.weapon.lib.FWWeaponBasicSwordItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;

import java.util.*;


/**
 * Manages all items in the mod.
 */
public class FWItemManager {

    /**
     * The singleton instance of ItemManager.
     */
    public static final FWItemManager FW_ITEM_MANAGER = new FWItemManager();
    private final Set<ResourceLocation> declaredBasic = new HashSet<>();
    private final Set<ResourceLocation> declaredSwords = new HashSet<>();
    private final Map<ResourceLocation, Item> registered = new HashMap<>();

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
        ResourceLocation id = new ResourceLocation(FantasyWeapons.MOD_ID, path);
        this.declaredBasic.add(id);
        return new FWDeclaredItem(id);
    }

    /**
     * Declares a basic sword item to be registered.
     *
     * @param path The item's identifier
     */
    public FWDeclaredItem declareSword(String path) {
        ResourceLocation id = new ResourceLocation(FantasyWeapons.MOD_ID, path);
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
        return registered.get(id);
    }

    /**
     * Returns the registered items.
     */
    public List<Item> getRegisteredItems() {
        return new ArrayList<>(registered.values());
    }

    /**
     * Returns the declared swords.
     */
    public Set<ResourceLocation> getDeclaredSwords() {
        return declaredSwords;
    }

    /**
     * Registers all declared items.
     */
    public void register() {
        registerBasic();
        registerSwords();
    }

    private void registerBasic() {
        for (ResourceLocation declaredItem : declaredBasic) {
            Item item = new Item(new Item.Properties());
            registered.put(declaredItem, item);
            Registry.register(BuiltInRegistries.ITEM, declaredItem, item);
        }
    }

    private void registerSwords() {
        for (ResourceLocation id : declaredSwords) {
            FWWeaponBasicSwordItem item = new FWWeaponBasicSwordItem(Tiers.NETHERITE, () -> FWConfig.weapon(id.getPath()), new Item.Properties());
            registered.put(id, item);
            Registry.register(BuiltInRegistries.ITEM, id, item);
        }
    }
}
