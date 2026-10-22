package net.kenddie.fantasyweapons.test;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import java.util.List;
import java.util.Optional;

final class RecipeAccess {
    static Optional<ItemStack> upgrade(GameTestHelper h, List<ItemStack> items) {
        return craft(h, java.util.List.of(items.get(0), items.get(1), items.get(2),
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY));
    }
    static Optional<ItemStack> craft(GameTestHelper h, List<ItemStack> items) {
        var input = new net.minecraft.world.inventory.CraftingContainer(new TestMenu(), 3, 3);
        for (int slot = 0; slot < items.size(); slot++) input.setItem(slot, items.get(slot));
        return h.getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, h.getLevel())
                .map(recipe -> recipe.assemble(input, h.getLevel().registryAccess()));
    }
    private RecipeAccess() {}
}
