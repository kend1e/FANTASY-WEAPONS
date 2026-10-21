package net.kenddie.fantasyweapons.clienttest;

import net.kenddie.fantasyweapons.GameVersion;
import net.kenddie.fantasyweapons.model.ForgeModelLocations;
import net.kenddie.fantasyweapons.shared.content.WeaponCatalog;
import net.minecraft.client.resources.model.ModelResourceLocation;
import org.junit.Test;
import static org.junit.Assert.*;

public class ForgeModelLocationsTest {
    @Test
    public void everyAdditionalModelResolvesToItsResource() throws Exception {
        for (var weapon : WeaponCatalog.INSTANCE.weapons()) {
            if (!weapon.largeModel()) continue;
            var inventory = new ModelResourceLocation(GameVersion.id("fantasy_weapons", weapon.id() + "_large"), "inventory");
            var additional = ForgeModelLocations.additional(inventory);
            var id = additional.id();
            var path = "/assets/" + id.getNamespace() + "/models/" + id.getPath() + ".json";
            try (var resource = getClass().getResourceAsStream(path)) {
                assertNotNull("Forge ModelBakery cannot load " + additional + " at " + path, resource);
            }
        }
    }
}
