package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankSystem.Account;
import org.poo.bankSystem.User;

import org.poo.fileio.CommandInput;
import org.poo.transactions.Transaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpendingsReport implements Command {
    private final List<User> users;

    /**
     * Constructor for SpendingsReport.
     * @param users the users
     */
    public SpendingsReport(final List<User> users) {
        this.users = users;
    }

    /**
     * This method is used to generate the spendings report of a user.
     * @param command the command to be executed
     * @param objectMapper the object mapper
     * @param output the output array
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper objectMapper,
                        final ArrayNode output) {
        ObjectNode commandNode = objectMapper.createObjectNode();
        commandNode.put("command", "spendingsReport");
        commandNode.put("timestamp", command.getTimestamp());
        ObjectNode outputNode = commandNode.putObject("output");

        for (User user : users) {
            for (Account account : user.getAccounts()) {
                if (account.getIBAN().equals(command.getAccount())) {
                    if (account.getType().equals("savings")) {
                        outputNode.put("error",
                                "This kind of report is not supported for a saving account");
                        output.add(commandNode);
                        return;
                    }
                    outputNode.put("IBAN", account.getIBAN());
                    outputNode.put("balance", account.getBalance());
                    outputNode.put("currency", account.getCurrency());

                    Map<String, Double> commerciantSpendings = new HashMap<>();
                    ArrayNode transactionsArray = outputNode.putArray("transactions");

                    for (Transaction transaction : account.getTransactions()) {
                        if (transaction.getTimestamp() >= command.getStartTimestamp()
                                && transaction.getTimestamp() <= command.getEndTimestamp()
                                && transaction.getDescription().equals("Card payment")) {
                            transactionsArray.add(transaction.toJson(objectMapper));
                            String commerciant = transaction.getCommerciant();
                            Double currentTotal = commerciantSpendings.getOrDefault(commerciant,
                                    0.0);
                            currentTotal += Double.parseDouble(transaction.getAmount());
                            commerciantSpendings.put(commerciant, currentTotal);
                        }
                    }

                    List<String> sortedCommerciants = new ArrayList<>(commerciantSpendings
                            .keySet());
                    sortedCommerciants.sort(Comparator.nullsFirst(String::compareTo));

                    ArrayNode commerciantsArray = outputNode.putArray("commerciants");
                    for (String commerciant : sortedCommerciants) {
                        ObjectNode commerciantNode = commerciantsArray.addObject();
                        commerciantNode.put("commerciant", commerciant);
                        commerciantNode.put("total", commerciantSpendings.get(commerciant));
                    }
                    output.add(commandNode);
                    return;
                }
            }
        }
        outputNode.put("description", "Account not found")
                .put("timestamp", command.getTimestamp());
        output.add(commandNode);
    }
}
