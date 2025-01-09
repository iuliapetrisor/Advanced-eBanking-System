package org.poo.commands;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bankSystem.Account;
import org.poo.bankSystem.SavingsAccount;
import org.poo.fileio.CommandInput;

import org.poo.bankSystem.User;
import org.poo.transactions.TransactionManager;
import org.poo.transactions.Transaction;
import org.poo.utils.Utils;

import java.util.List;

public class AddAccount implements Command {
    private List<User> users;
    private final TransactionManager transactionManager;

    /**
     * Constructor for AddAccount.
     * @param users the users
     */
    public AddAccount(final List<User> users, final TransactionManager transactionManager) {
        this.users = users;
        this.transactionManager = transactionManager;
    }

    /**
     * This method is used to add an account to a user.
     * @param command the command to be executed
     * @param objectMapper the object mapper
     * @param output the output array
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper objectMapper,
                        final ArrayNode output) {
        for (User user : users) {
            if (user.getEmail().equals(command.getEmail())) {
                String IBAN = Utils.generateIBAN();
                Account account;
                if (command.getAccountType().equals("savings")) {
                    account = new SavingsAccount(IBAN, 0.0,
                            command.getCurrency(), command.getInterestRate());
                } else {
                    account = new Account(IBAN, 0.0, command.getCurrency(),
                            command.getAccountType());
                }
                user.addAccount(account);
                Transaction transaction = new Transaction.Builder()
                        .timestamp(command.getTimestamp())
                        .description("New account created")
                        .build();
                transactionManager.addTransactionToUser(user.getEmail(), transaction);
                transactionManager.addTransactionToAccount(user.getEmail(),
                        account.getIBAN(), transaction);
                break;
            }
        }
    }
}
