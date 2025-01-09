package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.bankSystem.Account;
import org.poo.bankSystem.Card;
import org.poo.bankSystem.User;


import java.util.List;

public class PrintUsers implements Command {
    private final List<User> users;

    /**
     * Constructor for PrintUsers.
     * @param users the users
     */
    public PrintUsers(final List<User> users) {
        this.users = users;
    }

    /**
     * This method is used to print the users.
     * @param command the command to be executed
     * @param objectMapper the object mapper
     * @param output the output array
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper objectMapper,
                        final ArrayNode output) {
        ObjectNode commandNode = objectMapper.createObjectNode();
        commandNode.put("command", "printUsers");
        commandNode.put("timestamp", command.getTimestamp());

        ArrayNode usersArray = objectMapper.createArrayNode();
        for (User user : users) {
            ObjectNode userNode = objectMapper.createObjectNode();
            userNode.put("firstName", user.getFirstName());
            userNode.put("lastName", user.getLastName());
            userNode.put("email", user.getEmail());

            ArrayNode accountsArray = objectMapper.createArrayNode();
            for (Account account : user.getAccounts()) {
                ObjectNode accountNode = objectMapper.createObjectNode();
                accountNode.put("IBAN", account.getIBAN());
                accountNode.put("balance", account.getBalance());
                accountNode.put("currency", account.getCurrency());
                accountNode.put("type", account.getType());

                ArrayNode cardsArray = objectMapper.createArrayNode();
                for (Card card : account.getCards()) {
                    ObjectNode cardNode = objectMapper.createObjectNode();
                    cardNode.put("cardNumber", card.getCardNumber());
                    cardNode.put("status", card.getStatus());
                    cardsArray.add(cardNode);
                }
                accountNode.set("cards", cardsArray);
                accountsArray.add(accountNode);
            }
            userNode.set("accounts", accountsArray);
            usersArray.add(userNode);
        }
        commandNode.set("output", usersArray);
        output.add(commandNode);
    }
}
