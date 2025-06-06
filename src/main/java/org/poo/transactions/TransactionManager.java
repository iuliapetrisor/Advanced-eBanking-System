package org.poo.transactions;

import org.poo.banksystem.Account;
import org.poo.banksystem.User;
import java.util.List;

public class TransactionManager {
    private final List<User> users;

    /**
     * Constructor for TransactionManager.
     *
     * @param users the users
     */
    public TransactionManager(final List<User> users) {
        this.users = users;
    }

    /**
     * Adds a transaction to the user with the specified email.
     *
     * @param email the email of the user
     * @param transaction the transaction to be added
     */
    public void addTransactionToUser(final String email, final Transaction transaction) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                user.addTransaction(transaction);
                break;
            }
        }
    }

    /**
     * Adds a transaction to the account with the specified IBAN.
     *
     * @param email the email of the user
     * @param iban the IBAN of the account
     * @param transaction the transaction to be added
     */
    public void addTransactionToAccount(final String email, final String iban,
                                        final Transaction transaction) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                for (Account account : user.getAccounts()) {
                    if (account.getIBAN().equals(iban)) {
                        account.addTransaction(transaction);
                        break;
                    }
                }
            }
        }
    }
}
