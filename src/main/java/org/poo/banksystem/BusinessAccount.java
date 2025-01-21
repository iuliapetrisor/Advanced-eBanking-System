package org.poo.banksystem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BusinessAccount extends Account {
    private final User owner;
    private final List<User> managers = new ArrayList<>();
    private final List<User> employees = new ArrayList<>();
    private double depositLimit;
    private double spendingLimit;
    private Map<User, Double> managerSpendings = new LinkedHashMap<>();
    private Map<User, Double> employeeSpendings = new LinkedHashMap<>();
    private Map<User, Double> managerDeposits = new LinkedHashMap<>();
    private Map<User, Double> employeeDeposits = new LinkedHashMap<>();
    private Map<Commerciant, Double> commerciantPayments = new LinkedHashMap<>();
    private Map<Commerciant, List<User>> commerciantEmployees = new LinkedHashMap<>();
    private Map<Commerciant, List<User>> commerciantManagers = new LinkedHashMap<>();

    /**
     * Constructor for the BusinessAccount class.
     *
     * @param iban     the iban of the account
     * @param balance  the balance of the account
     * @param currency the currency of the account
     * @param owner    the owner of the account
     */
    public BusinessAccount(final String iban, final double balance,
                           final String currency, final User owner,
                           final double initialLimits) {
        super(iban, balance, currency, "business");
        this.owner = owner;
        this.depositLimit = initialLimits;
        this.spendingLimit = initialLimits;
    }

    /**
     * Getter for the type.
     *
     * @return the type
     */
    public String getType() {
        return "business";
    }

    /**
     * Getter for the owner.
     *
     * @return the owner
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Getter for the managers.
     *
     * @return the managers
     */
    public List<User> getManagers() {
        return managers;
    }

    /**
     * Getter for the employees.
     *
     * @return the employees
     */
    public List<User> getEmployees() {
        return employees;
    }

    /**
     * Getter for the deposit limit.
     *
     * @return the deposit limit
     */
    public double getDepositLimit() {
        return depositLimit;
    }

    /**
     * Setter for the deposit limit.
     *
     * @param depositLimit the deposit limit
     */
    public void setDepositLimit(final double depositLimit) {
        this.depositLimit = depositLimit;
    }

    /**
     * Getter for the spending limit.
     *
     * @return the spending limit
     */
    public double getSpendingLimit() {
        return spendingLimit;
    }

    /**
     * Setter for the spending limit.
     *
     * @param spendingLimit the spending limit
     */
    public void setSpendingLimit(final double spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    /**
     * Adds a manager to the account.
     *
     * @param manager the manager to be added
     */
    public void addManager(final User manager) {
        managers.add(manager);
    }

    /**
     * Adds an employee to the account.
     *
     * @param employee the employee to be added
     */
    public void addEmployee(final User employee) {
        employees.add(employee);
    }

    /**
     * Getter for the manager spendings.
     *
     * @return the manager spendings
     */
    public Map<User, Double> getManagerSpendings() {
        return managerSpendings;
    }

    /**
     * Getter for the employee spendings.
     *
     * @return the employee spendings
     */
    public Map<User, Double> getEmployeeSpendings() {
        return employeeSpendings;
    }

    /**
     * Getter for the manager deposits.
     *
     * @return the manager deposits
     */
    public Map<User, Double> getManagerDeposits() {
        return managerDeposits;
    }

    /**
     * Getter for the employee deposits.
     *
     * @return the employee deposits
     */
    public Map<User, Double> getEmployeeDeposits() {
        return employeeDeposits;
    }

    /**
     * Getter for the commerciant payments.
     *
     * @return the commerciant payments
     */
    public Map<Commerciant, Double> getCommerciantPayments() {
        return commerciantPayments;
    }

    /**
     * Getter for the commerciant employees.
     *
     * @return the commerciant employees
     */
    public Map<Commerciant, List<User>> getCommerciantEmployees() {
        return commerciantEmployees;
    }

    /**
     * Getter for the commerciant managers.
     *
     * @return the commerciant managers
     */
    public Map<Commerciant, List<User>> getCommerciantManagers() {
        return commerciantManagers;
    }
}
