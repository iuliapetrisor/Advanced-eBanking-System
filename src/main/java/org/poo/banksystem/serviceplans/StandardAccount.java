package org.poo.banksystem.serviceplans;

/**
 * Implementation of the AccountPlan interface for standard accounts.
 * <p>
 * This class represents a standard account plan with a fixed transaction fee.
 * </p>
 */
public class StandardAccount implements AccountPlan {
    public static final double FEE = 0.002;

    /**
     * Getter for the transaction fee.
     *
     * @param amount the amount of the transaction
     * @return the transaction fee
     */
    @Override
    public double getTransactionFee(final double amountInRon, final double amount) {
        return FEE * amount;
    }
}
