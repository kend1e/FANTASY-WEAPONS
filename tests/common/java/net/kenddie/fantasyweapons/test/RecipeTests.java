package net.kenddie.fantasyweapons.test;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.ArrayList;
import java.util.Collections;

final class RecipeTests {
    static void run(GameTestHelper h) {
        for (var entry : TestSupport.EXPECTED.getAsJsonArray("weapons")) {
            var data = entry.getAsJsonObject();
            var id = data.get("id").getAsString();
            var base = new ItemStack(TestSupport.item(data.get("base").getAsString()));
            base.setDamageValue(7);
            StackAccess.mark(base);
            var inputs = new ArrayList<>(java.util.List.of(
                    new ItemStack(TestSupport.item(data.get("template").getAsString())), base,
                    new ItemStack(TestSupport.item(TestSupport.EXPECTED.get("addition").getAsString()))));
            var result = RecipeAccess.upgrade(h, inputs).orElseThrow();
            h.assertTrue(result.is(TestSupport.item(id)) && result.getCount() == 1, "Wrong result for " + id);
            h.assertTrue(result.getDamageValue() == 7 && StackAccess.marked(result), id + " lost base data");
            for (int slot = 0; slot < 3; slot++) {
                var original = inputs.set(slot, new ItemStack(Items.DIRT));
                h.assertTrue(RecipeAccess.upgrade(h, inputs).isEmpty(), id + " accepted invalid slot " + slot);
                inputs.set(slot, original);
            }
        }
        for (var entry : TestSupport.EXPECTED.getAsJsonArray("materials")) {
            var data = entry.getAsJsonObject();
            var definition = data.getAsJsonObject("recipe");
            var pattern = definition.getAsJsonArray("pattern");
            var inputs = new ArrayList<>(Collections.nCopies(9, ItemStack.EMPTY));
            for (int y = 0; y < pattern.size(); y++) {
                var row = pattern.get(y).getAsString();
                for (int x = 0; x < row.length(); x++) if (row.charAt(x) != ' ')
                    inputs.set(y * 3 + x, new ItemStack(TestSupport.item(definition.getAsJsonObject("ingredients").get(String.valueOf(row.charAt(x))).getAsString())));
            }
            var result = RecipeAccess.craft(h, inputs).orElseThrow();
            h.assertTrue(result.is(TestSupport.item(data.get("id").getAsString())) && result.getCount() == definition.get("count").getAsInt(), "Wrong material result");
            inputs.set(0, new ItemStack(Items.DIRT));
            h.assertTrue(RecipeAccess.craft(h, inputs).isEmpty(), "Material accepts invalid ingredient");
        }
        h.succeed();
    }
}
