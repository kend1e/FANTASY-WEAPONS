package net.kenddie.fantasyweapons.shared.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.kenddie.fantasyweapons.shared.weapon.FWWeaponAttributes;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Loads and caches weapon default attributes from weapon_defaults.json.
 * Similar to FAArmorDefaults in Fantasy Armor.
 */
public final class FWWeaponDefaults {

    private static final String RESOURCE_PATH = "/weapon_defaults.json";
    private static final FWWeaponAttributes FALLBACK = FWWeaponAttributes.builder()
            .damage(9.0f)
            .speed(-2.4f)
            .reach(0.0f)
            .durability(0)
            .build();

    private static Map<String, FWWeaponAttributes> cache;

    private FWWeaponDefaults() {}

    /**
     * Loads all weapon defaults from weapon_defaults.json.
     * Results are cached after first load.
     *
     * @return an unmodifiable map of weapon name to attributes
     */
    public static Map<String, FWWeaponAttributes> load() {
        if (cache != null) return cache;

        InputStream is = FWWeaponDefaults.class.getResourceAsStream(RESOURCE_PATH);
        if (is == null) {
            // If no resource file exists, return empty map
            // Individual weapon lookups will fall back to FALLBACK
            cache = Collections.emptyMap();
            return cache;
        }

        Type type = new TypeToken<Map<String, RawWeapon>>() {}.getType();
        Map<String, RawWeapon> raw = new Gson().fromJson(
                new InputStreamReader(is, StandardCharsets.UTF_8), type);

        Map<String, FWWeaponAttributes> result = new LinkedHashMap<>();
        for (var entry : raw.entrySet()) {
            result.put(entry.getKey(), entry.getValue().toAttributes());
        }

        cache = Collections.unmodifiableMap(result);
        return cache;
    }

    /**
     * Gets the default attributes for a specific weapon.
     *
     * @param weaponName the weapon identifier (e.g., "weapon_dark_lord_sword")
     * @return the weapon attributes, or fallback defaults if not found
     */
    public static FWWeaponAttributes get(String weaponName) {
        Map<String, FWWeaponAttributes> all = load();
        FWWeaponAttributes attrs = all.get(weaponName);
        return attrs != null ? attrs : FALLBACK;
    }

    /**
     * Gets the fallback default attributes for weapons not defined in JSON.
     *
     * @return fallback weapon attributes
     */
    public static FWWeaponAttributes fallback() {
        return FALLBACK;
    }

    /**
     * Internal class for parsing raw JSON weapon entries.
     */
    private static class RawWeapon {
        float damage = 9.0f;
        float speed = -2.4f;
        float reach = 0.0f;
        int durability = 0;

        FWWeaponAttributes toAttributes() {
            return new FWWeaponAttributes(damage, speed, reach, durability);
        }
    }
}
