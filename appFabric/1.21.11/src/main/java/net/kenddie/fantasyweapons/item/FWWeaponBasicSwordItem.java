package net.kenddie.fantasyweapons.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public class FWWeaponBasicSwordItem extends Item {

    private final float attackDamage;

    public FWWeaponBasicSwordItem(ToolMaterial material, float damage, float speed, Item.Properties properties) {
        // Subtract material bonus so the internal addition cancels out:
        // applySwordProperties computes modifier = attackDamageBonus + adjustedDamage = damage
        super(material.applySwordProperties(properties, damage - material.attackDamageBonus(), speed));
        this.attackDamage = damage;
    }

    public float getDamage() {
        return attackDamage;
    }
}
