package de.nurmarvin.axo.command;

import com.mewna.catnip.entity.Snowflake;
import com.mewna.catnip.entity.channel.MessageChannel;
import com.mewna.catnip.entity.guild.Guild;
import com.mewna.catnip.entity.guild.Member;
import com.mewna.catnip.entity.message.Embed;
import com.mewna.catnip.entity.message.Message;
import com.mewna.catnip.entity.user.User;
import de.nurmarvin.axo.AxoDiscordBot;
import de.nurmarvin.axo.settings.GuildSettings;
import de.nurmarvin.axo.settings.Settings;
import gg.amy.catnip.utilities.FinderUtil;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public final class CommandContext {
    private Message message;
    private AbstractCommand command;
    private final String aliasUsed;
    private final String concatArgs;
    private final String[] args;

    public CommandContext(Message message, AbstractCommand command, String aliasUsed, String[] args,
                          String concatArgs) {
        this.message = message;
        this.command = command;
        this.aliasUsed = aliasUsed;
        this.args = args;
        this.concatArgs = concatArgs;
    }

    @Nonnull
    public AbstractCommand command() {
        return command;
    }

    @Nonnull
    public User user() {
        return this.member().user();
    }

    @Nonnull
    public Member member() {
        return Objects.requireNonNull(this.message.member(), "Member can not be null");
    }

    @Nonnull
    public User selfUser() {
        return Objects.requireNonNull(this.message.catnip().selfUser(), "Self User can not be null");
    }

    @Nonnull
    public Member selfMember() {
        return Objects.requireNonNull(this.guild().selfMember(), "WTF");
    }

    @Nonnull
    public Guild guild() {
        return Objects.requireNonNull(this.message.guild(), "Guild can not be null");
    }

    @Nonnull
    public MessageChannel messageChannel() {
        return Objects.requireNonNull(this.message.channel(), "Message Channel can not be null");
    }

    public Message message() {
        return message;
    }

    @Nonnull
    public String aliasUsed() {
        return aliasUsed;
    }

    @Nonnull
    public String[] args() {
        return args;
    }

    @CheckReturnValue
    public CommandArg arg(int arg) throws CommandException {
        if(arg > args.length) throw new CommandException("Not enough arguments");
        return new CommandArg(this.args[arg], this.guild());
    }

    @Nonnull
    public boolean hasArgs() {
        return !concatArgs.isEmpty();
    }

    @Nonnull
    public String skipConcatArgs(int count) {
        return Arrays.stream(args).skip(count).collect(Collectors.joining(" "));
    }

    public void send(String message) {
        this.messageChannel().sendMessage(message);
    }

    public void send(Embed embed) {
        this.messageChannel().sendMessage(embed);
    }

    public boolean hasPermissions() {
        AtomicInteger highestLevel = new AtomicInteger();

        this.member().roles().stream().map(Snowflake::id).forEach(id -> {
            if(this.guildSettings().levels().containsKey(id)) {
                int level = this.guildSettings().levels().get(id);

                if(level > highestLevel.get()) highestLevel.set(level);
            }
        });

        int requiredLevel = this.command.requiredLevel();

        //TODO: Implement overrides

        return requiredLevel == 0 || highestLevel.get() > requiredLevel;
    }

    @Nonnull
    public Settings settings() {
        return Objects.requireNonNull(AxoDiscordBot.instance().settings(), "Settings can not be null");
    }

    @Nonnull
    public GuildSettings guildSettings() {
        return Objects.requireNonNull(AxoDiscordBot.instance().guildSettingsManager()
                                                   .getGuildSetting(this.guild().id()),
                                      "Guild Settings not defined");
    }

    public boolean useEmbeds() {
        //TODO: Add command override support
        return this.guildSettings().commands().useEmbeds();
    }

    public class CommandArg {
        private Guild guild;
        private String value;

        public CommandArg(String value, Guild guild) {
            this.value = value.replace("`", "\\u0060");
            this.guild = guild;
        }

        public String asString() {
            return value;
        }

        public Integer asInteger() throws CommandException {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new CommandException(String.format("Argument `%s` must be a number between %s and %s.", value,
                                                         Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
        }

        public Long asLong() throws CommandException {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                throw new CommandException(String.format("Argument `%s` must be a number between %s and %s.", value,
                                                         Long.MIN_VALUE, Long.MAX_VALUE));
            }
        }

        public Member asMember() throws CommandException {
            try {
                return FinderUtil.findMembers(value, guild).stream().findFirst()
                                 .orElseThrow(Exception::new);
            } catch (Exception e) {
                throw new CommandException(String.format("Can't find server member by input `%s`.", value));
            }
        }

        public Double asDouble() throws CommandException {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                throw new CommandException(String.format("Argument `%s` must be a number between %s and %s.", value,
                                                         Double.MIN_VALUE, Double.MAX_VALUE));
            }
        }

        public Object asObject() {
            return value;
        }
    }
}
