package org.poo.banksystem;

import org.poo.fileio.UserInput;
import org.poo.transactions.Transaction;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class User {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String birthDate;
    private final String occupation;
    private List<Account> accounts;
    private String planType;
    private List<Transaction> transactions;
    private int silverTransactions = 0;
    private Map<String, String> aliases = new HashMap<>();
    private Map<Integer, String> splitPaymentResponses = new LinkedHashMap<>();
    /**
     * Constructor for the User class.
     *
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @param email     the email of the user
     * @param birthDate the birthdate of the user
     * @param occupation the occupation of the user
     */
    public User(final String firstName, final String lastName, final String email,
                final String birthDate, final String occupation) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
        this.occupation = occupation;
        this.accounts = new ArrayList<>();
        this.transactions = new ArrayList<>();
        if (occupation.equals("student")) {
            this.planType = "student";
        } else {
            this.planType = "standard";
        }
    }

    /**
     * Constructor for the User class.
     *
     * @param userInput the user input
     */
    public User(final UserInput userInput) {
        this.firstName = userInput.getFirstName();
        this.lastName = userInput.getLastName();
        this.email = userInput.getEmail();
        this.birthDate = userInput.getBirthDate();
        this.occupation = userInput.getOccupation();
        this.accounts = new ArrayList<>();
        this.transactions = new ArrayList<>();
        if (occupation.equals("student")) {
            this.planType = "student";
        } else {
            this.planType = "standard";
        }
    }

    /**
     * Getter for the first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Getter for the last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Getter for the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Getter for the birth date.
     *
     * @return the birth date
     */
    public String getBirthDate() {
        return birthDate;
    }

    /**
     * Getter for the occupation.
     *
     * @return the occupation
     */
    public String getOccupation() {
        return occupation;
    }

    /**
     * Getter for the accounts.
     *
     * @return the accounts
     */
    public List<Account> getAccounts() {
        return accounts;
    }

    /**
     * Setter for the plan type for all accounts.
     *
     * @param inputPlanType the plan type
     */
    public void setPlanTypeForAllAccounts(final String inputPlanType) {
        for (Account account : accounts) {
            account.setPlanType(inputPlanType);
        }
        this.planType = inputPlanType;
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
     * Adds an account to the user.
     *
     * @param account the account to be added
     */
    public void addAccount(final Account account) {
        accounts.add(account);
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
     * Getter for the split payment responses.
     *
     * @return the split payment responses
     */
    public Map<Integer, String> getSplitPaymentResponses() {
        return splitPaymentResponses;
    }

    /**
     * Adds a transaction to the user.
     *
     * @param transaction the transaction to be added
     */
    public void addTransaction(final Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * Setter for the alias.
     *
     * @param alias the alias
     * @param account the account
     */
    public void setAlias(final String alias, final String account) {
        aliases.put(alias, account);
    }

    /**
     * Getter for the account by alias.
     *
     * @param alias the alias
     * @return the account
     */
    public String getAccountByAlias(final String alias) {
        return aliases.get(alias);
    }

    /**
     * Calculates the age of the user based on their birthdate.
     *
     * @return the age of the user as of January 2025
     */
    public int getAge() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedBirthDate = LocalDate.parse(this.birthDate, formatter);
        LocalDate currentDate = LocalDate.now();
        return Period.between(parsedBirthDate, currentDate).getYears();
    }

    /**
     * Gets the first classic account in the specified currency
     * (used for the withdrawSavings command).
     *
     * @param currency the currency
     * @return the first classic account in the specified currency
     */
    public Account getFirstClassicAccountInCurrency(final String currency) {
        for (Account account : accounts) {
            if (account.getType().equals("classic") && account.getCurrency().equals(currency)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Checks if the user is a student.
     *
     * @return true if the user is a student, false otherwise
     */
    public boolean isStudent() {
        return occupation.equals("student");
    }

    /**
     * Getter for the number of silver transactions.
     *
     * @return the number of silver transactions
     */
    public int getSilverTransactions() {
        return silverTransactions;
    }

    /**
     * Increments the number of silver transactions.
     */
    public void incrementSilverTransactions() {
        silverTransactions++;
    }

    /**
     * Accepts the first pending split payment.
     */
    public int acceptFirstPendingSplitPayment() {
        for (Map.Entry<Integer, String> entry : splitPaymentResponses.entrySet()) {
            if (entry.getValue().equals("pending")) {
                entry.setValue("accepted");
                return entry.getKey();
            }
        }
        return -1;
    }
}
