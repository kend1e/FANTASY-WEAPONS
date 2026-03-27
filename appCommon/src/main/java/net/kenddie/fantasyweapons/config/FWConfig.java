package net.kenddie.fantasyweapons.config;

import de.exlll.configlib.Configuration;
import net.kenddie.fantasyweapons.FantasyWeapons;
import net.kenddie.fantasyweapons.shared.config.FWWeaponDefaults;
import net.kenddie.fantasyweapons.shared.weapon.FWWeaponAttributes;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class FWConfig {

    public static final FWConfig FW_CONFIG = FWConfigManager.updateAndLoadConfig(
            Path.of("config/fantasy_weapons.yml"), FWConfig.class);

    public Map<String, SwordConfig> weapons;

    public FWConfig() {
        this.weapons = new HashMap<>();
        for (String name : FantasyWeapons.WEAPON_NAMES) {
            weapons.put(name, SwordConfig.fromDefaults(name));
        }
    }

    public SwordConfig getSwordConfigOrDefault(String id) {
        return weapons.computeIfAbsent(id, SwordConfig::fromDefaults);
    }

    @Configuration
    public static class SwordConfig {
        public float damage = 9;
        public float speed = -2.4f;

        public SwordConfig() {
            // Default constructor for ConfigLib - uses fallback values
        }

        public SwordConfig(float damage, float speed) {
            this.damage = damage;
            this.speed = speed;
        }

        /**
         * Creates a SwordConfig using defaults from FWWeaponDefaults.
         *
         * @param weaponName the weapon identifier
         * @return a SwordConfig with the weapon's default values
         */
        public static SwordConfig fromDefaults(String weaponName) {
            FWWeaponAttributes attrs = FWWeaponDefaults.get(weaponName);
            return new SwordConfig(attrs.damage(), attrs.speed());
        }
    }
}
