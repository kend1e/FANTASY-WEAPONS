package net.kenddie.fantasyweapons.model;

import net.minecraft.client.resources.model.ModelResourceLocation;

public final class ForgeModelLocations {
    public static ModelResourceLocation additional(ModelResourceLocation model) {
        return new ModelResourceLocation(model.id().withPrefix("item/"), model.getVariant());
    }
    private ForgeModelLocations() {}
}
