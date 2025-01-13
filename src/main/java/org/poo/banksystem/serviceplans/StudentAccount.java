package org.poo.banksystem.serviceplans;

/**
 * Implementation of the AccountPlan interface for student accounts.
 * <p>
 * This class represents a student account plan with no transaction fees.
 * </p>
 */
public class StudentAccount implements AccountPlan {
    /**
     * Gets the transaction fee for the specified amount.
     *
     * @param amount the amount
     * @return the transaction fee
     */
    @Override
    public double getTransactionFee(final double amountInRon, final double amount) {
        return 0;
    }
}
