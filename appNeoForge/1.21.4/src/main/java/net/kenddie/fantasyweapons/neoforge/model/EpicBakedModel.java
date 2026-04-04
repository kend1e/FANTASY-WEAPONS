package net.kenddie.fantasyweapons.neoforge.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

@SuppressWarnings("deprecation")
public record EpicBakedModel(BakedModel smallBakedModel, BakedModel largeBakedModel) implements BakedModel {
    private static final ThreadLocal<ItemDisplayContext> CURRENT_CONTEXT = new ThreadLocal<>();

    private BakedModel selectModel(ItemDisplayContext context) {
        if (context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            return largeBakedModel;
        }
        return smallBakedModel;
    }

    @Override
    public void applyTransform(ItemDisplayContext itemDisplayContext, PoseStack poseStack, boolean applyLeftHandTransform) {
        CURRENT_CONTEXT.set(itemDisplayContext);
        selectModel(itemDisplayContext).applyTransform(itemDisplayContext, poseStack, applyLeftHandTransform);
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return largeBakedModel.getParticleIcon();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState blockState, Direction direction, RandomSource randomSource) {
        var context = CURRENT_CONTEXT.get();
        return selectModel(context != null ? context : ItemDisplayContext.NONE).getQuads(blockState, direction, randomSource);
    }

    @Override
    public ItemTransforms getTransforms() {
        return largeBakedModel.getTransforms();
    }

    @Override
    public boolean isGui3d() {
        return largeBakedModel.isGui3d();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return largeBakedModel.useAmbientOcclusion();
    }

    @Override
    public boolean usesBlockLight() {
        return largeBakedModel.usesBlockLight();
    }
}
