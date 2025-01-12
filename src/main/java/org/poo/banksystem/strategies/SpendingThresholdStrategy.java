package org.poo.banksystem.strategies;

import org.poo.banksystem.Account;

public class SpendingThresholdStrategy implements CashbackStrategy {
    private double transactionAmount;
    public static final double GOLD_CASHBACK_1 = 0.005;
    public static final double GOLD_CASHBACK_2 = 0.0055;
    public static final double GOLD_CASHBACK_3 = 0.007;
    public static final double SILVER_CASHBACK_1 = 0.003;
    public static final double SILVER_CASHBACK_2 = 0.004;
    public static final double SILVER_CASHBACK_3 = 0.005;
    public static final double STANDARD_CASHBACK_1 = 0.001;
    public static final double STANDARD_CASHBACK_2 = 0.002;
    public static final double STANDARD_CASHBACK_3 = 0.0025;
    public static final double THRESHOLD_1 = 100;
    public static final double THRESHOLD_2 = 300;
    public static final double THRESHOLD_3 = 500;

    /**
     * Constructor for the SpendingThresholdStrategy class.
     *
     * @param transactionAmount the transaction amount
     */
    public SpendingThresholdStrategy(final double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    /**
     * Calculates the cashback for the account based on the total spendings
     * and the user's account plan type.
     *
     * @param account the account
     * @return the cashback
     */
    @Override
    public double calculateCashback(final Account account) {
        double totalSpent = account.getTotalSpendingsForCashback();
        String planType = account.getPlanType();
        double cashback = 0;

        if (totalSpent >= THRESHOLD_3) {
            cashback = planType.equals("gold") ? GOLD_CASHBACK_3 : planType.equals("silver")
                    ? SILVER_CASHBACK_3 : STANDARD_CASHBACK_3;
        } else if (totalSpent >= THRESHOLD_2) {
            cashback = planType.equals("gold") ? GOLD_CASHBACK_2 : planType.equals("silver")
                    ? SILVER_CASHBACK_2 : STANDARD_CASHBACK_2;
        } else if (totalSpent >= THRESHOLD_1) {
            cashback = planType.equals("gold") ? GOLD_CASHBACK_1 : planType.equals("silver")
                    ? SILVER_CASHBACK_1 : STANDARD_CASHBACK_1;
        }
        return transactionAmount * cashback;
    }
}
