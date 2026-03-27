package net.kenddie.fantasyweapons.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class FWWeaponBasicSwordItem extends SwordItem {

    private final float attackDamage;

    public FWWeaponBasicSwordItem(Tier tier, float damage, float speed, Item.Properties properties) {
        super(tier, properties.attributes(SwordItem.createAttributes(tier, (int) damage, speed)));
        this.attackDamage = damage;
    }

    public float getDamage() {
        return attackDamage;
    }
}
