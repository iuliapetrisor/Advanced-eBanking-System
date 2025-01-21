package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banksystem.Account;
import org.poo.banksystem.BusinessAccount;
import org.poo.banksystem.User;
import org.poo.fileio.CommandInput;

import java.util.List;

public class ChangeSpendingLimit implements Command {
    private final List<User> users;

    /**
     * Constructor for ChangeSpendingLimit.
     * @param users the users
     */
    public ChangeSpendingLimit(final List<User> users) {
        this.users = users;
    }

    /**
     * This method is used to change the spending limit of a business account.
     * @param command the command to be executed
     * @param objectMapper the object mapper
     * @param output the output array
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper objectMapper,
                        final ArrayNode output) {
        Account account = findAccountByIBAN(command.getAccount());
        if (account == null) {
            ObjectNode commandNode = objectMapper.createObjectNode();
            commandNode.put("command", "changeSpendingLimit");
            commandNode.put("timestamp", command.getTimestamp());
            commandNode.putObject("output").put("description",
                            "Account not found.")
                    .put("timestamp", command.getTimestamp());
            output.add(commandNode);
            return;
        }
        if (!account.getType().equals("business")) {
            ObjectNode commandNode = objectMapper.createObjectNode();
            commandNode.put("command", "changeSpendingLimit");
            commandNode.put("timestamp", command.getTimestamp());
            commandNode.putObject("output").put("description",
                            "This is not a business account")
                    .put("timestamp", command.getTimestamp());
            output.add(commandNode);
            return;
        }
        BusinessAccount businessAccount = (BusinessAccount) account;
        if (!businessAccount.getOwner().getEmail().equals(command.getEmail())) {
            ObjectNode commandNode = objectMapper.createObjectNode();
            commandNode.put("command", "changeSpendingLimit");
            commandNode.put("timestamp", command.getTimestamp());
            commandNode.putObject("output").put("description",
                            "You must be owner in order to change spending limit.")
                    .put("timestamp", command.getTimestamp());
            output.add(commandNode);
            return;
        }
        businessAccount.setSpendingLimit(command.getAmount());
    }

    /**
     * This method is used to find an account by its IBAN.
     * @param iban the IBAN of the account
     * @return the account with the given IBAN
     */
    private Account findAccountByIBAN(final String iban) {
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                if (account.getIBAN().equals(iban)) {
                    return account;
                }
            }
        }
        return null;
    }
}
