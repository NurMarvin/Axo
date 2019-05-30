package de.nurmarvin.axo.command;

import de.nurmarvin.axo.utils.Embeds;

public final class CommandExceptions {
    public static void missingPermissions(CommandContext commandContext) {
        String noPermissionsMessage = commandContext.guildSettings().commands().noPermissionsMessage();
        if(commandContext.guildSettings().commands().useEmbeds())
            Embeds.commandException(commandContext, new CommandException(noPermissionsMessage));
        else commandContext.send(noPermissionsMessage);
    }
}
