package org.poo.banksystem.serviceplans;

/**
 * Implementation of the AccountPlan interface for gold accounts.
 * <p>
 * This class represents a gold account plan with no transaction fees.
 * </p>
 */
public class GoldAccount implements AccountPlan {
    /**
     * Gets the transaction fee for a gold account.
     *
     * @param amount the amount of the transaction
     * @return the transaction fee
     */
    @Override
    public double getTransactionFee(final double amountInRon, final double amount) {
        return 0;
    }
}
