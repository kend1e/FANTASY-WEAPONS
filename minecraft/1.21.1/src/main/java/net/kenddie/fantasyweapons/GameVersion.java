package net.kenddie.fantasyweapons;

import net.minecraft.resources.ResourceLocation;

public final class GameVersion {
    public static ResourceLocation id(String namespace, String path) { return ResourceLocation.fromNamespaceAndPath(namespace, path); }
    public static ResourceLocation id(String value) { return ResourceLocation.parse(value); }
    private GameVersion() {}
}
