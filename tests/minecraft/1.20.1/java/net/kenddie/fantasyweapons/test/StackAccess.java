package net.kenddie.fantasyweapons.test;

import net.minecraft.world.item.ItemStack;

final class StackAccess {
    static void mark(ItemStack stack) { stack.getOrCreateTag().putString("weapon_test", "preserved"); }
    static boolean marked(ItemStack stack) { return "preserved".equals(stack.getOrCreateTag().getString("weapon_test")); }
    private StackAccess() {}
}
