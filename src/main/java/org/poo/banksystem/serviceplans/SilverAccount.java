package org.poo.banksystem.serviceplans;

/**
 * Implementation of the AccountPlan interface for silver accounts.
 * <p>
 * This class represents a silver account plan with a conditional transaction fee.
 * </p>
 */
public class SilverAccount implements AccountPlan {
    public static final int MAX_AMOUNT = 500;
    public static final double FEE = 0.001;

    /**
     * Getter for the transaction fee.
     *
     * @param amount the amount of the transaction
     * @return the transaction fee
     */
    @Override
    public double getTransactionFee(final double amount) {
        return amount < MAX_AMOUNT ? 0 : amount * FEE;
    }
}
