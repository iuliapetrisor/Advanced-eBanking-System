package org.poo.bankSystem;

import org.poo.transactions.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;


public class Account {
    private final String IBAN;
    private double balance;
    private final String currency;
    private final String type;
    private List<Card> cards;
    private Double minBalance = 0.0;
    private List<Transaction> transactions;

    /**
     * Constructor for the Account class.
     *
     * @param IBAN     the IBAN of the account
     * @param balance  the balance of the account
     * @param currency the currency of the account
     * @param type     the type of the account
     */
    public Account(final String IBAN, final double balance, final String currency,
                   final String type) {
        this.IBAN = IBAN;
        this.balance = balance;
        this.currency = currency;
        this.type = type;
        this.cards = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    /**
     * Getter for the IBAN.
     *
     * @return the IBAN
     */
    public String getIBAN() {
        return IBAN;
    }

    /**
     * Setter for the balance.
     *
     * @param balance the balance
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * Getter for the balance.
     *
     * @return the balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Getter for the currency.
     *
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Getter for the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Getter for the cards.
     *
     * @return the cards
     */
    public List<Card> getCards() { return cards; }

    /**
     * Adds a card to the account.
     *
     * @param card the card to be added
     */
    public void addCard(final Card card) {
        cards.add(card);
    }

    /**
     * Adds funds to the account.
     *
     * @param amount the amount to be added
     */
    public void addFunds(final double amount) {
        this.balance += amount;
    }

    /**
     * Withdraws funds from the account.
     *
     * @param amount the amount to be withdrawn
     */
    public void pay(final double amount) {
        this.balance -= amount;
    }

    /**
     * Deletes a card from the account.
     *
     * @param cardNumber the card number to be deleted
     */
    public void deleteCard(final String cardNumber) {
        Iterator<Card> iterator = cards.iterator();
        while (iterator.hasNext()) {
            Card card = iterator.next();
            if (card.getCardNumber().equals(cardNumber)) {
                iterator.remove();
                break;
            }
        }
    }

    /**
     * Getter for the minimum balance.
     *
     * @return the minimum balance
     */
    public Double getMinBalance() {
        return minBalance;
    }

    /**
     * Setter for the minimum balance.
     *
     * @param minBalance the minimum balance
     */
    public void setMinBalance(final double minBalance) {
        this.minBalance = minBalance;
    }

    /**
     * Getter for the transactions.
     *
     * @return the transactions
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Adds a transaction to the account.
     *
     * @param transaction the transaction to be added
     */
    public void addTransaction(final Transaction transaction) {
        transactions.add(transaction);
    }
}

