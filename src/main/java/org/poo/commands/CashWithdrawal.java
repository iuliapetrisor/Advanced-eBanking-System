package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banksystem.Account;
import org.poo.banksystem.Card;
import org.poo.banksystem.ExchangeRateManager;
import org.poo.banksystem.User;
import org.poo.fileio.CommandInput;
import org.poo.transactions.TransactionManager;
import org.poo.transactions.Transaction;
import java.util.List;


public class CashWithdrawal implements Command {
    private final List<User> users;
    private final ExchangeRateManager exchangeRateManager;
    private final TransactionManager transactionManager;

    /**
     * Constructor for CashWithdrawal.
     * @param users the users
     * @param exchangeRateManager the exchange rate manager
     * @param transactionManager the transaction manager
     */
    public CashWithdrawal(final List<User> users,
                          final ExchangeRateManager exchangeRateManager,
                          final TransactionManager transactionManager) {
        this.users = users;
        this.exchangeRateManager = exchangeRateManager;
        this.transactionManager = transactionManager;
    }

    /**
     * This method is used to handle cash withdrawals.
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
                            if (card.isAlreadyUsed()) {
                                Transaction transaction = new Transaction.Builder()
                                        .timestamp(command.getTimestamp())
                                        .description("Card has already been used")
                                        .build();
                                transactionManager.addTransactionToUser(user.getEmail(),
                                        transaction);
                                transactionManager.addTransactionToAccount(user.getEmail(),
                                        account.getIBAN(), transaction);
                                return;
                            }

                            double amountInRon = command.getAmount();
                            double amountInAccountCurrency = exchangeRateManager.convert(
                                    amountInRon, "RON",
                                    account.getCurrency());
                            amountInAccountCurrency += account.getTransactionFee(amountInRon,
                                    amountInAccountCurrency);

                            if (account.getBalance() >= amountInAccountCurrency
                                    && card.isActive()) {
                                account.pay(amountInAccountCurrency);
                                Transaction transaction = new Transaction.Builder()
                                        .timestamp(command.getTimestamp())
                                        .description("Cash withdrawal of " + amountInRon)
                                        .amount(String.valueOf(amountInRon))
                                        .build();
                                transactionManager.addTransactionToUser(user.getEmail(),
                                        transaction);
                                transactionManager.addTransactionToAccount(user.getEmail(),
                                        account.getIBAN(), transaction);

                                if (card.isOneTime()) {
                                    card.setAlreadyUsed(true);
                                    return;
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
                commandNode.put("command", "cashWithdrawal");
                commandNode.put("timestamp", command.getTimestamp());
                commandNode.putObject("output").put("description",
                                "Card not found")
                        .put("timestamp", command.getTimestamp());
                output.add(commandNode);
                return;
            }
        }

        ObjectNode commandNode = objectMapper.createObjectNode();
        commandNode.put("command", "cashWithdrawal");
        commandNode.put("timestamp", command.getTimestamp());
        commandNode.putObject("output").put("description",
                        "User not found")
                .put("timestamp", command.getTimestamp());
        output.add(commandNode);
    }
}
