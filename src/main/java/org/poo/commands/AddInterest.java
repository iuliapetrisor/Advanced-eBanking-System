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

public class AddInterest implements Command {
    private final List<User> users;
    private final TransactionManager transactionManager;
    /**
     * Constructor for AddInterest.
     * @param users the users
     */
    public AddInterest(final List<User> users,
                       final TransactionManager transactionManager) {
        this.users = users;
        this.transactionManager = transactionManager;
    }

    /**
     * This method is used to add interest to a savings account.
     * @param command the command to be executed
     * @param objectMapper the object mapper
     * @param output the output array
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper objectMapper,
                        final ArrayNode output) {
        String accountIBAN = command.getAccount();
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                if (account.getIBAN().equals(accountIBAN)) {
                    if (account.getType().equals("savings")) {
                        SavingsAccount savingsAccount = (SavingsAccount) account;
                        double interest = savingsAccount.getBalance()
                                * savingsAccount.getInterestRate();
                        savingsAccount.setBalance(savingsAccount.getBalance() + interest);
                        Transaction transaction = new Transaction.Builder()
                                .timestamp(command.getTimestamp())
                                .description("Interest rate income")
                                .amount(String.valueOf(interest))
                                .currency(account.getCurrency())
                                .build();
                        transactionManager.addTransactionToUser(user.getEmail(), transaction);
                        transactionManager.addTransactionToAccount(user.getEmail(),
                                accountIBAN, transaction);
                    } else {
                        ObjectNode commandNode = objectMapper.createObjectNode();
                        commandNode.put("command", "addInterest");
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
}
