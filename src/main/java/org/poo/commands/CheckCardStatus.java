package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banksystem.Account;
import org.poo.banksystem.Card;
import org.poo.banksystem.User;
import org.poo.transactions.TransactionManager;
import org.poo.transactions.Transaction;
import org.poo.fileio.CommandInput;

import java.util.List;

public class CheckCardStatus implements Command {
    private final List<User> users;
    private final TransactionManager transactionManager;
    private final int minBalanceLimit = 30;
    /**
     * Constructor for CheckCardStatus.
     * @param users the users
     * @param transactionManager the transaction manager
     */
    public CheckCardStatus(final List<User> users,
                           final TransactionManager transactionManager) {
        this.users = users;
        this.transactionManager = transactionManager;
    }

    /**
     * This method is used to check the status of a card.
     * @param command the command to be executed
     * @param objectMapper the object mapper
     * @param output the output array
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper objectMapper,
                        final ArrayNode output) {
        Card card = null;
        Account account = null;
        User user = null;

        for (User userIter : users) {
            for (Account accountIter : userIter.getAccounts()) {
                for (Card cardIter : accountIter.getCards()) {
                    if (cardIter.getCardNumber().equals(command.getCardNumber())) {
                        card = cardIter;
                        account = accountIter;
                        user = userIter;
                        break;
                    }
                }
            }
        }

        if (card == null) {
            ObjectNode errorNode = objectMapper.createObjectNode();
            errorNode.put("command", "checkCardStatus");
            errorNode.put("timestamp", command.getTimestamp());
            errorNode.putObject("output").put("description", "Card not found")
                    .put("timestamp", command.getTimestamp());
            output.add(errorNode);
            return;
        }

        Double balance = account.getBalance();
        Double minBalance = account.getMinBalance();

        if (balance == 0.0) {
            Transaction transaction = new Transaction.Builder()
                    .timestamp(command.getTimestamp())
                    .description("You have reached the minimum amount of funds,"
                            + " the card will be frozen")
                    .build();
            transactionManager.addTransactionToUser(user.getEmail(), transaction);
            return;
        }
        if (balance <= minBalance) {
            card.changeStatus("frozen");
            Transaction transaction = new Transaction.Builder()
                    .timestamp(command.getTimestamp())
                    .description("The card is frozen")
                    .build();
            transactionManager.addTransactionToUser(user.getEmail(), transaction);
            return;
        }

        if (balance - minBalance <= minBalanceLimit) {
            Transaction transaction = new Transaction.Builder()
                    .timestamp(command.getTimestamp())
                    .description("You have reached the minimum amount of funds")
                    .build();
            transactionManager.addTransactionToUser(user.getEmail(), transaction);
        }
    }
}
