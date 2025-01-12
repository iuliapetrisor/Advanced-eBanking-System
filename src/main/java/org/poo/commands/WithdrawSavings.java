package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banksystem.Account;
import org.poo.banksystem.ExchangeRateManager;
import org.poo.banksystem.User;
import org.poo.fileio.CommandInput;
import org.poo.transactions.Transaction;
import org.poo.transactions.TransactionManager;

import java.util.List;

public class WithdrawSavings implements Command {
    private final List<User> users;
    private final ExchangeRateManager exchangeRateManager;
    private final TransactionManager transactionManager;
    public static final int MINIMUM_AGE = 21;
    /**
     * Constructor for WithdrawSavings.
     * @param users the users
     * @param exchangeRateManager the exchange rate manager
     */
    public WithdrawSavings(final List<User> users, final ExchangeRateManager exchangeRateManager,
                           final TransactionManager transactionManager) {
        this.users = users;
        this.exchangeRateManager = exchangeRateManager;
        this.transactionManager = transactionManager;
    }

    /**
     * This method is used to handle withdrawals from savings accounts.
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
                    if (!account.getType().equals("savings")) {
                        Transaction transaction = new Transaction.Builder()
                                .timestamp(command.getTimestamp())
                                .description("Account is not of type savings.")
                                .build();
                        transactionManager.addTransactionToUser(user.getEmail(), transaction);
                        transactionManager.addTransactionToAccount(user.getEmail(),
                                account.getIBAN(), transaction);
                        return;
                    }

                    if (user.getAge() < MINIMUM_AGE) {
                        Transaction transaction = new Transaction.Builder()
                                .timestamp(command.getTimestamp())
                                .description("You don't have the minimum age required.")
                                .build();
                        transactionManager.addTransactionToUser(user.getEmail(), transaction);
                        transactionManager.addTransactionToAccount(user.getEmail(),
                                account.getIBAN(), transaction);
                        return;
                    }

                    Account receiverAccount = user.getFirstClassicAccountInCurrency(command
                            .getCurrency());

                    if (receiverAccount == null) {
                        Transaction transaction = new Transaction.Builder()
                                .timestamp(command.getTimestamp())
                                .description("You do not have a classic account.")
                                .build();
                        transactionManager.addTransactionToUser(user.getEmail(), transaction);
                        transactionManager.addTransactionToAccount(user.getEmail(),
                                account.getIBAN(), transaction);
                        return;
                    }

                    double convertedAmount = exchangeRateManager.convert(
                            command.getAmount(), command.getCurrency(),
                            account.getCurrency());
                    if (account.getBalance() < convertedAmount) {
                        Transaction transaction = new Transaction.Builder()
                                .timestamp(command.getTimestamp())
                                .description("Insufficient funds")
                                .build();
                        transactionManager.addTransactionToUser(user.getEmail(), transaction);
                        transactionManager.addTransactionToAccount(user.getEmail(),
                                account.getIBAN(), transaction);
                        return;
                    }

                    account.pay(convertedAmount);
                    receiverAccount.addFunds(command.getAmount());
                    Transaction transaction = new Transaction.Builder()
                            .timestamp(command.getTimestamp())
                            .description("Savings withdrawal")
                            .build();
                    transactionManager.addTransactionToUser(user.getEmail(), transaction);
                    transactionManager.addTransactionToAccount(user.getEmail(),
                            account.getIBAN(), transaction);
                    return;
                }
            }
        }

        ObjectNode commandNode = objectMapper.createObjectNode();
        commandNode.put("command", "withdrawSavings");
        commandNode.put("timestamp", command.getTimestamp());
        commandNode.putObject("output")
                .put("description", "Account not found")
                .put("timestamp", command.getTimestamp());
        output.add(commandNode);
    }
}
