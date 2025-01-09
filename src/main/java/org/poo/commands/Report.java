package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankSystem.Account;
import org.poo.bankSystem.User;
import org.poo.fileio.CommandInput;
import org.poo.transactions.Transaction;

import java.util.List;

public class Report implements Command {
    private final List<User> users;

    /**
     * Constructor for Report.
     * @param users the users
     */
    public Report(final List<User> users) {
        this.users = users;
    }

    /**
     * This method is used to generate a report for a given account between two timestamps.
     * @param command the command to be executed
     * @param objectMapper the object mapper
     * @param output the output array
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper objectMapper,
                        final ArrayNode output) {
        String accountIBAN = command.getAccount();
        int startTimestamp = command.getStartTimestamp();
        int endTimestamp = command.getEndTimestamp();

        for (User user : users) {
            for (Account account : user.getAccounts()) {
                if (account.getIBAN().equals(accountIBAN)) {
                    ObjectNode reportNode = objectMapper.createObjectNode();
                    reportNode.put("command", "report");
                    reportNode.put("timestamp", command.getTimestamp());

                    ObjectNode outputNode = reportNode.putObject("output");
                    outputNode.put("IBAN", account.getIBAN());
                    outputNode.put("balance", account.getBalance());
                    outputNode.put("currency", account.getCurrency());

                    ArrayNode transactionsArray = outputNode.putArray("transactions");
                    for (Transaction transaction : account.getTransactions()) {
                        if (transaction.getTimestamp() >= startTimestamp
                                && transaction.getTimestamp() <= endTimestamp) {
                            transactionsArray.add(transaction.toJson(objectMapper));
                        }
                    }
                    output.add(reportNode);
                    return;
                }
            }
        }
        ObjectNode errorNode = objectMapper.createObjectNode();
        errorNode.put("command", "report");
        errorNode.put("timestamp", command.getTimestamp());
        errorNode.putObject("output").put("description", "Account not found")
                .put("timestamp", command.getTimestamp());
        output.add(errorNode);
    }
}
