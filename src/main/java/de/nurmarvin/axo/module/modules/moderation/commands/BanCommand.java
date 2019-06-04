package de.nurmarvin.axo.module.modules.moderation.commands;

import com.google.common.collect.Lists;
import com.mewna.catnip.entity.builder.EmbedBuilder;
import com.mewna.catnip.entity.user.User;
import de.nurmarvin.axo.command.AbstractCommand;
import de.nurmarvin.axo.command.CommandContext;
import de.nurmarvin.axo.command.CommandException;
import de.nurmarvin.axo.command.CommandLevel;
import de.nurmarvin.axo.utils.Embeds;
import gg.amy.catnip.utilities.FinderUtil;

import java.util.ArrayList;
import java.util.List;

public final class BanCommand extends AbstractCommand {
    public BanCommand() {
        super("ban", CommandLevel.MODERATOR);
        this.setUsage("<one or multiple users/members/ids/names separated by spaces> [Reason]");
        this.setDescription("Bans one or multiple users with the given reason");
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void execute(CommandContext commandContext) throws CommandException {
        if(commandContext.argLength() < 1) {
            Embeds.usage(commandContext);
        } else {
            List<User> users = new ArrayList<>();
            StringBuilder reason = new StringBuilder();

            for(int i = 0; i < commandContext.argLength(); i++) {
                ArrayList<User> usersFoundForArg = new ArrayList<>(
                        FinderUtil.findUsers(commandContext.arg(i).asString(), commandContext.catnip()));

                if(usersFoundForArg.size() < 1) {
                    if(commandContext.argLength() > i) {
                        reason.append(commandContext.skipConcatArgs(i));
                    }
                    break;
                }
                users.add(usersFoundForArg.get(0));
            }

            if(reason.toString().equals("")) reason.append("Undefined");

            for (User user : users) {
                user.createDM().handle((dmChannel, throwable) -> {
                    if(throwable != null) {
                        commandContext.guild().ban(user.idAsLong(), reason.toString(), 7);
                        return null;
                    }

                    //TODO: Check if users should be informed about infringements
                    boolean sendDM = true;

                    //TODO: Check if users should be informed about the issuer
                    String issuer = String.format("%s (%s)", commandContext.member().asMention(),
                                                  commandContext.member().user().discordTag());

                    if(sendDM) {
                        if (commandContext.useEmbeds()) {
                            EmbedBuilder embedBuilder = Embeds.normalEmbed(commandContext.guild())
                                                              .title("Ban Information")
                                                              .description(String.format(
                                                                      "You've been banned from `%s`",
                                                                      commandContext.guild()
                                                                                    .name()))
                                                              .field("Reason", reason.toString(),
                                                                     true)
                                                              .field("Issuer", issuer, true);

                            dmChannel.sendMessage(embedBuilder.build());
                        }
                    }
                    commandContext.guild().ban(user.idAsLong(), reason.toString(), 7);
                    return null;
                });
            }

            if(commandContext.useEmbeds()) {
                EmbedBuilder embedBuilder = Embeds.normalEmbed(commandContext)
                                                  .title("Success")
                                                  .description(String.format("Banned `%s` user(s) for reason `%s`.",
                                                                             users.size(), reason));

                commandContext.send(embedBuilder.build());
            }
        }
    }
}
