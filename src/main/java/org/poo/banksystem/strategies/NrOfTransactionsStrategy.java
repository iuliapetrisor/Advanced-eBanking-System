package org.poo.banksystem.strategies;

import org.poo.banksystem.Account;

public class NrOfTransactionsStrategy implements CashbackStrategy {
    public static final double TECH_CASHBACK = 0.1;
    public static final double CLOTHES_CASHBACK = 0.05;
    public static final double FOOD_CASHBACK = 0.02;
    public static final double TWO_TRANSACTIONS = 2;
    public static final double FIVE_TRANSACTIONS = 5;
    public static final double TEN_TRANSACTIONS = 10;
    private String commerciant;

    /**
     * Constructor for the NrOfTransactionsStrategy class.
     *
     * @param commerciant the commerciant's name
     */
    public NrOfTransactionsStrategy(final String commerciant) {
        this.commerciant = commerciant;
    }

    /**
     * Calculates the cashback for the account based on the number of transactions.
     *
     * @param account the account
     * @return the cashback
     */
    @Override
    public double calculateCashback(final Account account) {
        int transactions = account.getNrTransactionsPerCommerciant().get(commerciant);
        if (transactions == TEN_TRANSACTIONS) {
            account.setDiscount("Tech", TECH_CASHBACK);
            return 0;
        } else if (transactions == FIVE_TRANSACTIONS) {
            account.setDiscount("Clothes", CLOTHES_CASHBACK);
            return 0;
        } else if (transactions == TWO_TRANSACTIONS) {
            account.setDiscount("Food", FOOD_CASHBACK);
            return 0;
        }
        return 0;
    }
}
