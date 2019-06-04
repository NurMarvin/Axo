package de.nurmarvin.axo.module.modules.moderation.commands;

import com.mewna.catnip.entity.builder.EmbedBuilder;
import com.mewna.catnip.entity.user.User;
import com.mewna.catnip.rest.invite.InviteCreateOptions;
import de.nurmarvin.axo.command.AbstractCommand;
import de.nurmarvin.axo.command.CommandContext;
import de.nurmarvin.axo.command.CommandException;
import de.nurmarvin.axo.command.CommandLevel;
import de.nurmarvin.axo.utils.Embeds;
import gg.amy.catnip.utilities.FinderUtil;

import java.util.ArrayList;
import java.util.List;

public final class UnbanCommand extends AbstractCommand {
    public UnbanCommand() {
        super("unban", CommandLevel.MODERATOR);
        this.setUsage("<one or multiple ids separated by spaces> [Reason]");
        this.setDescription("Unbans one or multiple users with the given reason");
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void execute(CommandContext commandContext) throws CommandException {
        if(commandContext.argLength() < 1) {
            Embeds.usage(commandContext);
        } else {
            for (String id : commandContext.args()) {
                User user = new ArrayList<>(FinderUtil.findUsers(id, commandContext.catnip())).get(0);

                if(user == null) {
                    commandContext.guild().unban(id);
                    return;
                }

                user.createDM().handle((dmChannel, throwable) -> {
                    if(throwable != null) {
                        commandContext.guild().unban(user.idAsLong());
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
                                                              .title("Unban Information")
                                                              .description(String.format(
                                                                      "You've been unbanned from `%s`",
                                                                      commandContext.guild()
                                                                                    .name()))
                                                              .field("Issuer", issuer, true);

                            dmChannel.sendMessage(embedBuilder.build());
                        }

                        boolean reInvite = true;

                        if (reInvite) {
                            commandContext.catnip().rest().channel()
                                          .createInvite(commandContext.messageChannel().id(),
                                                        InviteCreateOptions.create().maxUses(1))
                                          .handle((createdInvite, throwable1) -> {
                                              if (throwable1 == null) {
                                                  dmChannel.sendMessage("Invite Link:\n" +
                                                                        "https://discord.gg/" +
                                                                        createdInvite.code());
                                              }
                                              return null;
                                          });
                        }
                    }
                    commandContext.guild().unban(user.idAsLong());
                    return null;
                });
            }

            if(commandContext.useEmbeds()) {
                EmbedBuilder embedBuilder = Embeds.normalEmbed(commandContext)
                                                  .title("Success")
                                                  .description(String.format("Unbanned `%s` user(s).",
                                                                             commandContext.args().length));

                commandContext.send(embedBuilder.build());
            }
        }
    }
}
