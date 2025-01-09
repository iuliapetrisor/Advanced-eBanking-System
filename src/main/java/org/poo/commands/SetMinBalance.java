package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankSystem.Account;
import org.poo.bankSystem.User;
import org.poo.fileio.CommandInput;

import java.util.List;

public class SetMinBalance implements Command {
    private final List<User> users;

    /**
     * Constructor for SetMinBalance.
     * @param users the users
     */
    public SetMinBalance(final List<User> users) {
        this.users = users;
    }

    /**
     * This method is used to set the minimum balance of an account.
     * @param command the command to be executed
     * @param objectMapper the object mapper
     * @param output the output array
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper objectMapper,
                        final ArrayNode output) {
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                if (account.getIBAN().equals(command.getAccount())) {
                    account.setMinBalance(command.getAmount());
                    return;
                }
            }
        }

        ObjectNode errorNode = objectMapper.createObjectNode();
        errorNode.put("command", "setMinBalance");
        errorNode.put("timestamp", command.getTimestamp());
        errorNode.putObject("output").put("error", "Invalid account or user");
        output.add(errorNode);
    }
}
