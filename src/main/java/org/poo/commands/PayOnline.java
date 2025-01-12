package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banksystem.Account;
import org.poo.banksystem.Card;
import org.poo.banksystem.Commerciant;
import org.poo.banksystem.ExchangeRateManager;
import org.poo.banksystem.User;
import org.poo.fileio.CommandInput;
import org.poo.transactions.Transaction;
import org.poo.transactions.TransactionManager;
import org.poo.utils.Utils;
import java.util.List;

public class PayOnline implements Command {
    private final List<User> users;
    private List<Commerciant> commerciants;
    private final ExchangeRateManager exchangeRateManager;
    private final TransactionManager transactionManager;

    /**
     * Constructor for PayOnline.
     * @param users the users
     * @param commerciants the commerciants
     * @param exchangeRateManager the exchange rate manager
     * @param transactionManager the transaction manager
     */
    public PayOnline(final List<User> users,
                     final List<Commerciant> commerciants,
                     final ExchangeRateManager exchangeRateManager,
                     final TransactionManager transactionManager) {
        this.users = users;
        this.commerciants = commerciants;
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
                            double amountInRon = exchangeRateManager.convert(
                                    command.getAmount(), command.getCurrency(), "RON");
                            double amountInAccountCurrency = exchangeRateManager.convert(
                                    command.getAmount(), command.getCurrency(),
                                    account.getCurrency());

                            if (account.getBalance() >=  amountInAccountCurrency && card.isActive()
                                    && account.getBalance() - amountInAccountCurrency
                                    >= account.getMinBalance()) {
                                account.pay(amountInAccountCurrency);
                                account.pay(account.getTransactionFee(amountInAccountCurrency));
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

                                Commerciant commerciant = findCommerciantByName(command
                                        .getCommerciant());
                                if (commerciant == null) {
                                    return;
                                }
                                double cashback = account
                                        .processTransactionStrategy(amountInAccountCurrency,
                                        amountInRon, commerciant.getCashbackStrategyName());
                                if (cashback > 0) {
                                    account.addFunds(cashback);
                                }
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

    private Commerciant findCommerciantByName(final String name) {
        for (Commerciant commerciant : commerciants) {
            if (commerciant.getName().equals(name)) {
                return commerciant;
            }
        }
        return null;
    }
}
