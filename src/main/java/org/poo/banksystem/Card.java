package org.poo.banksystem;

public class Card {
    private final String cardNumber;
    private String status;
    private boolean oneTime = false;

    /**
     * Constructor for the Card class.
     *
     * @param cardNumber the card number
     * @param status     the status of the card
     */
    public Card(final String cardNumber, final String status) {
        this.cardNumber = cardNumber;
        this.status = status;
    }

    /**
     * Getter for the one time status of the card.
     *
     * @return the one time status of the card
     */
    public boolean isOneTime() {
        return oneTime;
    }

    /**
     * Setter for the one time status of the card.
     *
     * @param oneTime the one time status of the card
     */
    public void setOneTime(final boolean oneTime) {
        this.oneTime = oneTime;
    }

    /**
     * Getter for the card number.
     *
     * @return the card number
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Getter for the status of the card.
     *
     * @return the status of the card
     */
    public String getStatus() {
        return status;
    }

    /**
     * Changes the status of the card.
     *
     * @param newStatus the new status of the card
     */
    public void changeStatus(final String newStatus) {
        this.status = newStatus;
    }

    /**
     * Checks if the card is active.
     *
     * @return true if the card is active, false otherwise
     */
    public boolean isActive() {
        return status.equals("active");
    }

}
