package net.kenddie.fantasyweapons.model;

import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public final class EpicBakedModel extends WeaponBakedModel {
    public EpicBakedModel(BakedModel smallModel, BakedModel largeModel) { super(smallModel, largeModel); }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> random, RenderContext context) {
        modelFor(context.itemTransformationMode()).emitItemQuads(stack, random, context);
    }

    @Override public boolean isVanillaAdapter() { return false; }
}
