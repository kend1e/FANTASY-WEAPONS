package net.kenddie.fantasyweapons;

import net.minecraft.resources.Identifier;

public final class GameVersion {
    public static Identifier id(String namespace, String path) { return Identifier.fromNamespaceAndPath(namespace, path); }
    public static Identifier id(String value) { return Identifier.parse(value); }
    private GameVersion() {}
}
