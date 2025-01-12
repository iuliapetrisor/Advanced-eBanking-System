package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;
import org.poo.banksystem.Account;
import org.poo.banksystem.User;

import java.util.List;

public class AddFunds implements Command {
    private List<User> users;

    /**
     * Constructor for AddFunds.
     * @param users the users
     */
    public AddFunds(final List<User> users) {
        this.users = users;
    }

    /**
     * This method is used to add funds to an account.
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
                    account.addFunds(command.getAmount());
                    break;
                }
            }
        }
    }
}
