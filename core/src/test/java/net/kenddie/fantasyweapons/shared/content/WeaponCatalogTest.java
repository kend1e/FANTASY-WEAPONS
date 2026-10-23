package net.kenddie.fantasyweapons.shared.content;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class WeaponCatalogTest {
    @Test public void loadsCatalogWithOptionalLargeModels() {
        var catalog = WeaponCatalog.INSTANCE;
        assertTrue(catalog.materials().contains("blazing_stone"));
        assertTrue(catalog.weapons().stream().anyMatch(weapon -> weapon.id().equals("weapon_savage_sword") && weapon.largeModel()));
        assertTrue(catalog.weapons().stream().anyMatch(weapon -> weapon.id().equals("weapon_wakizashi") && !weapon.largeModel()));
        assertTrue(catalog.itemIds().contains(catalog.tabIcon()));
    }

    @Test public void rejectsDuplicateIdsAndUnknownIcon() {
        assertThrows(IllegalArgumentException.class, () -> new WeaponCatalog("a", List.of("a"), List.of(new WeaponCatalog.Weapon("a", true))));
        assertThrows(IllegalArgumentException.class, () -> new WeaponCatalog("missing", List.of("a"), List.of()));
    }

    @Test public void exposesImmutableCatalogData() {
        assertThrows(UnsupportedOperationException.class, () -> WeaponCatalog.INSTANCE.materials().add("other"));
        assertThrows(UnsupportedOperationException.class, () -> WeaponCatalog.INSTANCE.weapons().clear());
    }
}
