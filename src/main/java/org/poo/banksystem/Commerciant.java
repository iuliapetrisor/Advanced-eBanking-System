package org.poo.banksystem;

import org.poo.fileio.CommerciantInput;

public class Commerciant {
    private String commerciant;
    private int id;
    private String account;
    private String type;
    private String cashbackStrategy;

    /**
     * Constructor for the Commerciant class.
     * @param commerciantInput the input for the commerciant
     */
    public Commerciant(final CommerciantInput commerciantInput) {
        this.commerciant = commerciantInput.getCommerciant();
        this.id = commerciantInput.getId();
        this.account = commerciantInput.getAccount();
        this.type = commerciantInput.getType();
        this.cashbackStrategy = commerciantInput.getCashbackStrategy();
    }

    /**
     * Getter for the name of the commerciant.
     * @return the name of the commerciant
     */
    public String getName() {
        return commerciant;
    }

    /**
     * Getter for the id of the commerciant.
     * @return the id of the commerciant
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the account of the commerciant.
     * @return the account of the commerciant
     */
    public String getAccount() {
        return account;
    }

    /**
     * Getter for the type of the commerciant.
     * @return the type of the commerciant
     */
    public String getType() {
        return type;
    }

    /**
     * Getter for the cashback strategy name of the commerciant.
     * @return the cashback strategy name of the commerciant
     */
    public String getCashbackStrategyName() {
        return cashbackStrategy;
    }
}
