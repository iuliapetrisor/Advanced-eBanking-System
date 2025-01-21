package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.banksystem.ExchangeRateManager;
import org.poo.banksystem.User;
import org.poo.banksystem.Account;
import org.poo.fileio.CommandInput;
import org.poo.transactions.TransactionManager;
import org.poo.transactions.Transaction;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class UpgradePlan implements Command {
    private final List<User> users;
    private final TransactionManager transactionManager;
    private final ExchangeRateManager exchangeRateManager;
    public static final int STANDARD_TO_SILVER = 100;
    public static final int STANDARD_TO_GOLD = 350;
    public static final int SILVER_TO_GOLD = 250;

    /**
     * Constructor for UpgradePlan.
     * @param users the users
     * @param exchangeRateManager the exchange rate manager
     * @param transactionManager the transaction manager
     */
    public UpgradePlan(final List<User> users,
                       final ExchangeRateManager exchangeRateManager,
                       final TransactionManager transactionManager) {
        this.users = users;
        this.transactionManager = transactionManager;
        this.exchangeRateManager = exchangeRateManager;
    }

    /**
     * This method is used to upgrade a plan.
     * @param command the command to be executed
     * @param objectMapper the object mapper
     * @param output the output array
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper objectMapper,
                        final ArrayNode output) {
        String newPlanType = command.getNewPlanType();
        String accountIBAN = command.getAccount();
        int timestamp = command.getTimestamp();

        for (User user : users) {
            for (Account account : user.getAccounts()) {
                if (account.getIBAN().equals(accountIBAN)) {
                    String currentPlan = account.getPlanType();
                    if (currentPlan.equals(newPlanType)) {
                        Transaction transaction = new Transaction.Builder()
                                .timestamp(timestamp)
                                .description("The user already has the " + newPlanType + " plan.")
                                .build();
                        transactionManager.addTransactionToUser(user.getEmail(), transaction);
                        transactionManager.addTransactionToAccount(user.getEmail(), accountIBAN,
                                transaction);
                        return;
                    }
                    if (isDowngrade(currentPlan, newPlanType)) {
                        Transaction transaction = new Transaction.Builder()
                                .timestamp(timestamp)
                                .description("You cannot downgrade your plan.")
                                .accountIBAN(accountIBAN)
                                .build();
                        transactionManager.addTransactionToUser(user.getEmail(), transaction);
                        transactionManager.addTransactionToAccount(user.getEmail(), accountIBAN,
                                transaction);
                        return;
                    }

                    double feeInRON = calculateUpgradeFee(currentPlan, newPlanType);
                    double feeInAccountCurrency = exchangeRateManager.convert(feeInRON, "RON",
                            account.getCurrency());

                    if (account.getBalance() < feeInAccountCurrency) {
                        Transaction transaction = new Transaction.Builder()
                                .timestamp(timestamp)
                                .description("Insufficient funds")
                                .build();
                        transactionManager.addTransactionToUser(user.getEmail(), transaction);
                        transactionManager.addTransactionToAccount(user.getEmail(), accountIBAN,
                                transaction);
                        return;
                    }

                    account.pay(feeInAccountCurrency);
                    user.setPlanTypeForAllAccounts(newPlanType);

                    Transaction transaction = new Transaction.Builder()
                            .timestamp(timestamp)
                            .description("Upgrade plan")
                            .accountIBAN(accountIBAN)
                            .newPlanType(newPlanType)
                            .build();
                    transactionManager.addTransactionToUser(user.getEmail(), transaction);
                    transactionManager.addTransactionToAccount(user.getEmail(),
                            accountIBAN, transaction);
                    return;
                }
            }
        }
        ObjectNode commandNode = objectMapper.createObjectNode();
        commandNode.put("command", "upgradePlan");
        commandNode.put("timestamp", timestamp);
        commandNode.putObject("output")
                .put("description", "Account not found")
                .put("timestamp", timestamp);
        output.add(commandNode);

    }

    /**
     * This method is used to check if the plan is a downgrade.
     * @param currentPlan the current plan
     * @param newPlan the new plan
     * @return true if the plan is a downgrade, false otherwise
     */
    private boolean isDowngrade(final String currentPlan, final String newPlan) {
        if ((currentPlan.equals("gold") || currentPlan.equals("silver"))
                && !newPlan.equals("gold")) {
            return true;
        }
        return false;
    }

    /**
     * This method is used to calculate the upgrade fee.
     * @param currentPlan the current plan
     * @param newPlan the new plan
     * @return the upgrade fee
     */
    private double calculateUpgradeFee(final String currentPlan, final String newPlan) {
        if (currentPlan.equals("standard") || currentPlan.equals("student")) {
            if (newPlan.equals("silver")) {
                return STANDARD_TO_SILVER;
            } else if (newPlan.equals("gold")) {
                return STANDARD_TO_GOLD;
            }
        } else if (currentPlan.equals("silver") && newPlan.equals("gold")) {
            return SILVER_TO_GOLD;
        }
        return 0.0;
    }
}
