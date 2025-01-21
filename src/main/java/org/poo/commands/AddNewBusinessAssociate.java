package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.banksystem.Account;
import org.poo.banksystem.BusinessAccount;
import org.poo.banksystem.User;
import org.poo.fileio.CommandInput;
import java.util.List;

public class AddNewBusinessAssociate implements Command {
    private final List<User> users;

    /**
     * Constructor for AddNewBusinessAssociate.
     * @param users the users
     */
    public AddNewBusinessAssociate(final List<User> users) {
        this.users = users;
    }

    /**
     * This method is used to add a new business associate to a user.
     * @param command the command to be executed
     * @param objectMapper the object mapper
     * @param output the output array
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper objectMapper,
                        final ArrayNode output) {
        Account account = findAccountByIBAN(command.getAccount());
        if (account == null || !account.getType().equals("business")) {
            return;
        }
        BusinessAccount businessAccount = (BusinessAccount) account;
        for (User user : users) {
            if (user.getEmail().equals(command.getEmail())) {
                if (businessAccount.getOwner().equals(user)
                        || businessAccount.getEmployees().contains(user)
                        || businessAccount.getManagers().contains(user)) {
//                    ObjectNode commandNode = objectMapper.createObjectNode();
//                    commandNode.put("command", "addNewBusinessAssociate");
//                    commandNode.put("timestamp", command.getTimestamp());
//                    commandNode.putObject("output").put("description",
//                                    "The user is already an associate of the account.")
//                            .put("timestamp", command.getTimestamp());
//                    output.add(commandNode);
                    return;
                }
                if (command.getRole().equals("employee")) {
                    businessAccount.addEmployee(user);
                    user.getBusinessAssociations().put(businessAccount, "employee");
                    user.addAccount(businessAccount);
                } else if (command.getRole().equals("manager")) {
                    businessAccount.addManager(user);
                    user.getBusinessAssociations().put(businessAccount, "manager");
                    user.addAccount(businessAccount);
                }
            }
        }
    }

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
