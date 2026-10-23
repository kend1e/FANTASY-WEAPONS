package net.kenddie.fantasyweapons.test;

import net.kenddie.fantasyweapons.config.WeaponSettings;
import net.kenddie.fantasyweapons.item.weapon.lib.FWWeaponBasicSwordItem;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

final class AttributeAccess {
    static net.minecraft.world.entity.player.Player player(GameTestHelper h) { return h.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL); }
    static void verify(GameTestHelper h, String id, WeaponSettings settings) {
        var item = (FWWeaponBasicSwordItem) TestSupport.item(id);
        AttributeAccess.check(h, new ItemStack(item).is(net.minecraft.tags.ItemTags.MELEE_WEAPON_ENCHANTABLE), id + " not sword-enchantable");
        var stack = item.getDefaultInstance();
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        stack.setDamageValue(7);
        StackAccess.mark(stack);
        var entity = net.minecraft.world.entity.EntityType.ARMOR_STAND.create(h.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
        AttributeAccess.check(h, entity != null, "Missing attribute fixture entity");
        item.inventoryTick(stack, h.getLevel(), entity, null);
        AttributeAccess.check(h, stack.getDamageValue() == 7 && StackAccess.marked(stack), id + " refresh lost data");
        var entries = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers();
        AttributeAccess.check(h, entries.size() == 2, id + " must have two modifiers");
        for (var attribute : java.util.List.of(Attributes.ATTACK_DAMAGE, Attributes.ATTACK_SPEED)) {
            var matching = entries.stream().filter(entry -> entry.attribute().equals(attribute)).toList();
            AttributeAccess.check(h, matching.size() == 1, id + " duplicate or missing attribute");
            var entry = matching.getFirst();
            AttributeAccess.check(h, entry.slot().test(EquipmentSlot.MAINHAND) && !entry.slot().test(EquipmentSlot.OFFHAND), id + " slot");
            AttributeAccess.check(h, entry.modifier().operation() == AttributeModifier.Operation.ADD_VALUE, id + " operation");
            TestSupport.near(entry.modifier().amount(), attribute.equals(Attributes.ATTACK_DAMAGE) ? settings.damage() : settings.speed(), id + " modifier");
        }
    }
    static void check(GameTestHelper h, boolean condition, String message) { h.assertTrue(condition, net.minecraft.network.chat.Component.literal(message)); }
    private AttributeAccess() {}
}
