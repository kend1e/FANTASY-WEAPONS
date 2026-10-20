package net.kenddie.fantasyweapons.test;

import net.kenddie.fantasyweapons.shared.content.WeaponCatalog;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;

final class AttributeTests {
    static void run(GameTestHelper h) {
        for (var id : WeaponCatalog.INSTANCE.weaponIds()) {
            var item = TestSupport.item(id);
            var settings = ConfigTests.expected(id);
            TestSupport.near(((SwordItem) item).getDamage(), settings.damage(), id + " sword damage");
            var stack = new ItemStack(item);
            var modifiers = stack.getAttributeModifiers(EquipmentSlot.MAINHAND);
            for (var attribute : java.util.List.of(Attributes.ATTACK_DAMAGE, Attributes.ATTACK_SPEED)) {
                var entries = modifiers.get(attribute);
                h.assertTrue(entries.size() == 1, id + " must have one modifier per attribute");
                var modifier = entries.iterator().next();
                h.assertTrue(modifier.getOperation() == AttributeModifier.Operation.ADDITION, id + " operation");
                TestSupport.near(modifier.getAmount(), attribute == Attributes.ATTACK_DAMAGE ? settings.damage() : settings.speed(), id + " modifier");
            }
            h.assertTrue(stack.getAttributeModifiers(EquipmentSlot.OFFHAND).isEmpty(), id + " offhand modifiers");
        }
        var player = h.makeMockSurvivalPlayer();
        double baseDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        double baseSpeed = player.getAttributeValue(Attributes.ATTACK_SPEED);
        try {
            for (var id : WeaponCatalog.INSTANCE.weaponIds()) {
                var settings = ConfigTests.expected(id);
                var stack = new ItemStack(TestSupport.item(id));
                for (int repeat = 0; repeat < 2; repeat++) {
                    player.setItemSlot(EquipmentSlot.MAINHAND, stack.copy());
                    player.tick();
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
