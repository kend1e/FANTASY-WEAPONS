package net.kenddie.fantasyweapons.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

import java.util.Map;
import java.util.TreeMap;

import static net.kenddie.fantasyweapons.item.FWItemManager.FW_ITEM_MANAGER;

@Config(name = "fantasy_weapons")
public final class FWConfig implements ConfigData {
    private static Map<String, WeaponSettings> active = Map.of();
    public Map<String, WeaponConfig> weapons = new TreeMap<>();

    public FWConfig() {
        FW_ITEM_MANAGER.getDeclaredSwords().forEach(id -> weapons.put(id.getPath(), new WeaponConfig()));
    }

    public static void register() {
        var config = AutoConfig.register(FWConfig.class, GsonConfigSerializer::new).getConfig();
        var settings = new TreeMap<String, WeaponSettings>();
        config.weapons.forEach((id, entry) -> settings.put(id, new WeaponSettings(entry.damage, entry.speed)));
        active = Map.copyOf(settings);
    }

    public static WeaponSettings weapon(String id) {
        return active.getOrDefault(id, WeaponSettings.DEFAULT);
    }

    @Override
    public void validatePostLoad() {
        if (weapons == null) weapons = new TreeMap<>();
        weapons.values().removeIf(java.util.Objects::isNull);
        FW_ITEM_MANAGER.getDeclaredSwords().forEach(id -> weapons.computeIfAbsent(id.getPath(), key -> new WeaponConfig()));
        weapons.values().forEach(entry -> {
            if (!WeaponSettings.validDamage(entry.damage)) entry.damage = WeaponSettings.DEFAULT.damage();
            if (!WeaponSettings.validSpeed(entry.speed)) entry.speed = WeaponSettings.DEFAULT.speed();
        });
    }

    public static final class WeaponConfig {
        public double damage = WeaponSettings.DEFAULT.damage();
        public double speed = WeaponSettings.DEFAULT.speed();
    }
}
