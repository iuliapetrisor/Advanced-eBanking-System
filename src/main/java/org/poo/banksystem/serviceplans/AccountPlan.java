package org.poo.banksystem.serviceplans;

/**
 * Interface for the account plan.
 */
public interface AccountPlan {
    /**
     * Gets the transaction fee for the account.
     *
     * @param amount the amount of the transaction
     * @return the transaction fee
     */
    double getTransactionFee(double amountInRon, double amount);
}
