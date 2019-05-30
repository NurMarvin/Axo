package de.nurmarvin.axo.manager.impl;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.nurmarvin.axo.manager.GuildSettingsManager;
import de.nurmarvin.axo.settings.GuildSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

public class FileSystemGuildSettingsManager implements GuildSettingsManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuildSettingsManager.class);

    private Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private File guildsFolder = new File("guilds");

    private Map<String, GuildSettings> guildSettings = Maps.newHashMap();

    @Override
    public void createNewSettings(String guildId) {
        if(!guildsFolder.exists()) guildsFolder.mkdirs();

        File file = new File(guildsFolder, guildId + ".json");
        GuildSettings guildSettings = new GuildSettings();

        this.guildSettings.put(guildId, guildSettings);

        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(this.gson.toJson(guildSettings));
            fileWriter.close();
            LOGGER.debug("Created new settings for guild with id {}", guildId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public GuildSettings getGuildSetting(String guildId) {
        if(guildSettings.containsKey(guildId)) return guildSettings.get(guildId);

        try {
            return this.gson.fromJson(new FileReader(new File(guildsFolder, guildId + ".json")), GuildSettings.class);
        } catch (FileNotFoundException ignored) { }

        return null;
    }
}
