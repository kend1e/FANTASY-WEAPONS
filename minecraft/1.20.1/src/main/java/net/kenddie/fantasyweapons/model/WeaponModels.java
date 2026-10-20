package net.kenddie.fantasyweapons.model;

import net.kenddie.fantasyweapons.shared.ModConstants;
import net.kenddie.fantasyweapons.shared.content.WeaponCatalog;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public final class WeaponModels {
    public static Map<ModelResourceLocation, ModelResourceLocation> pairs() {
        var pairs = new LinkedHashMap<ModelResourceLocation, ModelResourceLocation>();
        for (var weapon : WeaponCatalog.INSTANCE.weapons()) {
            if (weapon.largeModel()) pairs.put(model(weapon.id()), model(weapon.id() + "_large"));
        }
        return Map.copyOf(pairs);
    }

    private static ModelResourceLocation model(String path) {
        return new ModelResourceLocation(new ResourceLocation(ModConstants.MOD_ID, path), "inventory");
    }

    private WeaponModels() {}
}
