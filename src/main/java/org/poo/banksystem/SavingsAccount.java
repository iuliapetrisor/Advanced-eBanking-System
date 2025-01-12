package org.poo.banksystem;

public class SavingsAccount extends Account {
    private double interestRate = 0.0;

    /**
     * Constructor for the SavingsAccount class.
     *
     * @param iban         the iban of the account
     * @param balance      the balance of the account
     * @param currency     the currency of the account
     */
    public SavingsAccount(final String iban, final double balance,
                          final String currency, final double interestRate) {
        super(iban, balance, currency, "savings");
        this.interestRate = interestRate;
    }

    /**
     * Getter for the interest rate.
     *
     * @return the interest rate
     */
    public double getInterestRate() {
        return interestRate;
    }

    /**
     * Setter for the interest rate.
     *
     * @param interestRate the interest rate
     */
    public void setInterestRate(final double interestRate) {
        this.interestRate = interestRate;
    }
}
