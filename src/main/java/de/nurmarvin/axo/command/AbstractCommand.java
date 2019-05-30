package de.nurmarvin.axo.command;

public abstract class AbstractCommand implements Command {
    private String name;
    private int requiredLevel;
    private String description;
    private String[] aliases;
    private String usage;

    public AbstractCommand(String name, String... aliases) {
        this(name, CommandLevel.USER, aliases);
    }

    public AbstractCommand(String name, CommandLevel commandLevel, String... aliases) {
        this(name, commandLevel == CommandLevel.ADMIN ? 100 :
                   (commandLevel == CommandLevel.MODERATOR ? 50 :
                    (commandLevel == CommandLevel.TRUSTED ? 10 : 0)), aliases);
    }

    public AbstractCommand(String name, int requiredLevel, String... aliases) {
        this.name = name;
        this.requiredLevel = requiredLevel;
        this.description = "command.description.not-set";
        this.usage = "";
        this.aliases = aliases;
    }

    public void preExecute(final CommandContext commandContext) throws CommandException {
        if(!commandContext.hasPermissions()) {
            if(commandContext.guildSettings().commands().noPermissionsFeedback())
                CommandExceptions.missingPermissions(commandContext);
            return;
        }
        execute(commandContext);
    }

    @Override
    public int requiredLevel() {
        return requiredLevel;
    }

    public String usage(CommandContext commandContext) {
        return commandContext.guildSettings().commands().prefix()
               + commandContext.aliasUsed() + (!this.usage.isEmpty() ? " " + this.usage : "") + " - " + this.description;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String name() {
        return name;
    }

    public String[] aliases() {
        return aliases;
    }

    public String description() {
        return description;
    }

    public abstract void execute(CommandContext commandContext) throws CommandException;
}
