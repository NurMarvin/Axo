package de.nurmarvin.axo.utils;

import com.mewna.catnip.entity.builder.EmbedBuilder;
import com.mewna.catnip.entity.guild.Guild;
import com.mewna.catnip.entity.guild.Member;
import com.mewna.catnip.entity.message.Embed;
import com.mewna.catnip.entity.user.User;
import com.mewna.catnip.util.CatnipMeta;
import de.nurmarvin.axo.AxoDiscordBot;
import de.nurmarvin.axo.command.CommandContext;
import de.nurmarvin.axo.settings.GuildSettings;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.awt.*;
import java.time.Instant;

public final class Embeds {
    public static void usage(CommandContext context) {
        StringBuilder description = new StringBuilder()
                .append(context.command().description())
                .append("\n\n`")
                .append(context.command().usage(context));

        description.append("`");

        EmbedBuilder usage = errorEmbed(context)
                .title("Command Usage: " + context.command().name())
                .description(description.toString());
        context.send(usage.build());
    }

    public static void commandException(CommandContext context, Exception e) {
        EmbedBuilder usage = errorEmbed(context)
                .description(e.getMessage());
        context.send(usage.build());
    }

    public static void exception(CommandContext context, Exception e) {
        String stackTrace = ExceptionUtils.getStackTrace(e);
        EmbedBuilder usage = errorEmbed(context)
                .description(stackTrace.substring(0, Math.min(2048, stackTrace.length())));
        context.send(usage.build());
    }

    public static EmbedBuilder errorEmbed(CommandContext commandContext) {
        return new EmbedBuilder()
                .color(commandContext.guildSettings().embeds().errorColor())
                .title("Error")
                .timestamp(commandContext.guildSettings().embeds().showTimestamp() ? Instant.now() : null)
                .footer(commandContext.guildSettings().embeds().footerMessage()
                                      .replace("%VERSION%", AxoDiscordBot.VERSION), null);
    }

    public static EmbedBuilder normalEmbed(Guild guild) {
        return normalEmbed(guild.selfMember());
    }

    public static EmbedBuilder normalEmbed(CommandContext commandContext) {
        return normalEmbed(commandContext.member(), commandContext.guildSettings());
    }

    public static EmbedBuilder normalEmbed(Member member) {
        return normalEmbed(member, AxoDiscordBot.instance().guildSettingsManager()
                                                .getGuildSetting(member.guildId()));
    }

    private static EmbedBuilder normalEmbed(Member member, GuildSettings guildSettings) {
        return new EmbedBuilder()
                .color(normalColor(member, guildSettings))
                .timestamp(guildSettings.embeds().showTimestamp() ? Instant.now() : null)
                .footer(guildSettings.embeds().footerMessage() != null ?
                        guildSettings.embeds().footerMessage().replace("%VERSION%",
                                                                       AxoDiscordBot.VERSION) : null,
                        null);
    }

    private static Color normalColor(CommandContext commandContext) {
        return normalColor(commandContext.member(), commandContext.guildSettings());
    }

    private static Color normalColor(Member member, GuildSettings guildSettings) {
        Color color = guildSettings.embeds().normalColor();

        if(guildSettings.embeds().useRoleColor()) {
            return member.color() != null ? member.color() : color;
        }

        return color;
    }

    private static Color normalColor(Member member) {
        return normalColor(member, AxoDiscordBot.instance().guildSettingsManager()
                                                .getGuildSetting(member.guildId()));
    }
}
