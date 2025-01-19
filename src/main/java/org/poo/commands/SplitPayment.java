package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banksystem.Account;
import org.poo.banksystem.User;
import org.poo.banksystem.ExchangeRateManager;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplitPayment implements Command {
    private final List<User> users;
    private final ExchangeRateManager exchangeRateManager;
    private SplitPaymentData splitPaymentData;
    public static Map<Integer, SplitPaymentData> splitPayments = new HashMap<>();

    /**
     * Constructor for SplitPayment.
     * @param users the users
     * @param exchangeRateManager the exchange rate manager
     */
    public SplitPayment(final List<User> users, final ExchangeRateManager exchangeRateManager) {
        this.users = users;
        this.exchangeRateManager = exchangeRateManager;
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
        String type = command.getSplitPaymentType();
        String currency = command.getCurrency();
        int timestamp = command.getTimestamp();
        List<Account> accounts = new ArrayList<>();
        List<User> usersInvolved = new ArrayList<>();

        if (type.equals("equal")) {
            double amountForSplit = command.getAmount() / accountsForSplit.size();
            for (String iban : accountsForSplit) {
                Account account = findAccountByIBAN(iban);
                User user = findUserByAccount(account);
                if (account == null || user == null) {
                    ObjectNode errorNode = objectMapper.createObjectNode();
                    errorNode.put("command", "splitPayment");
                    errorNode.put("timestamp", timestamp);
                    errorNode.put("error", "User not found");
                    output.add(errorNode);
                    return;
                }
                accounts.add(account);
                usersInvolved.add(user);
                double amountInAccountCurrency = exchangeRateManager
                        .convert(amountForSplit, currency, account.getCurrency());
                user.getSplitPaymentResponses().put(timestamp, "pending");
                account.getSplitPaymentAmounts().put(timestamp, amountInAccountCurrency);
            }
            splitPaymentData = new SplitPaymentData(command.getAmount(),
                    currency, usersInvolved, accounts,
                    accountsForSplit, "equal", null);
            splitPayments.put(timestamp, splitPaymentData);
            return;
        }
        if (type.equals("custom")) {
            List<Double> amountForUsers = command.getAmountForUsers();
            for (String iban : accountsForSplit) {
                Account account = findAccountByIBAN(iban);
                User user = findUserByAccount(account);
                if (account == null || user == null) {
                    ObjectNode errorNode = objectMapper.createObjectNode();
                    errorNode.put("command", "splitPayment");
                    errorNode.put("timestamp", timestamp);
                    errorNode.put("error", "Account not found");
                    output.add(errorNode);
                    return;
                }
                accounts.add(account);
                usersInvolved.add(user);
                double amount = amountForUsers.get(accountsForSplit.indexOf(iban));
                double amountInAccountCurrency = exchangeRateManager
                        .convert(amount, currency, account.getCurrency());
                user.getSplitPaymentResponses().put(timestamp, "pending");
                account.getSplitPaymentAmounts().put(timestamp, amountInAccountCurrency);

            }
            splitPaymentData = new SplitPaymentData(command.getAmount(), currency,
                    usersInvolved, accounts,
                    accountsForSplit, "custom", amountForUsers);
            splitPayments.put(timestamp, splitPaymentData);
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
