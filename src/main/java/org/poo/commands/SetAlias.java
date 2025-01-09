package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankSystem.User;
import org.poo.fileio.CommandInput;

import java.util.List;

public class SetAlias implements Command {
    private final List<User> users;

    /**
     * Constructor for SetAlias.
     * @param users the users
     */
    public SetAlias(final List<User> users) {
        this.users = users;
    }

    /**
     * This method is used to set an alias to an account.
     * @param command the command to be executed
     * @param objectMapper the object mapper
     * @param output the output array
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper objectMapper,
                        final ArrayNode output) {
        for (User user : users) {
            if (user.getEmail().equals(command.getEmail())) {
                user.setAlias(command.getAlias(), command.getAccount());
                return;
            }
        }

        ObjectNode errorNode = objectMapper.createObjectNode();
        errorNode.put("command", "setAlias");
        errorNode.put("timestamp", command.getTimestamp());
        errorNode.putObject("output").put("error", "User not found");
        output.add(errorNode);
    }
}
