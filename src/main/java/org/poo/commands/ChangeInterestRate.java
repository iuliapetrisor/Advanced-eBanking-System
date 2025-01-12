package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banksystem.Account;
import org.poo.banksystem.SavingsAccount;
import org.poo.banksystem.User;
import org.poo.fileio.CommandInput;
import org.poo.transactions.Transaction;
import org.poo.transactions.TransactionManager;


import java.util.List;

public class ChangeInterestRate implements Command {
    private final List<User> users;
    private final TransactionManager transactionManager;

    /**
     * Constructor for ChangeInterestRate.
     * @param users the users
     */
    public ChangeInterestRate(final List<User> users,
                              final TransactionManager transactionManager) {
        this.users = users;
        this.transactionManager = transactionManager;
    }

    /**
     * This method is used to change the interest rate of an account.
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
                    if (account.getType().equals("savings")) {
                        SavingsAccount savingsAccount = (SavingsAccount) account;
                        savingsAccount.setInterestRate(command.getInterestRate());

                        Transaction transaction = new Transaction.Builder()
                                .timestamp(command.getTimestamp())
                                .description("Interest rate of the account changed to "
                                        + command.getInterestRate())
                                .build();
                        transactionManager.addTransactionToUser(user.getEmail(), transaction);
                        transactionManager.addTransactionToAccount(user.getEmail(),
                                account.getIBAN(), transaction);
                        return;
                    }
                    ObjectNode commandNode = objectMapper.createObjectNode();
                    commandNode.put("command", "changeInterestRate");
                    commandNode.put("timestamp", command.getTimestamp());
                    commandNode.putObject("output")
                            .put("description", "This is not a savings account")
                            .put("timestamp", command.getTimestamp());
                    output.add(commandNode);
                }
            }
        }
    }
}
