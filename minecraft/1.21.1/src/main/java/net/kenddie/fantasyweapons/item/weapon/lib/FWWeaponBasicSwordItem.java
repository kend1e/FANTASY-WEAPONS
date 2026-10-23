package net.kenddie.fantasyweapons.item.weapon.lib;

import net.kenddie.fantasyweapons.config.WeaponSettings;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import java.util.function.Supplier;

public class FWWeaponBasicSwordItem extends SwordItem {
    private final Supplier<WeaponSettings> settings;
    private volatile AttributeSnapshot cached;

    public FWWeaponBasicSwordItem(Tier tier, Supplier<WeaponSettings> settings, Properties properties) {
        super(tier, properties.attributes(modifiers(settings.get())));
        this.settings = settings;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (!level.isClientSide) applySettings(stack);
    }

    @Override
    public ItemStack getDefaultInstance() {
        var stack = super.getDefaultInstance();
        applySettings(stack);
        return stack;
    }

    public void applySettings(ItemStack stack) {
        var current = settings.get();
        var snapshot = cached;
        if (snapshot == null || !snapshot.settings().equals(current)) {
            snapshot = new AttributeSnapshot(current, modifiers(current));
            cached = snapshot;
        }
        if (!snapshot.modifiers().equals(stack.get(DataComponents.ATTRIBUTE_MODIFIERS)))
            stack.set(DataComponents.ATTRIBUTE_MODIFIERS, snapshot.modifiers());
    }

    private static ItemAttributeModifiers modifiers(WeaponSettings settings) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID,
                        settings.damage(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID,
                        settings.speed(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    private record AttributeSnapshot(WeaponSettings settings, ItemAttributeModifiers modifiers) {}
}
