package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bankSystem.Account;
import org.poo.bankSystem.Card;
import org.poo.bankSystem.User;
import org.poo.fileio.CommandInput;
import org.poo.transactions.Transaction;
import org.poo.transactions.TransactionManager;
import org.poo.utils.Utils;

import java.util.List;

public class CreateOneTimeCard implements Command {
    private final List<User> users;
    private final TransactionManager transactionManager;

    /**
     * Constructor for CreateOneTimeCard.
     * @param users the users
     */
    public CreateOneTimeCard(final List<User> users,
                             final TransactionManager transactionManager) {
        this.users = users;
        this.transactionManager = transactionManager;
    }

    /**
     * This method is used to create a one-time card.
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
                    if (account.getIBAN().equals(command.getAccount())) {
                        String cardNumber = Utils.generateCardNumber();
                        Card card = new Card(cardNumber, "active");
                        account.addCard(card);
                        card.setOneTime(true);

                        Transaction transaction = new Transaction.Builder()
                                .timestamp(command.getTimestamp())
                                .description("New card created")
                                .card(cardNumber)
                                .cardHolder(user.getEmail())
                                .account(account.getIBAN())
                                .build();
                        transactionManager.addTransactionToUser(user.getEmail(), transaction);
                        transactionManager.addTransactionToAccount(user.getEmail(),
                                account.getIBAN(), transaction);
                        return;
                    }
                }
            }
        }
    }
}
