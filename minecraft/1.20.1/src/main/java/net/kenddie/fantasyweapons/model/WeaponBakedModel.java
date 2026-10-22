package net.kenddie.fantasyweapons.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public abstract class WeaponBakedModel implements BakedModel {
    private final BakedModel smallModel;
    private final BakedModel largeModel;

    protected WeaponBakedModel(BakedModel smallModel, BakedModel largeModel) {
        this.smallModel = smallModel;
        this.largeModel = largeModel;
    }

    protected BakedModel modelFor(ItemDisplayContext context) {
        return switch (context) {
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> largeModel;
            default -> smallModel;
        };
    }

    @Override public ItemOverrides getOverrides() { return smallModel.getOverrides(); }
    @Override public TextureAtlasSprite getParticleIcon() { return smallModel.getParticleIcon(); }
    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) { return smallModel.getQuads(state, side, random); }
    @Override public ItemTransforms getTransforms() { return largeModel.getTransforms(); }
    @Override public boolean isCustomRenderer() { return smallModel.isCustomRenderer(); }
    @Override public boolean isGui3d() { return smallModel.isGui3d(); }
    @Override public boolean useAmbientOcclusion() { return smallModel.useAmbientOcclusion(); }
    @Override public boolean usesBlockLight() { return smallModel.usesBlockLight(); }
}
