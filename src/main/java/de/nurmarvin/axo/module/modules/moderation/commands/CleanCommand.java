package de.nurmarvin.axo.module.modules.moderation.commands;

import com.mewna.catnip.entity.Snowflake;
import com.mewna.catnip.entity.builder.EmbedBuilder;
import com.mewna.catnip.entity.guild.Member;
import com.mewna.catnip.entity.message.Message;
import de.nurmarvin.axo.command.AbstractCommand;
import de.nurmarvin.axo.command.CommandContext;
import de.nurmarvin.axo.command.CommandException;
import de.nurmarvin.axo.command.CommandLevel;
import de.nurmarvin.axo.utils.Embeds;

import java.util.List;
import java.util.stream.Collectors;

public class CleanCommand extends AbstractCommand {
    public CleanCommand() {
        super("clean", CommandLevel.MODERATOR, "clear", "purge", "prune");
        this.setUsage("@member|all|bots amount");
        this.setDescription("Clears messages from a specific member, everyone or just bots with a limit of 100 messages");
    }

    @Override
    public void execute(CommandContext commandContext) throws CommandException {
        if(commandContext.args().length > 1) {
            int messageCount = commandContext.arg(1).asInteger();

            if(messageCount > 100) messageCount = 100;

            switch (commandContext.arg(0).asString().toUpperCase()) {
                case "ALL": {
                    commandContext.messageChannel().fetchMessages().limit(messageCount).fetch().thenAccept(messages -> {
                        List<String> messageIds = messages.stream().map(Snowflake::id).collect(
                                Collectors.toList());

                        commandContext.messageChannel().catnip().rest().channel()
                                      .deleteMessages(commandContext.messageChannel().id(), messageIds);

                        String message = String.format("Deleted %d messages for you!", messageIds.size());

                        if(commandContext.useEmbeds()) {
                            EmbedBuilder embedBuilder = Embeds
                                    .normalEmbed(commandContext)
                                    .title("Success")
                                    .description(message);
                            commandContext.send(embedBuilder.build());
                        } else {
                            commandContext.send(message);
                        }
                    });
                    break;
                }
                case "BOTS": {
                    throw new CommandException("Not implemented yet");
                }
                default: {
                    Member member = commandContext.arg(0).asMember();

                    throw new CommandException("Not implemented yet");
                }
            }
        } else {
            if(commandContext.guildSettings().commands().usageFeedback()) {
                Embeds.usage(commandContext);
            }
        }
    }
}
