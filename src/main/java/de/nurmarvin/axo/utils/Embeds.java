package de.nurmarvin.axo.utils;


import com.besaba.revonline.pastebinapi.paste.PasteBuilder;
import com.besaba.revonline.pastebinapi.paste.PasteExpire;
import com.besaba.revonline.pastebinapi.paste.PasteVisiblity;
import com.besaba.revonline.pastebinapi.response.Response;
import com.mewna.catnip.entity.builder.EmbedBuilder;
import com.mewna.catnip.entity.guild.Guild;
import com.mewna.catnip.entity.guild.Member;
import com.mewna.catnip.entity.message.MessageOptions;
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
        String cutDownStackTrace = stackTrace.substring(0, Math.min(512, stackTrace.length()));
        String description = "Looks like Axo did an oopsie.\n" +
                             "This has been automatically reported to us but feel free to join our" +
                             " [support server](https://discord.gg/j2b6CTZ) if you want to.";

        String webHookId = AxoDiscordBot.instance().settings().exceptionWebHookId();
        String webHookToken = AxoDiscordBot.instance().settings().exceptionWebHookToken();

        if(!(webHookId.isEmpty() && webHookToken.isEmpty())) {
            EmbedBuilder embedBuilder = errorEmbed(context)
                    .title(e.getClass().getSimpleName())
                    .description("```" + cutDownStackTrace + "```")
                    .field("User ID", context.user().id(), true)
                    .field("Guild ID", context.guild().id(), true)
                    .field("Channel ID", context.messageChannel().id(), true)
                    .field("Message", context.message().content(), false);

            PasteBuilder pasteBuilder = AxoDiscordBot.instance().pastebinFactory().createPaste();

            pasteBuilder.setTitle(e.getClass().getSimpleName() + " - Axo Bot")
                        .setMachineFriendlyLanguage("text")
                        .setRaw(stackTrace)
                        .setVisiblity(PasteVisiblity.Unlisted)
                        .setExpire(PasteExpire.OneWeek);

            final Response<String> response = AxoDiscordBot.instance().pastebin()
                                                           .post(pasteBuilder.build());

            if(response.getError() == null) {
                embedBuilder = embedBuilder.field("Stacktrace", response.get(), false);
            }

            AxoDiscordBot.instance().catnip().rest().webhook()
                         .executeWebhook(webHookId, webHookToken, new MessageOptions().embed(embedBuilder.build()))
                         .thenAccept(ignored -> {});
        }

        EmbedBuilder embed = errorEmbed(context)
                .description(description);

        context.send(embed.build());
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
