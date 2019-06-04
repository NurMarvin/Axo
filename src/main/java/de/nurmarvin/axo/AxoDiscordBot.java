package de.nurmarvin.axo;

import com.besaba.revonline.pastebinapi.Pastebin;
import com.besaba.revonline.pastebinapi.impl.factory.PastebinFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mewna.catnip.Catnip;
import com.mewna.catnip.entity.user.Presence;
import com.mewna.catnip.shard.DiscordEvent;
import de.nurmarvin.axo.command.AbstractCommand;
import de.nurmarvin.axo.command.CommandContext;
import de.nurmarvin.axo.command.CommandException;
import de.nurmarvin.axo.command.CommandLevel;
import de.nurmarvin.axo.manager.CommandManager;
import de.nurmarvin.axo.manager.GuildSettingsManager;
import de.nurmarvin.axo.manager.JoinManager;
import de.nurmarvin.axo.manager.RateLimitManager;
import de.nurmarvin.axo.manager.impl.DefaultCommandManager;
import de.nurmarvin.axo.manager.impl.DefaultJoinManager;
import de.nurmarvin.axo.manager.impl.DefaultRateLimitManager;
import de.nurmarvin.axo.manager.impl.FileSystemGuildSettingsManager;
import de.nurmarvin.axo.module.modules.moderation.commands.BanCommand;
import de.nurmarvin.axo.module.modules.moderation.commands.CleanCommand;
import de.nurmarvin.axo.module.modules.moderation.commands.UnbanCommand;
import de.nurmarvin.axo.module.modules.utility.commands.AxolotlCommand;
import de.nurmarvin.axo.module.modules.utility.commands.InfoCommand;
import de.nurmarvin.axo.module.modules.utility.commands.ServerInfoCommand;
import de.nurmarvin.axo.settings.GuildSettings;
import de.nurmarvin.axo.settings.Settings;
import io.github.classgraph.utils.Join;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class AxoDiscordBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(AxoDiscordBot.class);

    public static final String VERSION = "1.0-DEV";
    private static AxoDiscordBot instance;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File settingsFile = new File("settings.json");
    private Settings settings;
    private Catnip catnip;
    private PastebinFactory pastebinFactory;
    private Pastebin pastebin;

    private JoinManager joinManager;
    private CommandManager commandManager;
    private GuildSettingsManager guildSettingsManager;
    private RateLimitManager rateLimitManager;

    /**
     * The main method that starts the entire bot
     * @param args Command line arguments, unused
     * @throws IOException Thrown when there's an error with trying to create/read the settings file
     * which in most cases won't happen unless there's insufficient w/r permissions present.
     */
    public static void main(String[] args) throws IOException {
        new AxoDiscordBot();
    }

    /**
     * The constructor that initializes the entire bot
     * @throws IOException Thrown when there's an error with trying to create/read the settings file
     * which in most cases won't happen unless there's insufficient w/r permissions present.
     */
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

        this.joinManager = new DefaultJoinManager();
        this.guildSettingsManager = new FileSystemGuildSettingsManager();
        this.rateLimitManager = new DefaultRateLimitManager();
        this.commandManager = new DefaultCommandManager();

        this.commandManager.registerCommand(new AbstractCommand("ping", CommandLevel.ADMIN) {
            @Override
            public void execute(CommandContext commandContext) {
                commandContext.send("Pong!");
            }
        });

        if(this.settings.apiKeys().containsKey("pastebin")) {
            this.pastebinFactory = new PastebinFactory();
            this.pastebin = pastebinFactory.createPastebin(this.settings.apiKeys().get("pastebin"));
            LOGGER.info("Added Pastebin error logging");
        }

        //Moderation Commands
        this.commandManager.registerCommand(new CleanCommand());
        this.commandManager.registerCommand(new BanCommand());
        this.commandManager.registerCommand(new UnbanCommand());

        //Utility Commands
        this.commandManager.registerCommand(new ServerInfoCommand());
        this.commandManager.registerCommand(new InfoCommand());
        this.commandManager.registerCommand(new AxolotlCommand());

        Catnip.catnipAsync(this.settings.connectionSettings().token()).thenAccept(catnip -> {
            this.catnip = catnip;

            catnip.on(DiscordEvent.READY, ready -> {
                updatePresence(ready.guilds().size(), ready.shardCount());

                Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                    this.catnip.fetchGatewayInfo().thenAccept(gatewayInfo -> {
                        int shardCount = gatewayInfo.shards();
                        this.catnip.rest().user().getCurrentUserGuilds().fetch().thenAccept(partialGuilds -> {
                            updatePresence(partialGuilds.size(), shardCount);
                        });
                    });
                }, 30, 30, TimeUnit.SECONDS);
            });

            catnip.on(DiscordEvent.MESSAGE_CREATE, message -> {
                if(message.author().bot()) return;

                commandManager.handle(message);
            });
            catnip.on(DiscordEvent.GUILD_MEMBER_ADD, member -> {
                GuildSettings guildSettings = this.guildSettingsManager.getGuildSetting(member.guildId());

                if(guildSettings != null) {
                    if(guildSettings.modules().modLog() != null) {
                        guildSettings.modules().modLog().handleGuildJoin(member);
                    }
                    if(guildSettings.modules().antiRaid() != null) {
                        if(guildSettings.modules().antiRaid().handleGuildJoin(member)) return;
                    }
                    if(guildSettings.modules().utility() != null) {
                        guildSettings.modules().utility().handleGuildJoin(member);
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

    /**
     * Saves the current {@link #settings} object to the {@link #settingsFile}
     * @throws IOException Thrown if there's insufficient write permissions present.
     */
    private void saveSettings() throws IOException {
        FileWriter fileWriter = new FileWriter(this.settingsFile);
        fileWriter.write(this.gson.toJson(this.settings));
        fileWriter.close();
    }

    /**
     * Loads the {@link Settings} into the {@link #settings} object from the {@link #settingsFile}
     * @throws FileNotFoundException Thrown if there's insufficient read permissions present or the file couldn't be found.
     */
    private void loadSettings() throws FileNotFoundException {
        this.settings = this.gson.fromJson(new FileReader(this.settingsFile), Settings.class);
    }

    /**
     * Updates the Discord Presence with the given guild and shard count
     * @param guildCount the count of guilds the bot is running on
     * @param shardCount the count of shards the bot is running on
     */
    private void updatePresence(int guildCount, int shardCount) {
        String guilds = guildCount + " guild" + (guildCount != 1 ? "s" : "");
        String shards = shardCount + " shard" + (shardCount != 1 ? "s" : "");
        catnip.presence(Presence.of(Presence.OnlineStatus.ONLINE, Presence.Activity.of(
                String.format("with %s on %s", guilds, shards), Presence.ActivityType.PLAYING)));
    }

    /**
     * The current settings
     * @return the {@link #settings} object
     */
    public Settings settings() {
        return settings;
    }

    /**
     * The instance of the bot
     * @return the {@link #instance} object
     */
    public static AxoDiscordBot instance() {
        return instance;
    }

    /**
     * The {@link Catnip} instance of the bot
     * @return the {@link #catnip} object
     */
    public Catnip catnip() {
        return catnip;
    }

    public PastebinFactory pastebinFactory() {
        return pastebinFactory;
    }

    public Pastebin pastebin() {
        return pastebin;
    }

    public JoinManager joinManager() {
        return joinManager;
    }

    public GuildSettingsManager guildSettingsManager() {
        return guildSettingsManager;
    }

    public RateLimitManager rateLimitManager() {
        return rateLimitManager;
    }
}
