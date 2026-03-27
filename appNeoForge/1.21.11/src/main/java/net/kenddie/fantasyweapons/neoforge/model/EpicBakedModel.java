package net.kenddie.fantasyweapons.neoforge.model;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record EpicBakedModel(ItemModel smallItemModel, ItemModel largeItemModel) implements ItemModel {
    @Override
    public void update(ItemStackRenderState renderState, ItemStack itemStack, ItemModelResolver resolver,
                       ItemDisplayContext displayContext, ClientLevel level, ItemOwner owner, int seed) {
        if (displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            largeItemModel.update(renderState, itemStack, resolver, displayContext, level, owner, seed);
        } else {
            smallItemModel.update(renderState, itemStack, resolver, displayContext, level, owner, seed);
        }
    }
}
