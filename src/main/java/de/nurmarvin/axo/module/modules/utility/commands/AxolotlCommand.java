package de.nurmarvin.axo.module.modules.utility.commands;

import com.mewna.catnip.entity.builder.EmbedBuilder;
import de.nurmarvin.axo.command.AbstractCommand;
import de.nurmarvin.axo.command.CommandContext;
import de.nurmarvin.axo.command.CommandException;
import de.nurmarvin.axo.utils.AxolotlAPI;
import de.nurmarvin.axo.utils.Embeds;

public class AxolotlCommand extends AbstractCommand {
    public AxolotlCommand() {
        super("axolotl");
        this.setDescription("Shows you a cute axolotl");
        this.setUsage("axolotl");
    }

    @Override
    public void execute(CommandContext commandContext) throws CommandException {
        if(commandContext.useEmbeds()) {
            EmbedBuilder embedBuilder = Embeds.normalEmbed(commandContext)
                    .title("Here's an axolotl <:Axolotl:585198887984431119>")
                    .image(AxolotlAPI.randomAxolotl());

            commandContext.send(embedBuilder.build());
        }
    }
}
