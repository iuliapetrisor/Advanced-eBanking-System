package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banksystem.Account;
import org.poo.banksystem.User;
import org.poo.fileio.CommandInput;
import org.poo.transactions.Transaction;
import org.poo.transactions.TransactionManager;

import java.util.List;

import static org.poo.commands.SplitPayment.splitPayments;

public class RejectSplitPayment implements Command {
    private final List<User> users;
    private final TransactionManager transactionManager;

    /**
     * Constructor for RejectSplitPayment.
     * @param users the users
     * @param transactionManager the transaction manager
     */
    public RejectSplitPayment(final List<User> users, final TransactionManager transactionManager) {
        this.users = users;
        this.transactionManager = transactionManager;
    }

    /**
     * This method is used to reject a split payment.
     * @param command the command to be executed
     * @param objectMapper the object mapper
     * @param output the output array
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper objectMapper,
                        final ArrayNode output) {
        for (User user : users) {
            if (user.getEmail().equals(command.getEmail())) {
                int timestamp = user.rejectFirstPendingSplitPayment();
                if (timestamp == -1) {
                    return;
                }
                SplitPaymentData splitPaymentData = splitPayments.get(timestamp);
                List<Account> accounts = splitPaymentData.getAccounts();
                String type = splitPaymentData.getType();

                if (type.equals("equal")) {
                    for (Account account : accounts) {
                        User associatedUser = findUserByAccount(account);
                        if (associatedUser == null) {
                            return;
                        }
                        Transaction transaction = new Transaction.Builder()
                                .timestamp(timestamp)
                                .description(String.format("Split payment of %.2f %s",
                                        splitPaymentData.getAmount(),
                                        splitPaymentData.getCurrency()))
                                .splitPaymentType("equal")
                                .amount(String.valueOf(splitPaymentData.getAmount()
                                            / accounts.size()))
                                .currency(splitPaymentData.getCurrency())
                                .involvedAccounts(splitPaymentData.getAccountsIbans())
                                .error("One user rejected the payment.")
                                .build();
                        transactionManager.addTransactionToUser(associatedUser.getEmail(),
                                transaction);
                        transactionManager.addTransactionToAccount(associatedUser.getEmail(),
                                account.getIBAN(), transaction);
                    }
                    return;
                } else if (type.equals("custom")) {
                    for (Account account : accounts) {
                        User associatedUser = findUserByAccount(account);
                        if (associatedUser == null) {
                            return;
                        }
                        Transaction transaction = new Transaction.Builder()
                                .timestamp(timestamp)
                                .description(String.format("Split payment of %.2f %s",
                                        splitPaymentData.getAmount(),
                                        splitPaymentData.getCurrency()))
                                .splitPaymentType("custom")
                                .amountForUsers(splitPaymentData.getAmountForUsers())
                                .currency(splitPaymentData.getCurrency())
                                .involvedAccounts(splitPaymentData.getAccountsIbans())
                                .error("One user rejected the payment.")
                                .build();
                        transactionManager.addTransactionToUser(associatedUser.getEmail(),
                                transaction);
                        transactionManager.addTransactionToAccount(associatedUser.getEmail(),
                                account.getIBAN(), transaction);
                    }
                }
                return;
            }
        }

        ObjectNode commandNode = objectMapper.createObjectNode();
        commandNode.put("command", "rejectSplitPayment");
        commandNode.put("timestamp", command.getTimestamp());
        commandNode.putObject("output").put("description",
                        "User not found")
                .put("timestamp", command.getTimestamp());
        output.add(commandNode);
    }

    /**
     * This method is used to find the user by account.
     * @param account the account
     * @return the user
     */
    private User findUserByAccount(final Account account) {
        for (User user : users) {
            for (Account userAccount : user.getAccounts()) {
                if (userAccount.equals(account)) {
                    return user;
                }
            }
        }
        return null;
    }
}
