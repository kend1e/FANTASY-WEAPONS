package net.kenddie.fantasyweapons.test;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.resources.ResourceLocation;

final class RecipeTests {
    static void run(GameTestHelper h) {
        var manager = h.getLevel().getRecipeManager();
        for (var entry : TestSupport.EXPECTED.getAsJsonArray("weapons")) {
            var data = entry.getAsJsonObject();
            var id = data.get("id").getAsString();
            var base = new ItemStack(TestSupport.item(data.get("base").getAsString()));
            base.setDamageValue(7);
            base.getOrCreateTag().putString("weapon_test", "preserved");
            var input = new SimpleContainer(new ItemStack(TestSupport.item(data.get("template").getAsString())), base,
                    new ItemStack(TestSupport.item(TestSupport.EXPECTED.get("addition").getAsString())));
            var recipe = manager.getRecipeFor(RecipeType.SMITHING, input, h.getLevel()).orElseThrow();
            h.assertTrue(recipe.getId().equals(new ResourceLocation("fantasy_weapons", id)), "Wrong recipe for " + id);
            var result = recipe.assemble(input, h.getLevel().registryAccess());
            h.assertTrue(result.is(TestSupport.item(id)) && result.getCount() == 1, "Wrong result for " + id);
            h.assertTrue(result.getDamageValue() == 7 && "preserved".equals(result.getTag().getString("weapon_test")), id + " lost base data");
            for (int slot = 0; slot < 3; slot++) {
                var original = input.getItem(slot);
                input.setItem(slot, new ItemStack(Items.DIRT));
                h.assertTrue(!recipe.matches(input, h.getLevel()), id + " accepted invalid slot " + slot);
                input.setItem(slot, original);
            }
        }
        for (var entry : TestSupport.EXPECTED.getAsJsonArray("materials")) {
            var data = entry.getAsJsonObject();
            var definition = data.getAsJsonObject("recipe");
            var pattern = definition.getAsJsonArray("pattern");
            var input = new TransientCraftingContainer(new TestMenu(), 3, 3);
            for (int y = 0; y < pattern.size(); y++) {
                var row = pattern.get(y).getAsString();
                for (int x = 0; x < row.length(); x++) if (row.charAt(x) != ' ')
                    input.setItem(y * 3 + x, new ItemStack(TestSupport.item(definition.getAsJsonObject("ingredients").get(String.valueOf(row.charAt(x))).getAsString())));
            }
            var recipe = manager.getRecipeFor(RecipeType.CRAFTING, input, h.getLevel()).orElseThrow();
            var result = recipe.assemble(input, h.getLevel().registryAccess());
            h.assertTrue(result.is(TestSupport.item(data.get("id").getAsString())) && result.getCount() == definition.get("count").getAsInt(), "Wrong material result");
            input.setItem(0, new ItemStack(Items.DIRT));
            h.assertTrue(!recipe.matches(input, h.getLevel()), "Material accepts invalid ingredient");
        }
        h.succeed();
    }
}
