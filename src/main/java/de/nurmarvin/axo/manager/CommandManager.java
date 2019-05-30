package de.nurmarvin.axo.manager;

import com.mewna.catnip.entity.message.Message;
import de.nurmarvin.axo.command.AbstractCommand;
import de.nurmarvin.axo.command.Command;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Map;

public interface CommandManager {
    default void handle(Message message) {
        throw new NotImplementedException("Command Manager does not implement CommandManager#handle");
    }

    Map<String, AbstractCommand> commands();

    void registerCommand(AbstractCommand command);
}
