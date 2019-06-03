package de.nurmarvin.axo.module.modules.utility.commands;

import com.mewna.catnip.cache.view.NamedCacheView;
import com.mewna.catnip.entity.builder.EmbedBuilder;
import com.mewna.catnip.entity.channel.Channel;
import com.mewna.catnip.entity.channel.GuildChannel;
import com.mewna.catnip.entity.guild.Member;
import com.mewna.catnip.entity.user.Presence;
import de.nurmarvin.axo.command.AbstractCommand;
import de.nurmarvin.axo.command.CommandContext;
import de.nurmarvin.axo.settings.GuildSettings;
import de.nurmarvin.axo.utils.DateUtils;
import de.nurmarvin.axo.utils.Embeds;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ServerInfoCommand extends AbstractCommand {
    public ServerInfoCommand() {
        super("serverinfo", "server");
    }

    @Override
    public void execute(CommandContext commandContext) {
        String guildName = commandContext.guild().name();

        // General Information
        String creationTime = DateUtils.humanize(commandContext.guild().creationTime());
        String creationDate = commandContext.guild().creationTime().toString();
        Member owner = commandContext.guild().owner();
        String ownerDiscordTag = owner.user().discordTag();
        String region = commandContext.guild().region();
        String features = String.join(", ", commandContext.guild().features());

        if(features.isEmpty()) features = "None";

        String generalInformation = String.format("Created: %s (%s)\n" +
                                                  "Name: %s\n" +
                                                  "Owner: %s\n" +
                                                  "Owner Profile: %s\n" +
                                                  "Region: %s\n" +
                                                  "Features: %s\n",
                                                  creationTime, creationDate,
                                                  guildName,
                                                  ownerDiscordTag,
                                                  owner.asMention(),
                                                  region,
                                                  features);

        //Counts
        String memberCount = String.valueOf(commandContext.guild().members().size());
        String roleCount = String.valueOf(commandContext.guild().roles().size());
        String emojiCount = String.valueOf(commandContext.guild().emojis().size());

        NamedCacheView<GuildChannel> guildChannels = commandContext.guild().channels();
        List<GuildChannel> textChannels = guildChannels.stream().filter(Channel::isText).collect(
                Collectors.toList());
        List<GuildChannel> voiceChannels = guildChannels.stream().filter(Channel::isVoice).collect(
                Collectors.toList());
        List<GuildChannel> categories = guildChannels.stream().filter(Channel::isCategory).collect(
                Collectors.toList());

        String textChannelCount = String.valueOf(textChannels.size());
        String voiceChannelCount = String.valueOf(voiceChannels.size());
        String categoryCount = String.valueOf(categories.size());

        String counts = String.format("Members: %s\n" +
                                      "Roles: %s\n" +
                                      "Emojis: %s\n" +
                                      "Text Channel: %s\n" +
                                      "Voice Channel: %s\n" +
                                      "Categories: %s\n",
                                      memberCount,
                                      roleCount,
                                      emojiCount,
                                      textChannelCount,
                                      voiceChannelCount,
                                      categoryCount);

        //Members

        NamedCacheView<Member> members = commandContext.guild().members();

        AtomicLong unknownCount = new AtomicLong();

        long onlineCount = members.stream().filter(member -> {
            Presence presence = member.user().presence();

            if(presence != null) {
                return presence.status() == Presence.OnlineStatus.ONLINE;
            } else {
                unknownCount.getAndIncrement();
            }
            return false;
        }).count();

        long dndCount = members.stream().filter(member -> {
            Presence presence = member.user().presence();

            if(presence != null) {
                return presence.status() == Presence.OnlineStatus.DND;
            }
            return false;
        }).count();

        long idleCount = members.stream().filter(member -> {
            Presence presence = member.user().presence();

            if(presence != null) {
                return presence.status() == Presence.OnlineStatus.IDLE;
            }
            return false;
        }).count();

        long offlineCount = members.stream().filter(member -> {
            Presence presence = member.user().presence();

            if(presence != null) {
                return presence.status() == Presence.OnlineStatus.OFFLINE
                       || presence.status() == Presence.OnlineStatus.INVISIBLE;
            }
            return false;
        }).count();

        GuildSettings.Emojis emojis = commandContext.guildSettings().emojis();

        String member = String.format("%s - %d Online\n" +
                                      "%s - %d Idle\n" +
                                      "%s - %d DND\n" +
                                      "%s - %d Offline\n" +
                                      "%s - %d Unknown (Not fetched)",
                                      emojis.onlineIconEmote(), onlineCount,
                                      emojis.idleIconEmote(), idleCount,
                                      emojis.dndIconEmote(), dndCount,
                                      emojis.offlineIconEmote(), offlineCount,
                                      emojis.unknownStatusEmote(), unknownCount.get());

        //Modules

        GuildSettings.Modules modules = commandContext.guildSettings().modules();

        String antiRaid = (modules.antiRaid() != null ? emojis.activatedEmote() :
                           emojis.deactivatedEmote()) + modules.antiRaid().name();

        String modLog = (modules.modLog() != null ? emojis.activatedEmote() :
                         emojis.deactivatedEmote()) + modules.modLog().name();

        String utility = (modules.utility() != null ? emojis.activatedEmote() :
                          emojis.deactivatedEmote()) + modules.utility().name();

        String moduleList = String.format("%s\n" +
                                          "%s\n" +
                                          "%s\n",
                                          antiRaid,
                                          modLog,
                                          utility);

        if(commandContext.useEmbeds()) {
            EmbedBuilder embedBuilder = Embeds
                    .normalEmbed(commandContext)
                    .thumbnail(commandContext.guild().iconUrl())
                    .title(String.format("Server Information for %s",
                                         guildName))
                    .field("General Information", generalInformation, false)
                    .field("Counts", counts, false)
                    .field("Members", member, false)
                    .field("Modules", moduleList, false);
            commandContext.send(embedBuilder.build());
        } else {
            commandContext.send(String.format("**Server Information for %s**\n" +
                                              "\n" +
                                              "**General Information**\n" +
                                              "%s\n" +
                                              "**Counts**\n" +
                                              "%s\n" +
                                              "**Members**\n" +
                                              "%s\n" +
                                              "**Modules**\n" +
                                              "%s",
                                              guildName,
                                              generalInformation,
                                              counts,
                                              member,
                                              moduleList));
        }
    }
}
