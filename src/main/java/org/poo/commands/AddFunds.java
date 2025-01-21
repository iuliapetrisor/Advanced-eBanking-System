package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.banksystem.BusinessAccount;
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
            if (user.getEmail().equals(command.getEmail())) {
                for (Account account : user.getAccounts()) {
                    if (account.getIBAN().equals(command.getAccount())) {
                        if (account.getType().equals("business")) {
                            BusinessAccount businessAccount = (BusinessAccount) account;
                            if (businessAccount.getEmployees().contains(user)) {
                                if (command.getAmount() > businessAccount.getDepositLimit()) {
                                    return;
                                }
                                double depositedAmount = businessAccount.getEmployeeDeposits()
                                        .getOrDefault(user, 0.0);
                                businessAccount.getEmployeeDeposits().put(user,
                                        depositedAmount + command.getAmount());
                            } else if (businessAccount.getManagers().contains(user)) {
                                double depositedAmount = businessAccount.getManagerDeposits()
                                        .getOrDefault(user, 0.0);
                                businessAccount.getManagerDeposits().put(user,
                                        depositedAmount + command.getAmount());
                            }
                        }
                        account.addFunds(command.getAmount());
                        break;
                    }
                }
            }
        }
    }
}
