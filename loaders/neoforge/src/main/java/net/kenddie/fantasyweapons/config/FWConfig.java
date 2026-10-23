package net.kenddie.fantasyweapons.config;

import net.kenddie.fantasyweapons.shared.ModConstants;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.bus.api.IEventBus;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class FWConfig {
    private static volatile Map<String, WeaponSettings> active = Map.of();

    public static void register(IEventBus bus, ModContainer container, ModConfig.Type type, Collection<String> weapons) {
        var builder = new ModConfigSpec.Builder();
        var entries = new TreeMap<String, Entry>();
        builder.push("weapons");
        weapons.stream().sorted().forEach(id -> {
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
        var spec = builder.build();
        bus.addListener((ModConfigEvent.Loading event) -> {
            if (event.getConfig().getSpec() == spec) {
                active = entries.entrySet().stream().collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey, entry -> entry.getValue().read()));
            }
        });
        bus.addListener((ModConfigEvent.Unloading event) -> {
            if (event.getConfig().getSpec() == spec) active = Map.of();
        });
        container.registerConfig(type, spec, ModConstants.MOD_ID + "-server.toml");
    }

    public static WeaponSettings weapon(String id) {
        return active.getOrDefault(id, WeaponSettings.DEFAULT);
    }

    private record Entry(ModConfigSpec.DoubleValue damage, ModConfigSpec.DoubleValue speed) {
        WeaponSettings read() { return new WeaponSettings(damage.get(), speed.get()); }
    }

    private FWConfig() {}
}
