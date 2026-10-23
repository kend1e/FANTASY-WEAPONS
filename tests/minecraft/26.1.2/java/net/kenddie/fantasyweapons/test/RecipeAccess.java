package net.kenddie.fantasyweapons.test;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import java.util.List;
import java.util.Optional;

final class RecipeAccess {
    static Optional<ItemStack> upgrade(GameTestHelper h, List<ItemStack> items) {
        var input = new net.minecraft.world.item.crafting.SmithingRecipeInput(items.get(0), items.get(1), items.get(2));
        return h.getLevel().getServer().getRecipeManager().getRecipeFor(RecipeType.SMITHING, input, h.getLevel())
                .map(recipe -> recipe.value().assemble(input));
    }
    static Optional<ItemStack> craft(GameTestHelper h, List<ItemStack> items) {
        var input = net.minecraft.world.item.crafting.CraftingInput.of(3, 3, items);
        return h.getLevel().getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, h.getLevel())
                .map(recipe -> recipe.value().assemble(input));
    }
    private RecipeAccess() {}
}
