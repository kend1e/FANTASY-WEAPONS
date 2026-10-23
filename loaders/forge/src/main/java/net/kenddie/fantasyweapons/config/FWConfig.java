package net.kenddie.fantasyweapons.config;

import net.kenddie.fantasyweapons.shared.content.WeaponCatalog;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Map;
import java.util.TreeMap;

public final class FWConfig {
    public static final ForgeConfigSpec SPEC;
    private static final Map<String, Entry> ENTRIES;

    static {
        var builder = new ForgeConfigSpec.Builder();
        var entries = new TreeMap<String, Entry>();
        builder.push("weapons");
        WeaponCatalog.INSTANCE.weaponIds().stream().sorted().forEach(id -> {
            builder.push(id);
            var damage = builder.comment("Attack damage modifier; added to the entity's base damage.")
                    .worldRestart().defineInRange("damage", WeaponSettings.DEFAULT.damage(),
                            WeaponSettings.MIN_DAMAGE, WeaponSettings.MAX_DAMAGE);
            var speed = builder.comment("Attack speed modifier; added to the entity's base attack speed.")
                    .worldRestart().defineInRange("speed", WeaponSettings.DEFAULT.speed(),
                            WeaponSettings.MIN_SPEED, WeaponSettings.MAX_SPEED);
            entries.put(id, new Entry(damage, speed));
            builder.pop();
        });
        builder.pop();
        SPEC = builder.build();
        ENTRIES = Map.copyOf(entries);
    }

    public static WeaponSettings weapon(String id) {
        var entry = ENTRIES.get(id);
        return entry != null && SPEC.isLoaded() ? entry.read() : WeaponSettings.DEFAULT;
    }

    private record Entry(ForgeConfigSpec.DoubleValue damage, ForgeConfigSpec.DoubleValue speed) {
        WeaponSettings read() { return new WeaponSettings(damage.get(), speed.get()); }
    }

    private FWConfig() {}
}
