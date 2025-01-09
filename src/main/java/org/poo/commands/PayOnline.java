package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankSystem.Account;
import org.poo.bankSystem.Card;
import org.poo.bankSystem.User;
import org.poo.bankSystem.ExchangeRateManager;
import org.poo.fileio.CommandInput;
import org.poo.transactions.Transaction;
import org.poo.transactions.TransactionManager;
import org.poo.utils.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PayOnline implements Command {
    private final List<User> users;
    private final ExchangeRateManager exchangeRateManager;
    private final TransactionManager transactionManager;

    /**
     * Constructor for PayOnline.
     * @param users the users
     * @param exchangeRateManager the exchange rate manager
     */
    public PayOnline(final List<User> users, final ExchangeRateManager exchangeRateManager,
                     final TransactionManager transactionManager) {
        this.users = users;
        this.exchangeRateManager = exchangeRateManager;
        this.transactionManager = transactionManager;
    }

    /**
     * This method is used to handle online payments.
     * @param command the command to be executed
     * @param objectMapper the object mapper
     * @param output the output array
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper objectMapper,
                        final ArrayNode output) {
        for (User user : users) {
            if (user.getEmail().equals(command.getEmail())) {
                for (Account account : user.getAccounts()) {
                    for (Card card : account.getCards()) {
                        if (card.getCardNumber().equals(command.getCardNumber())) {
                            double amountInAccountCurrency = exchangeRateManager.convert(
                                    command.getAmount(), command.getCurrency(),
                                    account.getCurrency()
                            );

//                            BigDecimal amountBD = BigDecimal.valueOf(amountInAccountCurrency)
//                                    .setScale(ExchangeRateManager.SCALE_PRECISION,
//                                            RoundingMode.HALF_UP);

                            if (account.getBalance() >=  amountInAccountCurrency && card.isActive()
                                    && account.getBalance() - amountInAccountCurrency
                                    >= account.getMinBalance()) {
                                account.pay(amountInAccountCurrency);

                                Transaction transaction = new Transaction.Builder()
                                        .timestamp(command.getTimestamp())
                                        .description("Card payment")
                                        .amount(String.valueOf(amountInAccountCurrency))
                                        .commerciant(command.getCommerciant())
                                        .build();
                                transactionManager.addTransactionToUser(command.getEmail(),
                                        transaction);
                                transactionManager.addTransactionToAccount(command.getEmail(),
                                        account.getIBAN(), transaction);

                                if (card.isOneTime()) {
                                    transaction = new Transaction.Builder()
                                            .timestamp(command.getTimestamp())
                                            .description("The card has been destroyed")
                                            .card(card.getCardNumber())
                                            .cardHolder(user.getEmail())
                                            .account(account.getIBAN())
                                            .build();
                                    transactionManager.addTransactionToUser(user.getEmail(),
                                            transaction);
                                    transactionManager.addTransactionToAccount(command.getEmail(),
                                            account.getIBAN(), transaction);

                                    account.deleteCard(card.getCardNumber());
                                    String newCardNumber = Utils.generateCardNumber();
                                    Card newCard = new Card(newCardNumber, "active");
                                    newCard.setOneTime(true);
                                    account.addCard(newCard);

                                    transaction = new Transaction.Builder()
                                            .timestamp(command.getTimestamp())
                                            .description("New card created")
                                            .card(newCardNumber)
                                            .cardHolder(user.getEmail())
                                            .account(account.getIBAN())
                                            .build();
                                    transactionManager.addTransactionToUser(user.getEmail(),
                                            transaction);
                                    transactionManager.addTransactionToAccount(command.getEmail(),
                                            account.getIBAN(), transaction);
                                }
                                return;
                            }

                            if (!card.isActive()) {
                                Transaction transaction = new Transaction.Builder()
                                        .timestamp(command.getTimestamp())
                                        .description("The card is frozen")
                                        .build();
                                transactionManager.addTransactionToUser(command.getEmail(),
                                        transaction);
                                transactionManager.addTransactionToAccount(command.getEmail(),
                                        account.getIBAN(), transaction);
                                return;
                            }

                            if (account.getBalance() < amountInAccountCurrency) {
                                Transaction transaction = new Transaction.Builder()
                                        .timestamp(command.getTimestamp())
                                        .description("Insufficient funds")
                                        .build();
                                transactionManager.addTransactionToUser(command.getEmail(),
                                        transaction);
                                transactionManager.addTransactionToAccount(command.getEmail(),
                                        account.getIBAN(), transaction);
                                return;
                            }

                            if (account.getBalance() - amountInAccountCurrency
                                    < account.getMinBalance()) {
                                card.changeStatus("frozen");
                                Transaction transaction = new Transaction.Builder()
                                        .timestamp(command.getTimestamp())
                                        .description("The card is frozen")
                                        .build();
                                transactionManager.addTransactionToUser(command.getEmail(),
                                        transaction);
                                transactionManager.addTransactionToAccount(command.getEmail(),
                                        account.getIBAN(), transaction);
                                return;
                            }
                        }
                    }
                }
                ObjectNode commandNode = objectMapper.createObjectNode();
                commandNode.put("command", "payOnline");
                commandNode.put("timestamp", command.getTimestamp());
                commandNode.putObject("output").put("description",
                                "Card not found")
                                .put("timestamp", command.getTimestamp());
                output.add(commandNode);
                return;
            }
        }
    }
}
