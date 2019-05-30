package de.nurmarvin.axo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mewna.catnip.Catnip;
import com.mewna.catnip.shard.DiscordEvent;
import de.nurmarvin.axo.command.AbstractCommand;
import de.nurmarvin.axo.command.CommandContext;
import de.nurmarvin.axo.command.CommandLevel;
import de.nurmarvin.axo.manager.CommandManager;
import de.nurmarvin.axo.manager.GuildSettingsManager;
import de.nurmarvin.axo.manager.RateLimitManager;
import de.nurmarvin.axo.manager.impl.DefaultCommandManager;
import de.nurmarvin.axo.manager.impl.DefaultRateLimitManager;
import de.nurmarvin.axo.manager.impl.FileSystemGuildSettingsManager;
import de.nurmarvin.axo.module.modules.moderation.commands.CleanCommand;
import de.nurmarvin.axo.module.modules.utility.commands.InfoCommand;
import de.nurmarvin.axo.module.modules.utility.commands.ServerInfoCommand;
import de.nurmarvin.axo.settings.GuildSettings;
import de.nurmarvin.axo.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public final class AxoDiscordBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(AxoDiscordBot.class);

    public static final String VERSION = "1.0-DEV";
    private static AxoDiscordBot instance;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File settingsFile = new File("settings.json");
    private Settings settings;
    private Catnip catnip;

    private CommandManager commandManager;
    private GuildSettingsManager guildSettingsManager;
    private RateLimitManager rateLimitManager;

    public static void main(String[] args) throws IOException {
        new AxoDiscordBot();
    }

    private AxoDiscordBot() throws IOException {
        instance = this;

        if (this.settingsFile.exists())
            this.loadSettings();
        else {
            this.settings = new Settings();
            this.saveSettings();
            LOGGER.info("Created config. Please insert bot token.");
            System.exit(0);
        }

        this.guildSettingsManager = new FileSystemGuildSettingsManager();
        this.rateLimitManager = new DefaultRateLimitManager();
        this.commandManager = new DefaultCommandManager();

        this.commandManager.registerCommand(new AbstractCommand("ping", CommandLevel.ADMIN) {
            @Override
            public void execute(CommandContext commandContext) {
                commandContext.send("Pong!");
            }
        });

        this.commandManager.registerCommand(new ServerInfoCommand());
        this.commandManager.registerCommand(new InfoCommand());

        this.commandManager.registerCommand(new CleanCommand());

        Catnip.catnipAsync(this.settings.connectionSettings().token()).thenAccept(catnip -> {
            this.catnip = catnip;
            catnip.on(DiscordEvent.MESSAGE_CREATE, message -> {
                if(message.author().bot()) return;

                GuildSettings guildSettings = this.guildSettingsManager.getGuildSetting(message.guildId());

                if(guildSettings != null) {
                    if(guildSettings.modules().antiRaid() != null) {
                        if(!guildSettings.modules().antiRaid().handleMessage(message)) return;
                    }
                }

                commandManager.handle(message);
            });
            catnip.on(DiscordEvent.GUILD_MEMBER_ADD, member -> {
                GuildSettings guildSettings = this.guildSettingsManager.getGuildSetting(member.guildId());

                if(guildSettings != null) {
                    if(guildSettings.modules().utility() != null) {
                        guildSettings.modules().utility().handleGuildJoin(member);
                    }
                    if(guildSettings.modules().modLog() != null) {
                        guildSettings.modules().modLog().handleGuildJoin(member);
                    }
                }
            });
            catnip.on(DiscordEvent.CHANNEL_CREATE, channel -> {
                if(!channel.isGuild()) return;
                GuildSettings guildSettings = this.guildSettingsManager.getGuildSetting(channel.asGuildChannel().guildId());

                if(guildSettings != null) {
                    if(guildSettings.modules().modLog() != null) {
                        guildSettings.modules().modLog().handleChannelCreate(channel.asGuildChannel());
                    }
                }
            });
            catnip.on(DiscordEvent.VOICE_STATE_UPDATE, voiceState -> {
                GuildSettings guildSettings = this.guildSettingsManager.getGuildSetting(voiceState.guildId());

                if(guildSettings != null) {
                    if(guildSettings.modules().utility() != null) {
                        guildSettings.modules().utility().handleVoiceChatJoin(voiceState);
                    }
                }
            });
            catnip.connect();
        });
    }

    private void saveSettings() throws IOException {
        FileWriter fileWriter = new FileWriter(this.settingsFile);
        fileWriter.write(this.gson.toJson(this.settings));
        fileWriter.close();
    }

    private void loadSettings() throws FileNotFoundException {
        this.settings = this.gson.fromJson(new FileReader(this.settingsFile), Settings.class);
    }

    public Settings settings() {
        return settings;
    }
    
    public static AxoDiscordBot instance() {
        return instance;
    }

    public Catnip catnip() {
        return catnip;
    }

    public GuildSettingsManager guildSettingsManager() {
        return guildSettingsManager;
    }

    public RateLimitManager rateLimitManager() {
        return rateLimitManager;
    }
}
