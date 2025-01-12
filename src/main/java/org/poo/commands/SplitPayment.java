package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banksystem.Account;
import org.poo.banksystem.User;
import org.poo.banksystem.ExchangeRateManager;
import org.poo.fileio.CommandInput;
import org.poo.transactions.Transaction;
import org.poo.transactions.TransactionManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class SplitPayment implements Command {
    private final List<User> users;
    private final ExchangeRateManager exchangeRateManager;
    private final TransactionManager transactionManager;

    /**
     * Constructor for SplitPayment.
     * @param users the users
     * @param exchangeRateManager the exchange rate manager
     */
    public SplitPayment(final List<User> users, final ExchangeRateManager exchangeRateManager,
                        final TransactionManager transactionManager) {
        this.users = users;
        this.exchangeRateManager = exchangeRateManager;
        this.transactionManager = transactionManager;
    }

    /**
     * This method is used to handle split payments between accounts.
     * @param command the command to be executed
     * @param objectMapper the object mapper
     * @param output the output array
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper objectMapper,
                        final ArrayNode output) {
        List<String> accountsForSplit = command.getAccounts();
        double totalAmount = command.getAmount();
        String currency = command.getCurrency();
        int timestamp = command.getTimestamp();

        double splitAmount = totalAmount / accountsForSplit.size();

        boolean enoughBalance = true;

        Account accountWithInsufficientFunds = null;
        for (String accountIBAN : accountsForSplit) {
            Account account = findAccountByIBAN(accountIBAN);
            if (account == null) {
                ObjectNode errorNode = objectMapper.createObjectNode();
                errorNode.put("command", "splitPayment");
                errorNode.put("timestamp", timestamp);
                errorNode.put("error", "Account not found");
                output.add(errorNode);
                return;
            }
            double splitAmountInAccountCurrency = exchangeRateManager
                    .convert(splitAmount, currency, account.getCurrency());
            if (account.getBalance() < splitAmountInAccountCurrency) {
                enoughBalance = false;
                accountWithInsufficientFunds = account;
            }
        }

        for (String accountIBAN : accountsForSplit) {
            Account account = findAccountByIBAN(accountIBAN);
            User user = findUserByAccount(account);
            if (enoughBalance) {
                double splitAmountInAccountCurrency = exchangeRateManager.convert(splitAmount,
                        currency, account.getCurrency());
                BigDecimal amountBD = BigDecimal.valueOf(splitAmountInAccountCurrency)
                        .setScale(ExchangeRateManager.SCALE_PRECISION, RoundingMode.HALF_UP);
                account.pay(amountBD.doubleValue());
                Transaction transaction = new Transaction.Builder()
                        .timestamp(timestamp)
                        .description(String.format("Split payment of %.2f %s", totalAmount,
                                currency))
                        .currency(currency)
                        .amount(String.valueOf(splitAmount))
                        .involvedAccounts(accountsForSplit)
                        .build();
                transactionManager.addTransactionToUser(user.getEmail(), transaction);
                transactionManager.addTransactionToAccount(user.getEmail(),
                        account.getIBAN(), transaction);
                } else {
                    Transaction transaction = new Transaction.Builder()
                            .timestamp(timestamp)
                            .description(String.format("Split payment of %.2f %s", totalAmount,
                                    currency))
                            .currency(currency)
                            .amount(String.valueOf(splitAmount))
                            .error("Account " + accountWithInsufficientFunds.getIBAN()
                                    + " has insufficient funds for a split payment.")
                            .involvedAccounts(accountsForSplit)
                            .build();
                    transactionManager.addTransactionToUser(user.getEmail(), transaction);
                    transactionManager.addTransactionToAccount(user.getEmail(),
                            account.getIBAN(), transaction);
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

    private User findUserByAccount(final Account account) {
        for (User user : users) {
            for (Account userAccount : user.getAccounts()) {
                if (userAccount.equals(account)) {
                    return user;
                }
            }
        }
        return null;
    }
}
