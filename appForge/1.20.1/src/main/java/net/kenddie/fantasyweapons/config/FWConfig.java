package net.kenddie.fantasyweapons.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class FWConfig {
    private static volatile Map<String, WeaponSettings> active = Map.of();

    public static void register(IEventBus bus, Collection<String> weapons) {
        var builder = new ForgeConfigSpec.Builder();
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
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, spec, "fantasy_weapons-server.toml");
    }

    public static WeaponSettings weapon(String id) {
        return active.getOrDefault(id, WeaponSettings.DEFAULT);
    }

    private record Entry(ForgeConfigSpec.DoubleValue damage, ForgeConfigSpec.DoubleValue speed) {
        WeaponSettings read() { return new WeaponSettings(damage.get(), speed.get()); }
    }

    private FWConfig() {}
}
