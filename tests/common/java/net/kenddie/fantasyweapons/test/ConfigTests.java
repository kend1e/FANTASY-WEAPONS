package net.kenddie.fantasyweapons.test;

import net.kenddie.fantasyweapons.config.FWConfig;
import net.kenddie.fantasyweapons.config.WeaponSettings;
import net.kenddie.fantasyweapons.shared.content.WeaponCatalog;
import net.minecraft.gametest.framework.GameTestHelper;

final class ConfigTests {
    static WeaponSettings expected(String id) {
        var data = TestSupport.EXPECTED;
        return id.equals(data.get("changed").getAsString())
                ? new WeaponSettings(data.get("damage").getAsDouble(), data.get("speed").getAsDouble()) : WeaponSettings.DEFAULT;
    }
    static void run(GameTestHelper h) {
        for (var id : WeaponCatalog.INSTANCE.weaponIds())
            AttributeAccess.check(h, FWConfig.weapon(id).equals(expected(id)), "Config did not load for " + id);
        h.succeed();
    }
}
