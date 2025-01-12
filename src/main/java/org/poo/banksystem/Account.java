package org.poo.banksystem;

import org.poo.banksystem.serviceplans.AccountPlan;
import org.poo.banksystem.serviceplans.AccountPlanFactory;
import org.poo.banksystem.strategies.CashbackStrategy;
import org.poo.banksystem.strategies.NrOfTransactionsStrategy;
import org.poo.banksystem.strategies.SpendingThresholdStrategy;
import org.poo.transactions.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;


public class Account {
    private final String iban;
    private double balance;
    private final String currency;
    private final String type;
    private List<Card> cards;
    private Double minBalance = 0.0;
    private List<Transaction> transactions;
    private String planType;
    private AccountPlan accountPlan;
    private int nrOfTransactionsForCashback = 0;
    private double totalSpendingsForCashback = 0.0;
    private Map<String, Double> discounts = new HashMap<>();

    /**
     * Constructor for the Account class.
     *
     * @param iban     the iban of the account
     * @param balance  the balance of the account
     * @param currency the currency of the account
     * @param type     the type of the account
     */
    public Account(final String iban, final double balance, final String currency,
                   final String type) {
        this.iban = iban;
        this.balance = balance;
        this.currency = currency;
        this.type = type;
        this.cards = new ArrayList<>();
        this.transactions = new ArrayList<>();
        discounts.put("Food", 0.0);
        discounts.put("Clothes", 0.0);
        discounts.put("Tech", 0.0);
    }

    /**
     * Getter for the IBAN.
     *
     * @return the IBAN
     */
    public String getIBAN() {
        return iban;
    }

    /**
     * Setter for the balance.
     *
     * @param balance the balance
     */
    public void setBalance(final double balance) {
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
    public List<Card> getCards() {
        return cards;
    }

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

    /**
     * Setter for the plan type and initializes the account plan.
     *
     * @param planType the plan type
     */
    public void setPlanType(final String planType) {
        this.planType = planType;
        this.accountPlan = AccountPlanFactory.getAccountPlan(planType);
    }

    /**
     * Getter for the plan type.
     *
     * @return the plan type
     */
    public String getPlanType() {
        return planType;
    }

    /**
     * Getter for the transaction fee.
     *
     * @param amount the amount of the transaction
     * @return the transaction fee
     */
    public double getTransactionFee(final double amount) {
        return accountPlan.getTransactionFee(amount);
    }

    /**
     * Getter for the total number of transactions made to
     * commerciants with the nrOfTransactions cashback strategy.
     * @return the number of transactions`
     */
    public int getNrOfTransactionsForCashback() {
        return nrOfTransactionsForCashback;
    }


    /**
     * Getter for the total spendings made to
     * commerciants with the spendingsThreshold cashback strategy.
     *
     * @return the total spendings
     */
    public double getTotalSpendingsForCashback() {
        return totalSpendingsForCashback;
    }

    /**
     * Processes a transaction depending on commerciant's cashback strategy.
     *
     * @param transactionAmount the amount paid for the transaction
     * @param amountInRon   the amount paid for the transaction in RON
     * @param strategy the cashback strategy to be used
     * @return the cashback amount
     */
    public double processTransactionStrategy(final double transactionAmount,
                                             final double amountInRon,
                                             final String strategy) {
        CashbackStrategy cashbackStrategy;
        if (strategy.equals("nrOfTransactions")) {
            this.nrOfTransactionsForCashback++;
            cashbackStrategy = new NrOfTransactionsStrategy();
            return cashbackStrategy.calculateCashback(this);
        } else if (strategy.equals("spendingThreshold")) {
            this.totalSpendingsForCashback += amountInRon;
            cashbackStrategy = new SpendingThresholdStrategy(transactionAmount);
            return cashbackStrategy.calculateCashback(this);
        }
        return 0;
    }

    /**
     * Checks if a certain category of commerciants has a discount.
     *
     * @param category the category of the commerciant to be checked
     * @return true if the category has a discount, false otherwise
     */
    public boolean hasDiscount(final String category) {
        return discounts.getOrDefault(category, 0.0) > 0;
    }

    /**
     * Adds a new discount for a certain category of commerciants.
     *
     * @param category the category of the commerciant
     * @param value    the value of the discount
     */
    public void setDiscount(final String category, final double value) {
        discounts.put(category, value);
    }
}

