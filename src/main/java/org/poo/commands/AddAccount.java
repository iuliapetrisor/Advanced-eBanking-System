package org.poo.commands;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.banksystem.Account;
import org.poo.banksystem.BusinessAccount;
import org.poo.banksystem.SavingsAccount;
import org.poo.banksystem.User;
import org.poo.banksystem.ExchangeRateManager;
import org.poo.fileio.CommandInput;

import org.poo.transactions.TransactionManager;
import org.poo.transactions.Transaction;
import org.poo.utils.Utils;

import java.util.List;

public class AddAccount implements Command {
    private List<User> users;
    private final ExchangeRateManager exchangeRateManager;
    private final TransactionManager transactionManager;
    private final static double DEFAULT_LIMIT = 500.0;

    /**
     * Constructor for AddAccount.
     * @param users the users
     */
    public AddAccount(final List<User> users, final ExchangeRateManager exchangeRateManager,
                      final TransactionManager transactionManager) {
        this.users = users;
        this.exchangeRateManager = exchangeRateManager;
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
                String iban = Utils.generateIBAN();
                Account account;
                if (command.getAccountType().equals("savings")) {
                    account = new SavingsAccount(iban, 0.0,
                            command.getCurrency(), command.getInterestRate());
                } else if (command.getAccountType().equals("classic")) {
                    account = new Account(iban, 0.0, command.getCurrency(),
                            command.getAccountType());
                } else {
                    double initialLimits = exchangeRateManager.convert(DEFAULT_LIMIT,
                            "RON", command.getCurrency());
                    account = new BusinessAccount(iban, 0.0,
                            command.getCurrency(), user, initialLimits);
                }
                account.setPlanType(user.getPlanType());
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
