package net.kenddie.fantasyweapons.config;

import de.exlll.configlib.Configuration;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static net.kenddie.fantasyweapons.item.FWItemManager.FW_ITEM_MANAGER;

@Configuration
public class FWConfig {

    public static final FWConfig FW_CONFIG = FWConfigManager.updateAndLoadConfig(Path.of("config/fantasy_weapons.yml"), FWConfig.class);

    public FWConfig() {
        this.weapons = new HashMap<>();
        FW_ITEM_MANAGER.getDeclaredSwords().forEach(id -> weapons.put(id.getPath(), SwordConfig.DEFAULT));
    }

    public Map<String, SwordConfig> weapons;

    public SwordConfig getSwordConfigOrDefault(String id) {
        return weapons.computeIfAbsent(id, k -> SwordConfig.DEFAULT);
    }

    @Configuration
    public static class SwordConfig {
        public static final SwordConfig DEFAULT = new SwordConfig();
        public float damage = 9;
        public float speed = -2.4f;
    }
}
