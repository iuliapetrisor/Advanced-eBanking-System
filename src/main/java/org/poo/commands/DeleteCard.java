package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bankSystem.Account;
import org.poo.bankSystem.Card;
import org.poo.bankSystem.User;
import org.poo.fileio.CommandInput;
import org.poo.transactions.Transaction;
import org.poo.transactions.TransactionManager;

import java.util.List;

public class DeleteCard implements Command {
    private final List<User> users;
    private final TransactionManager transactionManager;

    /**
     * Constructor for DeleteCard.
     * @param users the users
     */
    public DeleteCard(final List<User> users, final TransactionManager transactionManager) {
        this.users = users;
        this.transactionManager = transactionManager;
    }

    /**
     * This method is used to delete a card.
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
                            account.deleteCard(card.getCardNumber());

                            Transaction transaction = new Transaction.Builder()
                                    .timestamp(command.getTimestamp())
                                    .description("The card has been destroyed")
                                    .account(account.getIBAN())
                                    .card(card.getCardNumber())
                                    .cardHolder(command.getEmail())
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
        }
    }
}
