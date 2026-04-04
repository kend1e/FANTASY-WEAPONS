package net.kenddie.fantasyweapons.item;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class FWWeaponBasicSwordItem extends SwordItem {

    private final float attackDamage;

    public FWWeaponBasicSwordItem(Tier tier, float damage, float speed, Item.Properties properties) {
        super(tier, properties.attributes(createRawAttributes(damage, speed)));
        this.attackDamage = damage;
    }

    public float getDamage() {
        return attackDamage;
    }

    /**
     * Build attribute modifiers using raw damage/speed values directly,
     * bypassing the tier bonus that SwordItem.createAttributes() would add.
     * This matches the 1.20.x behavior where damage=9 means the attribute
     * modifier is 9.0 (displayed as 10 with player base of 1).
     */
    private static ItemAttributeModifiers createRawAttributes(float damage, float speed) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, damage, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, speed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }
}
