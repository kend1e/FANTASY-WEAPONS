package net.kenddie.fantasyweapons.config;

import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;

import java.nio.file.Path;

public class FWConfigManager {

    public static <T> T updateAndLoadConfig(Path configPath, Class<T> configClass) {
        YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
                .setNameFormatter(NameFormatters.IDENTITY)
                .build();
        return YamlConfigurations.update(configPath, configClass, properties);
    }
}
