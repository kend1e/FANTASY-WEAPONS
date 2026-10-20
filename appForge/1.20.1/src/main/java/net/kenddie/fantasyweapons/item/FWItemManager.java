package net.kenddie.fantasyweapons.item;

import net.kenddie.fantasyweapons.FantasyWeapons;
import net.kenddie.fantasyweapons.config.FWConfig;
import net.kenddie.fantasyweapons.item.weapon.lib.FWWeaponBasicSwordItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

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
    public void register(IEventBus bus) {
        DeferredRegister<Item> forgeRegistry = DeferredRegister.create(ForgeRegistries.ITEMS, FantasyWeapons.MOD_ID);
        registerBasic(forgeRegistry);
        registerSwords(forgeRegistry);
        forgeRegistry.register(bus);
    }

    private void registerBasic(DeferredRegister<Item> forgeRegistry) {
        for (ResourceLocation id : declaredBasic) {
            forgeRegistry.register(id.getPath(), () -> {
                Item item = new Item(new Item.Properties());
                registered.put(id, item);
                return item;
            });
        }
    }

    private void registerSwords(DeferredRegister<Item> forgeRegistry) {
        for (ResourceLocation id : declaredSwords) {
            forgeRegistry.register(id.getPath(), () -> {
                FWWeaponBasicSwordItem item = new FWWeaponBasicSwordItem(Tiers.NETHERITE, () -> FWConfig.weapon(id.getPath()), new Item.Properties());
                registered.put(id, item);
                return item;
            });
        }
    }
}
