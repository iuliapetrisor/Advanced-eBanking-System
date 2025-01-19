package org.poo.commands;

import org.poo.banksystem.Account;
import org.poo.banksystem.User;

import java.util.List;

public class SplitPaymentData {
    private final double amount;
    private final String currency;
    private final List<User> users;
    private final List<Account> accounts;
    private final List<String> accountsIbans;
    private final String type;
    private final List<Double> amountForUsers;

    /**
     * Constructor for SplitPaymentData.
     * @param amount the amount
     * @param currency the currency
     * @param users the users
     * @param accounts the accounts
     * @param accountsIbans the accounts ibans
     * @param type the type
     * @param amountForUsers the amount for users
     */
    public SplitPaymentData(final double amount, final String currency, final List<User> users,
                            final List<Account> accounts, final List<String> accountsIbans,
                            final String type, final List<Double> amountForUsers) {
        this.amount = amount;
        this.currency = currency;
        this.users = users;
        this.accounts = accounts;
        this.accountsIbans = accountsIbans;
        this.type = type;
        this.amountForUsers = amountForUsers;
    }

    /**
     * Getter for the amount.
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Getter for the currency.
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Getter for the users.
     * @return the users
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Getter for the accounts.
     * @return the accounts
     */
    public List<Account> getAccounts() {
        return accounts;
    }

    /**
     * Getter for the accounts ibans.
     * @return the accounts ibans
     */
    public List<String> getAccountsIbans() {
        return accountsIbans;
    }

    /**
     * Getter for the type.
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Getter for the amount for users.
     * @return the amount for users
     */
    public List<Double> getAmountForUsers() {
        return amountForUsers;
    }

}
