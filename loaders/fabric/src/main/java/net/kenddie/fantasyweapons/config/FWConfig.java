package net.kenddie.fantasyweapons.config;

import net.kenddie.fantasyweapons.shared.ModConstants;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

import java.util.Map;
import java.util.TreeMap;

import net.kenddie.fantasyweapons.shared.content.WeaponCatalog;

@Config(name = ModConstants.MOD_ID)
public final class FWConfig implements ConfigData {
    private static Map<String, WeaponSettings> active = Map.of();
    public Map<String, WeaponConfig> weapons = new TreeMap<>();

    public FWConfig() {
        WeaponCatalog.INSTANCE.weaponIds().forEach(id -> weapons.put(id, new WeaponConfig()));
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
        WeaponCatalog.INSTANCE.weaponIds().forEach(id -> weapons.computeIfAbsent(id, key -> new WeaponConfig()));
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
