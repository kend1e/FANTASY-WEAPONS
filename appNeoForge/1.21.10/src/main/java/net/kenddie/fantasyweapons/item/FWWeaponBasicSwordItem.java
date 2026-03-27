package net.kenddie.fantasyweapons.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public class FWWeaponBasicSwordItem extends Item {

    private final float attackDamage;

    public FWWeaponBasicSwordItem(ToolMaterial material, float damage, float speed, Item.Properties properties) {
        super(material.applySwordProperties(properties, damage, speed));
        this.attackDamage = damage;
    }

    public float getDamage() {
        return attackDamage;
    }
}
