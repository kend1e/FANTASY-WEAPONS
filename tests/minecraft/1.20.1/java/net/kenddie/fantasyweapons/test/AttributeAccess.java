package net.kenddie.fantasyweapons.test;

import net.kenddie.fantasyweapons.config.WeaponSettings;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;

final class AttributeAccess {
    static net.minecraft.world.entity.player.Player player(GameTestHelper h) { return h.makeMockSurvivalPlayer(); }
    static void verify(GameTestHelper h, String id, WeaponSettings settings) {
        var item = TestSupport.item(id);
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
    private AttributeAccess() {}
}
