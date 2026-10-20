# Weapon configuration

- Forge 1.20.1: `<world>/serverconfig/fantasy_weapons-server.toml`. Settings belong to the world and Forge synchronizes them to clients. Restart the world after editing.
- Fabric 1.20.1: `config/fantasy_weapons.json`, loaded by Cloth/AutoConfig. Restart the game or dedicated server after editing. This file is local; AutoConfig does not synchronize it between installations.

Each declared weapon has `damage` and `speed` attribute modifiers. Minecraft adds the entity's base attributes (normally 1 damage and 4 attack speed for a player). Defaults are 9 and -2.4. Damage accepts 0–2048; speed accepts -4–1024. Invalid values revert to defaults. Existing items use the current settings; crafting new weapons is unnecessary.

The previous YAML config is unused. No migration is performed.
