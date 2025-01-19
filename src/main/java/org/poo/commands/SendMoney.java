package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banksystem.Account;
import org.poo.banksystem.Commerciant;
import org.poo.banksystem.User;
import org.poo.banksystem.ExchangeRateManager;
import org.poo.fileio.CommandInput;
import org.poo.transactions.Transaction;
import org.poo.transactions.TransactionManager;

import java.util.List;

public class SendMoney implements Command {
    private final List<User> users;
    private List<Commerciant> commerciants;
    private final ExchangeRateManager exchangeRateManager;
    private final TransactionManager transactionManager;

    /**
     * Constructor for SendMoney.
     * @param users the users
     * @param commerciants the commerciants
     * @param exchangeRateManager the exchange rate manager
     * @param transactionManager the transaction manager
     */
    public SendMoney(final List<User> users,
                     final List<Commerciant> commerciants,
                     final ExchangeRateManager exchangeRateManager,
                     final TransactionManager transactionManager) {
        this.users = users;
        this.commerciants = commerciants;
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

        if (senderAccount == null) {
            ObjectNode commandNode = objectMapper.createObjectNode();
            commandNode.put("command", "sendMoney");
            commandNode.put("timestamp", command.getTimestamp());
            commandNode.putObject("output").put("description",
                            "User not found")
                    .put("timestamp", command.getTimestamp());
            output.add(commandNode);
            return;
        }

        if (senderAccount != null && receiverAccount == null) {
            for (Commerciant commerciant : commerciants) {
                if (commerciant.getAccount().equals(receiverIBAN)) {
                    double amountInRon = exchangeRateManager.convert(
                            command.getAmount(), senderAccount.getCurrency(), "RON");
                    double amount = command.getAmount()
                            + senderAccount.getTransactionFee(amountInRon, command.getAmount());
                    if (senderAccount.getBalance() >= amount) {
                        senderAccount.pay(amount);
                        Transaction transaction = new Transaction.Builder()
                                .timestamp(command.getTimestamp())
                                .description(command.getDescription())
                                .senderIBAN(senderAccount.getIBAN())
                                .receiverIBAN(receiverIBAN)
                                .amount(command.getAmount() + " " + senderAccount.getCurrency())
                                .transferType("sent")
                                .build();
                        transactionManager.addTransactionToUser(sender.getEmail(), transaction);
                        transactionManager.addTransactionToAccount(sender.getEmail(),
                                senderAccount.getIBAN(), transaction);
                        if (senderAccount.hasDiscount(commerciant.getType())) {
                            double discount = senderAccount.getDiscount(commerciant.getType())
                                    * command.getAmount();
                            senderAccount.addFunds(discount);
                            senderAccount.getDiscounts().remove(commerciant.getType());
                        }
                        double cashback = senderAccount
                                .processTransactionStrategy(command.getAmount(),
                                        amountInRon, commerciant);
                        if (cashback > 0) {
                            senderAccount.addFunds(cashback);
                        }
                        return;
                    }
                }
            }
            ObjectNode commandNode = objectMapper.createObjectNode();
            commandNode.put("command", "sendMoney");
            commandNode.put("timestamp", command.getTimestamp());
            commandNode.putObject("output").put("description",
                            "User not found")
                    .put("timestamp", command.getTimestamp());
            output.add(commandNode);
            return;
        }

        double amountInRon = exchangeRateManager.convert(
                command.getAmount(), senderAccount.getCurrency(), "RON");
        double amountInReceiverCurrency = exchangeRateManager.convert(
                command.getAmount(), senderAccount.getCurrency(), receiverAccount.getCurrency());
        double amount = command.getAmount() + senderAccount.getTransactionFee(amountInRon,
                command.getAmount());
        if (senderAccount.getBalance() >= amount) {
            senderAccount.pay(amount);
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
