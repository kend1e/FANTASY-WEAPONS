package net.kenddie.fantasyweapons.test;

import net.kenddie.fantasyweapons.shared.content.WeaponCatalog;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

final class AttributeTests {
    static void run(GameTestHelper h) {
        for (var id : WeaponCatalog.INSTANCE.weaponIds()) {
            var settings = ConfigTests.expected(id);
            AttributeAccess.verify(h, id, settings);
        }
        var player = AttributeAccess.player(h);
        var position = h.absolutePos(new net.minecraft.core.BlockPos(1, 2, 1));
        player.setPos(position.getX() + 0.5, position.getY(), position.getZ() + 0.5);
        player.setNoGravity(true);
        player.setInvulnerable(true);
        double baseDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        double baseSpeed = player.getAttributeValue(Attributes.ATTACK_SPEED);
        try {
            for (var id : WeaponCatalog.INSTANCE.weaponIds()) {
                var settings = ConfigTests.expected(id);
                var stack = new ItemStack(TestSupport.item(id));
                for (int repeat = 0; repeat < 2; repeat++) {
                    player.setItemSlot(EquipmentSlot.MAINHAND, stack.copy());
                    player.tick();
                    player.tick();
                    h.assertTrue(player.isAlive(), "Attribute fixture died while testing " + id);
                    TestSupport.near(player.getAttributeValue(Attributes.ATTACK_DAMAGE), baseDamage + settings.damage(), id + " equipped damage");
                    TestSupport.near(player.getAttributeValue(Attributes.ATTACK_SPEED), baseSpeed + settings.speed(), id + " equipped speed");
                }
                player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                player.tick();
                TestSupport.near(player.getAttributeValue(Attributes.ATTACK_DAMAGE), baseDamage, id + " removed damage");
                TestSupport.near(player.getAttributeValue(Attributes.ATTACK_SPEED), baseSpeed, id + " removed speed");
            }
        } finally {
            player.discard();
        }
        h.succeed();
    }
}
