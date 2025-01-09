package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bankSystem.Account;
import org.poo.bankSystem.User;
import org.poo.bankSystem.ExchangeRateManager;
import org.poo.fileio.CommandInput;
import org.poo.transactions.Transaction;
import org.poo.transactions.TransactionManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class SendMoney implements Command {
    private final List<User> users;
    private final ExchangeRateManager exchangeRateManager;
    private final TransactionManager transactionManager;
    //private final TransactionNotifier transactionNotifier;

    /**
     * Constructor for SendMoney.
     * @param users the users
     * @param exchangeRateManager the exchange rate manager
     */
    public SendMoney(final List<User> users, final ExchangeRateManager exchangeRateManager,
                     final TransactionManager transactionManager) {
        this.users = users;
        this.exchangeRateManager = exchangeRateManager;
        this.transactionManager = transactionManager;
    }

    /**
     * This method is used to handle money transfers between accounts.
     * @param command the command to be executed
     * @param objectMapper the object mapper
     * @param output the output array
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper objectMapper,
                        final ArrayNode output) {
        Account senderAccount = null;
        Account receiverAccount = null;
        User sender = null;
        User receiver = null;

        String receiverIBAN = command.getReceiver();

        for (User user : users) {
            if (user.getEmail().equals(command.getEmail())) {
                sender = user;
                for (Account account : user.getAccounts()) {
                    if (account.getIBAN().equals(command.getAccount())) {
                        senderAccount = account;
                    }
                }
            }
            if (user.getAccountByAlias(receiverIBAN) != null) {
                receiverIBAN = user.getAccountByAlias(receiverIBAN);
            }
            for (Account account : user.getAccounts()) {
                if (account.getIBAN().equals(receiverIBAN)) {
                    receiverAccount = account;
                    receiver = user;
                }
            }
        }

        if (senderAccount == null || receiverAccount == null) {
            return;
        }

        double amountInReceiverCurrency = exchangeRateManager.convert(
                command.getAmount(), senderAccount.getCurrency(), receiverAccount.getCurrency());
//        BigDecimal amountBD = BigDecimal.valueOf(amountInReceiverCurrency)
//                .setScale(ExchangeRateManager.SCALE_PRECISION, RoundingMode.HALF_UP);
        if (senderAccount.getBalance() >= command.getAmount()) {
            senderAccount.pay(command.getAmount());
            receiverAccount.addFunds(amountInReceiverCurrency);

            Transaction transaction = new Transaction.Builder()
                    .timestamp(command.getTimestamp())
                    .description(command.getDescription())
                    .senderIBAN(senderAccount.getIBAN())
                    .receiverIBAN(receiverAccount.getIBAN())
                    .amount(command.getAmount() + " " + senderAccount.getCurrency())
                    .transferType("sent")
                    .build();
            transactionManager.addTransactionToUser(sender.getEmail(), transaction);
            transactionManager.addTransactionToAccount(sender.getEmail(),
                    senderAccount.getIBAN(), transaction);

            transaction = new Transaction.Builder()
                    .timestamp(command.getTimestamp())
                    .description(command.getDescription())
                    .senderIBAN(senderAccount.getIBAN())
                    .receiverIBAN(receiverAccount.getIBAN())
                    .amount(amountInReceiverCurrency + " " + receiverAccount.getCurrency())
                    .transferType("received")
                    .build();
            transactionManager.addTransactionToUser(receiver.getEmail(), transaction);
            transactionManager.addTransactionToAccount(receiver.getEmail(),
                    receiverAccount.getIBAN(), transaction);

            return;
        }
        Transaction transaction = new Transaction.Builder()
                .timestamp(command.getTimestamp())
                .description("Insufficient funds")
                .build();
        transactionManager.addTransactionToUser(sender.getEmail(), transaction);
        transactionManager.addTransactionToAccount(sender.getEmail(),
                senderAccount.getIBAN(), transaction);
    }
}
