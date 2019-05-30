package de.nurmarvin.axo.manager.impl;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.mewna.catnip.entity.channel.Channel;
import com.mewna.catnip.entity.message.Message;
import de.nurmarvin.axo.AxoDiscordBot;
import de.nurmarvin.axo.command.AbstractCommand;
import de.nurmarvin.axo.command.Command;
import de.nurmarvin.axo.command.CommandContext;
import de.nurmarvin.axo.command.CommandException;
import de.nurmarvin.axo.manager.CommandManager;
import de.nurmarvin.axo.settings.GuildSettings;
import de.nurmarvin.axo.utils.Embeds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class DefaultCommandManager implements CommandManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
    private Map<String, AbstractCommand> commandMap;

    public DefaultCommandManager() {
        this.commandMap = Maps.newHashMap();
    }

    @Override
    public void registerCommand(AbstractCommand command) {
        if (command == null) return;

        List<String> aliases = Arrays.asList(command.aliases());

        commandMap.put(command.name().toLowerCase(), command);
        aliases.forEach(alias -> commandMap.put(alias, command));

        LOGGER.info("Registered command: {} -> {}", command.name(), command);
    }

    @Override
    public void handle(Message message) {
        if (message.channel().type() != Channel.ChannelType.TEXT || !message.channel().isGuild()) return;

        GuildSettings guildSettings = AxoDiscordBot.instance().guildSettingsManager().getGuildSetting(message.guildId());
        String content = message.content();

        if(guildSettings == null) {
            if(content.equalsIgnoreCase(Objects.requireNonNull(message.catnip().selfUser()).asMention() + " setup")) {
                AxoDiscordBot.instance().guildSettingsManager().createNewSettings(message.guildId());
                message.channel().sendMessage(String.format("✅ Successfully created a new guild" +
                                                            " config for `%s`!", Objects
                                                                    .requireNonNull(message.guild())
                                                                    .name()));
            }
            return;
        }

        if(content.startsWith(guildSettings.commands().prefix())) {
            content = content.trim().substring(guildSettings.commands().prefix().length()).trim();
            handle(message, content);
        }
    }

    private void handle(Message message, String content) {
        List<String> parts = Splitter.on(CharMatcher.breakingWhitespace()).splitToList(content);

        if(!parts.isEmpty()) {
            AbstractCommand command = commandMap.get(parts.get(0).toLowerCase());

            if(command != null) {
                dispatch(createContext(parts, command, message));
            }
        }
    }

    private void dispatch(CommandContext context) {
        Message message = context.message();

        LOGGER.info("User {}#{} ({}) [{}({})/{}-{}] invoked command: {}",
                 message.author().username(), message.author().discriminator(),
                 message.author().id(), context.guild().name(), message.guildId(),
                 message.channel().id(), message.id(), message.content());

        try {
            context.command().preExecute(context);
        }
        catch (CommandException e) {
            Embeds.commandException(context, e);
        } catch (Exception e) {
            LOGGER.error("Caught error while executing command!", e);
            Embeds.exception(context, e);
        }
    }

    private CommandContext createContext(List<String> parts, AbstractCommand command, Message message) {
        String[] args = parts.stream().skip(1).toArray(String[]::new);
        String concatArgs = Joiner.on(" ").join(args);

        return new CommandContext(message, command, parts.get(0).toLowerCase(), args, concatArgs);
    }

    @Override
    public Map<String, AbstractCommand> commands() {
        return commandMap;
    }
}
