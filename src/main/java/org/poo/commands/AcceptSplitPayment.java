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

import static org.poo.commands.SplitPayment.splitPayments;

public class AcceptSplitPayment implements Command {
    private final List<User> users;
    private final ExchangeRateManager exchangeRateManager;
    private final TransactionManager transactionManager;

    /**
     * Constructor for AcceptSplitPayment.
     * @param users the users
     * @param exchangeRateManager the exchange rate manager
     * @param transactionManager the transaction manager
     */
    public AcceptSplitPayment(final List<User> users, final ExchangeRateManager exchangeRateManager,
                              final TransactionManager transactionManager) {
        this.users = users;
        this.exchangeRateManager = exchangeRateManager;
        this.transactionManager = transactionManager;
    }

    /**
     * This method is used to handle accepting a split payment.
     * @param command the command to be executed
     * @param objectMapper the object mapper
     * @param output the output array
     */
    public void execute(final CommandInput command, final ObjectMapper objectMapper,
                        final ArrayNode output) {
        boolean enoughFunds = true;
        String accountWithInsufficientFunds = "";
        for (User user : users) {
            if (user.getEmail().equals(command.getEmail())) {
                int timestamp = user.acceptFirstPendingSplitPayment();
                if (timestamp == -1) {
                    return;
                }
                SplitPaymentData splitPaymentData = splitPayments.get(timestamp);
                List<Account> accounts = splitPaymentData.getAccounts();
                List<User> usersInvolved = splitPaymentData.getUsers();
                String type = splitPaymentData.getType();
                for (User userInvolved : usersInvolved) {
                    if (!userInvolved.getSplitPaymentResponses().get(timestamp)
                            .equals("accepted")) {
                        return;
                    }
                }

                for (Account account : accounts) {
                    double amount = account.getSplitPaymentAmounts().get(timestamp);
                    double amountInRon = exchangeRateManager.convert(amount,
                            account.getCurrency(), "RON");
                    double totalAmount = amount + account.getTransactionFee(amountInRon, amount);
                    if (account.getBalance() < totalAmount) {
                        enoughFunds = false;
                        accountWithInsufficientFunds = account.getIBAN();
                        break;
                    }
                }
                if (type.equals("equal")) {
                    if (!enoughFunds) {
                        for (Account account : accounts) {
                            User associatedUser = findUserByAccount(account);
                            if (associatedUser == null) {
                                return;
                            }
                            Transaction transaction = new Transaction.Builder()
                                    .timestamp(timestamp)
                                    .description(String.format("Split payment of %.2f %s",
                                            splitPaymentData.getAmount(),
                                            splitPaymentData.getCurrency()))
                                    .splitPaymentType("equal")
                                    .amount(String.valueOf(splitPaymentData.getAmount()
                                            / accounts.size()))
                                    .currency(splitPaymentData.getCurrency())
                                    .involvedAccounts(splitPaymentData.getAccountsIbans())
                                    .error("Account " + accountWithInsufficientFunds
                                            + " has insufficient funds for a split payment.")
                                    .build();
                            transactionManager.addTransactionToUser(associatedUser.getEmail(),
                                    transaction);
                            transactionManager.addTransactionToAccount(associatedUser.getEmail(),
                                    account.getIBAN(), transaction);
                        }
                        return;
                    }

                    for (Account account : accounts) {
                        User associatedUser = findUserByAccount(account);
                        if (associatedUser == null) {
                            return;
                        }
                        double amount = account.getSplitPaymentAmounts().get(timestamp);
                        double amountInRon = exchangeRateManager.convert(amount,
                                account.getCurrency(), "RON");
                        double totalAmount = amount + account.getTransactionFee(amountInRon,
                                amount);
                        account.pay(totalAmount);
                        Transaction transaction = new Transaction.Builder()
                                .timestamp(timestamp)
                                .description(String.format("Split payment of %.2f %s",
                                        splitPaymentData.getAmount(),
                                        splitPaymentData.getCurrency()))
                                .splitPaymentType("equal")
                                .amount(String.valueOf(splitPaymentData.getAmount()
                                        / accounts.size()))
                                .currency(splitPaymentData.getCurrency())
                                .involvedAccounts(splitPaymentData.getAccountsIbans())
                                .build();
                        transactionManager.addTransactionToUser(associatedUser.getEmail(),
                                transaction);
                        transactionManager.addTransactionToAccount(associatedUser.getEmail(),
                                account.getIBAN(), transaction);
                    }
                    return;
                } else if (type.equals("custom")) {
                    if (!enoughFunds) {
                        for (Account account : accounts) {
                            User associatedUser = findUserByAccount(account);
                            if (associatedUser == null) {
                                return;
                            }
                            Transaction transaction = new Transaction.Builder()
                                    .timestamp(timestamp)
                                    .description(String.format("Split payment of %.2f %s",
                                            splitPaymentData.getAmount(),
                                            splitPaymentData.getCurrency()))
                                    .splitPaymentType("custom")
                                    .amountForUsers(splitPaymentData.getAmountForUsers())
                                    .currency(splitPaymentData.getCurrency())
                                    .involvedAccounts(splitPaymentData.getAccountsIbans())
                                    .error("Account " + accountWithInsufficientFunds
                                            + " has insufficient funds for a split payment.")
                                    .build();
                            transactionManager.addTransactionToUser(associatedUser.getEmail(),
                                    transaction);
                            transactionManager.addTransactionToAccount(associatedUser.getEmail(),
                                    account.getIBAN(), transaction);
                        }
                        return;
                    }

                    for (Account account : accounts) {
                        User associatedUser = findUserByAccount(account);
                        if (associatedUser == null) {
                            return;
                        }
                        double amount = account.getSplitPaymentAmounts().get(timestamp);
                        double amountInRon = exchangeRateManager.convert(amount,
                                account.getCurrency(), "RON");
                        double totalAmount = amount + account.getTransactionFee(amountInRon,
                                amount);
                        account.pay(totalAmount);
                        Transaction transaction = new Transaction.Builder()
                                .timestamp(timestamp)
                                .description(String.format("Split payment of %.2f %s",
                                        splitPaymentData.getAmount(),
                                        splitPaymentData.getCurrency()))
                                .splitPaymentType("custom")
                                .amountForUsers(splitPaymentData.getAmountForUsers())
                                .currency(splitPaymentData.getCurrency())
                                .involvedAccounts(splitPaymentData.getAccountsIbans())
                                .build();
                        transactionManager.addTransactionToUser(associatedUser.getEmail(),
                                transaction);
                        transactionManager.addTransactionToAccount(associatedUser.getEmail(),
                                account.getIBAN(), transaction);
                    }
                    return;
                }

            }
        }

        ObjectNode commandNode = objectMapper.createObjectNode();
        commandNode.put("command", "acceptSplitPayment");
        commandNode.put("timestamp", command.getTimestamp());
        commandNode.putObject("output").put("description",
                        "User not found")
                .put("timestamp", command.getTimestamp());
        output.add(commandNode);
    }

    /**
     * This method is used to find a user by account.
     * @param account the account
     * @return the user
     */
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
