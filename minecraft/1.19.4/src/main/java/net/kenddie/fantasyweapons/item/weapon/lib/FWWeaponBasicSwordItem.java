package net.kenddie.fantasyweapons.item.weapon.lib;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.kenddie.fantasyweapons.config.WeaponSettings;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

import java.util.function.Supplier;

public class FWWeaponBasicSwordItem extends SwordItem {
    private final Supplier<WeaponSettings> settings;
    private volatile AttributeSnapshot cached;

    public FWWeaponBasicSwordItem(Tier tier, Supplier<WeaponSettings> settings, Properties properties) {
        super(tier, 0, 0.0f, properties);
        this.settings = settings;
    }

    @Override
    public float getDamage() {
        return (float) settings.get().damage();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot != EquipmentSlot.MAINHAND) return super.getDefaultAttributeModifiers(slot);
        var current = settings.get();
        var snapshot = cached;
        if (snapshot == null || !current.equals(snapshot.settings())) {
            var modifiers = ImmutableMultimap.<Attribute, AttributeModifier>builder()
                    .put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID,
                            "Weapon modifier", current.damage(), AttributeModifier.Operation.ADDITION))
                    .put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID,
                            "Weapon modifier", current.speed(), AttributeModifier.Operation.ADDITION))
                    .build();
            snapshot = new AttributeSnapshot(current, modifiers);
            cached = snapshot;
        }
        return snapshot.modifiers();
    }
    private record AttributeSnapshot(WeaponSettings settings, Multimap<Attribute, AttributeModifier> modifiers) {}
}
