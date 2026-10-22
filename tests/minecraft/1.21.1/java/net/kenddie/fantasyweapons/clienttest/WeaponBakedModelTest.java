package net.kenddie.fantasyweapons.clienttest;

import net.kenddie.fantasyweapons.model.WeaponBakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class WeaponBakedModelTest {
    @Test
    public void inventoryPropertiesComeFromTheSmallModel() {
        var small = new Model(false);
        var large = new Model(true);
        var wrapped = new Wrapper(small, large);
        assertFalse(wrapped.usesBlockLight());
        assertFalse(wrapped.isGui3d());
        assertFalse(wrapped.useAmbientOcclusion());
        assertFalse(wrapped.isCustomRenderer());
        assertSame(small.quads, wrapped.getQuads(null, null, null));
    }

    @Test
    public void onlyHandContextsSelectTheLargeModel() {
        var small = new Model(false);
        var large = new Model(true);
        var wrapped = new Wrapper(small, large);
        for (var context : ItemDisplayContext.values()) {
            boolean hand = context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                    || context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                    || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
                    || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            assertSame(context.toString(), hand ? large : small, wrapped.select(context));
        }
    }

    private static final class Wrapper extends WeaponBakedModel {
        Wrapper(BakedModel small, BakedModel large) { super(small, large); }
        BakedModel select(ItemDisplayContext context) { return modelFor(context); }
    }

    private static final class Model implements BakedModel {
        private final boolean block;
        private final List<BakedQuad> quads = new ArrayList<>();
        Model(boolean block) { this.block = block; }
        @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) { return quads; }
        @Override public boolean useAmbientOcclusion() { return block; }
        @Override public boolean isGui3d() { return block; }
        @Override public boolean usesBlockLight() { return block; }
        @Override public boolean isCustomRenderer() { return block; }
        @Override public TextureAtlasSprite getParticleIcon() { return null; }
        @Override public ItemTransforms getTransforms() { return null; }
        @Override public ItemOverrides getOverrides() { return null; }
    }
}
