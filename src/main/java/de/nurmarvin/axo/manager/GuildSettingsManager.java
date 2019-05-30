package de.nurmarvin.axo.manager;

import de.nurmarvin.axo.settings.GuildSettings;

public interface GuildSettingsManager {
    void createNewSettings(String guildId);
    GuildSettings getGuildSetting(String guildId);
}
