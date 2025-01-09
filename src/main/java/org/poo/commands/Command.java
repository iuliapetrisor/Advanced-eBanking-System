package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;

/**
 * The Command interface is part of the Command design pattern.
 * <p>
 * This interface defines a method for executing a command, which is implemented
 * by various command classes to handle specific operations such as printing users,
 * adding accounts, creating cards, and adding funds.
 * </p>
 */
public interface Command {
    /**
     * This method is used to execute a command.
     * @param command the command to be executed
     * @param objectMapper the object mapper
     * @param output the output array
     */
    void execute(CommandInput command, ObjectMapper objectMapper, ArrayNode output);
}
