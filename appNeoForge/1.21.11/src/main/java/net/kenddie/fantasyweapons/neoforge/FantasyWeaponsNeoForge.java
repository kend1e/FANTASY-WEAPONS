package net.kenddie.fantasyweapons.neoforge;

import net.kenddie.fantasyweapons.FantasyWeapons;
import net.kenddie.fantasyweapons.item.FWItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(FantasyWeapons.MOD_ID)
public class FantasyWeaponsNeoForge {
    public FantasyWeaponsNeoForge(IEventBus modBus) {
        FWItems.register(modBus);
        modBus.register(new FantasyWeaponsClientNeoForge());
    }
}
