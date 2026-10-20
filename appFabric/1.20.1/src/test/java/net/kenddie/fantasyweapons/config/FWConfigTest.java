package net.kenddie.fantasyweapons.config;

import com.google.gson.Gson;
import net.kenddie.fantasyweapons.item.FWItems;
import org.junit.BeforeClass;
import org.junit.Test;

import static net.kenddie.fantasyweapons.item.FWItemManager.FW_ITEM_MANAGER;
import static org.junit.Assert.*;

public class FWConfigTest {
    @BeforeClass
    public static void declareWeapons() { FWItems.declare(); }

    @Test
    public void restoresMissingEntriesAndRepairsInvalidValues() {
        var config = new Gson().fromJson("{\"weapons\":{\"weapon_fates_beacon\":{\"damage\":-1,\"speed\":-5},\"weapon_thief_dagger\":null}}", FWConfig.class);
        config.validatePostLoad();
        assertEquals(FW_ITEM_MANAGER.getDeclaredSwords().size(), config.weapons.size());
        var entry = config.weapons.get("weapon_fates_beacon");
        assertEquals(WeaponSettings.DEFAULT.damage(), entry.damage, 0.0);
        assertEquals(WeaponSettings.DEFAULT.speed(), entry.speed, 0.0);
        entry.damage = 15.0;
        assertEquals(WeaponSettings.DEFAULT.damage(), config.weapons.get("weapon_thief_dagger").damage, 0.0);
    }

    @Test
    public void restoresNullMap() {
        var config = new Gson().fromJson("{\"weapons\":null}", FWConfig.class);
        config.validatePostLoad();
        assertEquals(FW_ITEM_MANAGER.getDeclaredSwords().size(), config.weapons.size());
    }

    @Test
    public void preservesCustomValuesThroughJson() {
        var config = new FWConfig();
        var entry = config.weapons.get("weapon_fates_beacon");
        entry.damage = 12.5;
        entry.speed = -1.7;
        var gson = new Gson();
        var loaded = gson.fromJson(gson.toJson(config), FWConfig.class);
        loaded.validatePostLoad();
        assertEquals(12.5, loaded.weapons.get("weapon_fates_beacon").damage, 0.0);
        assertEquals(-1.7, loaded.weapons.get("weapon_fates_beacon").speed, 0.0);
    }
}
