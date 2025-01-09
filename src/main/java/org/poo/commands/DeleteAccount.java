package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bankSystem.Account;
import org.poo.bankSystem.User;
import org.poo.fileio.CommandInput;
import org.poo.transactions.Transaction;
import org.poo.transactions.TransactionManager;

import java.util.List;

public class DeleteAccount implements Command {
    private List<User> users;
    private final TransactionManager transactionManager;

    /**
     * Constructor for DeleteAccount.
     * @param users the users
     */
    public DeleteAccount(final List<User> users,
                         final TransactionManager transactionManager) {
        this.users = users;
        this.transactionManager = transactionManager;
    }

    /**
     * This method is used to delete an account.
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
                        if (account.getBalance() == 0.0) {
                            user.getAccounts().remove(account);
                            ObjectNode commandNode = objectMapper.createObjectNode();
                            commandNode.put("command", "deleteAccount");
                            commandNode.put("timestamp", command.getTimestamp());
                            commandNode.putObject("output")
                                    .put("success", "Account deleted")
                                    .put("timestamp", command.getTimestamp());
                            output.add(commandNode);
                            return;
                        }
                        Transaction transaction = new Transaction.Builder()
                                .timestamp(command.getTimestamp())
                                .description("Account couldn't be deleted - "
                                        + "there are funds remaining")
                                .build();
                        transactionManager.addTransactionToUser(command.getEmail(), transaction);
                        transactionManager.addTransactionToAccount(command.getEmail(),
                                account.getIBAN(), transaction);
                    }
                }
            }
        }
        ObjectNode commandNode = objectMapper.createObjectNode();
        commandNode.put("command", "deleteAccount");
        commandNode.put("timestamp", command.getTimestamp());
        commandNode.putObject("output")
                .put("error", "Account couldn't be deleted"
                        + " - see org.poo.transactions for details")
                .put("timestamp", command.getTimestamp());
        output.add(commandNode);
    }
}

