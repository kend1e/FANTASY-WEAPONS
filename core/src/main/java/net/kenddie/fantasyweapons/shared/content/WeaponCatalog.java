package net.kenddie.fantasyweapons.shared.content;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

public record WeaponCatalog(String tabIcon, List<String> materials, List<Weapon> weapons) {
    public static final WeaponCatalog INSTANCE = load();

    public WeaponCatalog {
        materials = List.copyOf(materials);
        weapons = List.copyOf(weapons);
        var ids = Stream.concat(materials.stream(), weapons.stream().map(Weapon::id)).toList();
        if (ids.size() != ids.stream().distinct().count() || !ids.contains(tabIcon)) {
            throw new IllegalArgumentException("Invalid weapon catalog IDs or creative tab icon");
        }
    }

    public List<String> itemIds() {
        return Stream.concat(materials.stream(), weapons.stream().map(Weapon::id)).toList();
    }

    public List<String> weaponIds() { return weapons.stream().map(Weapon::id).toList(); }

    public record Weapon(String id, boolean largeModel) {}

    private static WeaponCatalog load() {
        var stream = WeaponCatalog.class.getResourceAsStream("/weapon_catalog.json");
        if (stream == null) throw new IllegalStateException("Missing generated weapon catalog");
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            var catalog = new Gson().fromJson(reader, WeaponCatalog.class);
            if (catalog == null) throw new IllegalStateException("Empty generated weapon catalog");
            return catalog;
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Cannot read generated weapon catalog", exception);
        }
    }
}
