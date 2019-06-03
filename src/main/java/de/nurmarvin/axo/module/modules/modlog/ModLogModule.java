package de.nurmarvin.axo.module.modules.modlog;

import com.google.common.collect.Maps;
import com.mewna.catnip.entity.builder.EmbedBuilder;
import com.mewna.catnip.entity.channel.GuildChannel;
import com.mewna.catnip.entity.channel.TextChannel;
import com.mewna.catnip.entity.guild.Member;
import de.nurmarvin.axo.module.Module;
import de.nurmarvin.axo.utils.DateUtils;
import de.nurmarvin.axo.utils.Embeds;

import java.util.Map;
import java.util.Objects;


public class ModLogModule implements Module {

    private Map<String, ModLogChannel> channels = Maps.newHashMap();

    @Override
    public String name() {
        return "ModLog";
    }

    public void handleGuildJoin(Member member) {
        if(channels == null) return;

        channels.forEach((channelId, modLogChannel) -> {
            TextChannel textChannel = member.guild().textChannel(channelId);

            if(textChannel == null || !logs(modLogChannel, ModLogActions.GUILD_MEMBER_ADD)) return;

            String discordTag = member.user().discordTag();
            String asMention = member.asMention();
            String creationDate = member.creationTime().toString();
            String creationTime = DateUtils.humanize(member.creationTime());

            String creationString = String.format("%s (%s)", creationTime, creationDate);

            if(modLogChannel.useEmbeds()) {
                EmbedBuilder embedBuilder = Embeds.normalEmbed(member)
                                                  .author("Member Joined", null, member.user().effectiveAvatarUrl())
                                                  .description(asMention + " " + discordTag)
                                                  .field("Created", creationString, false)
                                                  .thumbnail(member.user().effectiveAvatarUrl());

                textChannel.sendMessage(embedBuilder.build());
            } else {
                System.out.println("gay");
            }
        });
    }

    private boolean logs(ModLogChannel modLogChannel, ModLogActions modLogAction) {
        if(modLogChannel.include() != null && modLogChannel.include().contains(modLogAction)) return true;
        if(modLogChannel.exclude() != null && modLogChannel.exclude().contains(modLogAction)) return false;

        return (modLogChannel.include() == null && modLogChannel.exclude() == null) ||
               (modLogChannel.include().size() == 0 && modLogChannel.exclude().size() == 0);
    }

    public void handleChannelCreate(GuildChannel channel) {
        if(channels == null) return;

        channels.forEach((channelId, modLogChannel) -> {
            TextChannel textChannel = channel.guild().textChannel(channelId);

            if (textChannel == null || !logs(modLogChannel, ModLogActions.CHANNEL_CREATE)) return;

            String name = channel.name();
            String asMention = channel.isText() ? channel.asTextChannel().asMention() + " ": "";
            String id = channel.id();
            String type = channel.isCategory() ? "Category" : (channel.isText() ? "Text" : "Voice") +
                                                              " Channel";
            String parent = channel.parentId() != null ? Objects
                    .requireNonNull(channel.guild().category(channel.parentId())).name() : "";
            String parentId = channel.parentId();

            if(modLogChannel.useEmbeds()) {
                EmbedBuilder embedBuilder = Embeds.normalEmbed(channel.guild())
                                                  .title("Channel Created")
                                                  .description(asMention + name)
                                                  .field("ID", id, true)
                                                  .field("Type", type, true);

                if(parentId != null) {
                    embedBuilder = embedBuilder
                            .field("Parent", String.format("%s (%s)", parent, parentId), false);
                }

                textChannel.sendMessage(embedBuilder.build());
            }
        });
    }

    public void handleRaid(Member member, int joins, int refreshTime) {
        if(channels == null) return;

        channels.forEach((channelId, modLogChannel) -> {
            TextChannel textChannel = member.guild().textChannel(channelId);

            if (textChannel == null || !logs(modLogChannel, ModLogActions.RAID_DETECTED)) return;

            String message = String.format("%s joins in under %s seconds reached!", joins, refreshTime);

            if(modLogChannel.useEmbeds()) {
                EmbedBuilder embedBuilder = Embeds.normalEmbed(member.guild())
                                                  .title("Raid detected")
                                                  .description(message);
                textChannel.sendMessage(embedBuilder.build());
            } else {
                textChannel.sendMessage("**Raid Detected**\n\n" + message);
            }
        });
    }
}
