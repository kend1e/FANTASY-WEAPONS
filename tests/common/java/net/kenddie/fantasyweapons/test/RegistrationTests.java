package net.kenddie.fantasyweapons.test;

import net.kenddie.fantasyweapons.shared.content.WeaponCatalog;
import net.kenddie.fantasyweapons.item.weapon.lib.FWWeaponBasicSwordItem;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.core.registries.BuiltInRegistries;

final class RegistrationTests {
    static void run(GameTestHelper h) {
        var catalog = WeaponCatalog.INSTANCE;
        for (var id : catalog.itemIds()) TestSupport.item(id);
        for (var id : catalog.weaponIds()) AttributeAccess.check(h, TestSupport.item(id) instanceof FWWeaponBasicSwordItem, id + " is not a weapon");
        var actual = BuiltInRegistries.ITEM.keySet().stream().filter(key -> key.getNamespace().equals("fantasy_weapons"))
                .map(key -> key.getPath()).collect(java.util.stream.Collectors.toSet());
        AttributeAccess.check(h, actual.equals(new java.util.HashSet<>(catalog.itemIds())), "Registered items differ from catalog");
        h.succeed();
    }
}
