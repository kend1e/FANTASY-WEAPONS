package net.kenddie.fantasyweapons.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ToolMaterial;

public class FWWeaponBasicSwordItem extends SwordItem {

    private final float attackDamage;

    public FWWeaponBasicSwordItem(ToolMaterial material, float damage, float speed, Item.Properties properties) {
        // Subtract material bonus so the internal addition cancels out:
        // applySwordProperties computes modifier = attackDamageBonus + adjustedDamage = damage
        super(material, damage - material.attackDamageBonus(), speed, properties);
        this.attackDamage = damage;
    }

    public float getDamage() {
        return attackDamage;
    }
}
