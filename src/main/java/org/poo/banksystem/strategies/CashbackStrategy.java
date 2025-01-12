package org.poo.banksystem.strategies;

import org.poo.banksystem.Account;

/**
 * Interface for the CashbackStrategy class.
 */
public interface CashbackStrategy {
    /**
     * Calculates the cashback for the account.
     *
     * @param account the account
     * @return the cashback
     */
    double calculateCashback(Account account);
}
