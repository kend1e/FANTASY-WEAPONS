package net.kenddie.fantasyweapons.test;

import net.minecraft.world.item.ItemStack;

final class StackAccess {
    static void mark(ItemStack stack) { stack.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, net.minecraft.network.chat.Component.literal("weapon_test")); }
    static boolean marked(ItemStack stack) { return net.minecraft.network.chat.Component.literal("weapon_test").equals(stack.get(net.minecraft.core.component.DataComponents.CUSTOM_NAME)); }
    private StackAccess() {}
}
