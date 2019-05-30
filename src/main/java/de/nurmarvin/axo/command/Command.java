package de.nurmarvin.axo.command;

public interface Command {
    String name();
    int requiredLevel();
    String[] aliases();
    String description();

    void execute(CommandContext commandContext) throws CommandException;
}
