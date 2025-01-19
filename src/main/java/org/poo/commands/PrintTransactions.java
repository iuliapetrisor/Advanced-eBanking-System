package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banksystem.User;
import org.poo.fileio.CommandInput;
import org.poo.transactions.Transaction;

import java.util.Comparator;
import java.util.List;

public class PrintTransactions implements Command {
    private final List<User> users;

    /**
     * Constructor for PrintTransactions.
     * @param users the users
     */
    public PrintTransactions(final List<User> users) {
        this.users = users;
    }

    /**
     * This method is used to print the transactions of a user.
     * @param command the command to be executed
     * @param objectMapper the object mapper
     * @param output the output array
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper objectMapper,
                        final ArrayNode output) {
        ObjectNode commandNode = objectMapper.createObjectNode();
        commandNode.put("command", "printTransactions");
        commandNode.put("timestamp", command.getTimestamp());

        ArrayNode transactionsArray = commandNode.putArray("output");
        for (User user : users) {
            if (user.getEmail().equals(command.getEmail())) {
                user.getTransactions().stream()
                        .sorted(Comparator.comparingInt(Transaction::getTimestamp))
                        .forEach(transaction -> transactionsArray
                                .add(transaction.toJson(objectMapper)));
                break;
            }
        }
        output.add(commandNode);
    }
}
