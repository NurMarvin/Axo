package de.nurmarvin.axo.module.modules.utility.commands;

import com.mewna.catnip.entity.builder.EmbedBuilder;
import com.mewna.catnip.entity.guild.Member;
import com.mewna.catnip.entity.guild.Role;
import de.nurmarvin.axo.command.AbstractCommand;
import de.nurmarvin.axo.command.CommandContext;
import de.nurmarvin.axo.command.CommandException;
import de.nurmarvin.axo.settings.GuildSettings;
import de.nurmarvin.axo.utils.DateUtils;
import de.nurmarvin.axo.utils.Embeds;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InfoCommand extends AbstractCommand {
    public InfoCommand() {
        super("info", "userinfo");
    }

    @Override
    public void execute(CommandContext commandContext) throws CommandException {
        Member member = commandContext.member();

        if(commandContext.hasArgs()) {
            member = commandContext.arg(0).asMember();
        }

        GuildSettings.Emojis emojis = commandContext.guildSettings().emojis();

        String discordTag = member.user().discordTag();

        //User Information
        String id = member.id();
        String profile = member.asMention();
        String creationDate = member.creationTime().toString();
        String creationTime = offsetDateTimeToString(member.creationTime());
        String status = emojis.unknownStatusEmote() + " Unknown";


        if(member.user().presence() != null) {
            switch (Objects.requireNonNull(member.user().presence()).status()) {
                case ONLINE: {
                    status = emojis.onlineIconEmote() + " Online";
                    break;
                }
                case IDLE: {
                    status = emojis.idleIconEmote() + " Idle";
                    break;
                }
                case DND: {
                    status = emojis.dndIconEmote() + " DND";
                    break;
                }
                case OFFLINE: {
                    status = emojis.offlineIconEmote() + " Offline";
                    break;
                }
            }
        }

        String userInformation = String.format("ID: %s\n" +
                                               "Profile: %s\n" +
                                               "Created: %s (%s)\n" +
                                               "Status: %s\n",
                                               id,
                                               profile,
                                               creationTime, creationDate,
                                               status);

        //Member Information
        String joinDate = member.joinedAt() == null ? "Unknown" : Objects
                .requireNonNull(member.joinedAt()).toString();
        String joinTime = offsetDateTimeToString(member.joinedAt());
        List<Role> roles = member.orderedRoles().stream().sorted(Collections.reverseOrder()).collect(
                Collectors.toList());

        String roleList = roles.stream().map(Role::asMention).collect(Collectors.joining(" "));

        String memberInformation = String.format("Joined: %s (%s)\n" +
                                                 "Roles (%s): %s\n",
                                                 joinTime, joinDate,
                                                 roles.size(), roleList);

        if(commandContext.useEmbeds()) {
            EmbedBuilder embedBuilder = Embeds
                    .normalEmbed(commandContext)
                    .author("User Information for " + discordTag, null,  member.user().effectiveAvatarUrl())
                    .thumbnail(member.user().effectiveAvatarUrl())
                    .field("User Information", userInformation, false)
                    .field("Member Information", memberInformation, false);

            commandContext.send(embedBuilder.build());
        } else {
            commandContext.send(String.format("**User Information for %s**\n" +
                                              "\n" +
                                              "**User Information**\n" +
                                              "%s\n" +
                                              "**Member Information**\n" +
                                              "%s",
                                              discordTag,
                                              userInformation,
                                              memberInformation));
        }
    }

    private String offsetDateTimeToString(OffsetDateTime offsetDateTime) {
        return DateUtils.humanize(offsetDateTime);
    }
}
