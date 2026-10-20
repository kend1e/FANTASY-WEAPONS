package net.kenddie.fantasyweapons;

import net.minecraft.resources.ResourceLocation;

public final class GameVersion {
    public static ResourceLocation id(String namespace, String path) { return new ResourceLocation(namespace, path); }
    public static ResourceLocation id(String value) { return new ResourceLocation(value); }
    private GameVersion() {}
}
