package org.poo.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

/**
 * The Transaction class represents a financial transaction with details such as timestamp,
 * description, sender IBAN, receiver IBAN, amount, and transfer type.
 * <p>
 * This class uses the Builder design pattern to create instances of Transaction.
 * The Builder pattern provides a flexible solution to construct complex objects
 * by separating the construction process from the representation.
 * </p>
 */
public final class Transaction {
    private final int timestamp;
    private final String description;
    private final String senderIBAN;
    private final String receiverIBAN;
    private final String accountIBAN;
    private final String amount;
    private final String transferType;
    private final String commerciant;
    private final String account;
    private final String card;
    private final String cardHolder;
    private final String currency;
    private final List<String> involvedAccounts;
    private final String newPlanType;
    private final String splitPaymentType;
    private final List<Double> amountForUsers;
    private final String error;

    /**
     * Private constructor for Transaction.
     * Instances of Transaction should be created using the Builder.
     *
     * @param builder the builder used to construct the Transaction
     */
    private Transaction(final Builder builder) {
        this.timestamp = builder.timestamp;
        this.description = builder.description;
        this.senderIBAN = builder.senderIBAN;
        this.receiverIBAN = builder.receiverIBAN;
        this.accountIBAN = builder.accountIBAN;
        this.newPlanType = builder.newPlanType;
        this.amount = builder.amount;
        this.transferType = builder.transferType;
        this.commerciant = builder.commerciant;
        this.account = builder.account;
        this.card = builder.card;
        this.cardHolder = builder.cardHolder;
        this.currency = builder.currency;
        this.involvedAccounts = builder.involvedAccounts;
        this.splitPaymentType = builder.splitPaymentType;
        this.amountForUsers = builder.amountForUsers;
        this.error = builder.error;
    }

    /**
     * Builder class for constructing instances of Transaction.
     * <p>
     * The Builder design pattern is used to provide a flexible and readable way
     * to create instances of Transaction. It allows setting only the necessary
     * fields and ensures that the Transaction object is immutable once created.
     * </p>
     */
    public static class Builder {
        private int timestamp;
        private String description;
        private String senderIBAN;
        private String receiverIBAN;
        private String accountIBAN;
        private String amount;
        private String transferType;
        private String commerciant;
        private String account;
        private String card;
        private String cardHolder;
        private String currency;
        private String newPlanType;
        private List<String> involvedAccounts = null;
        private String error = null;
        private String splitPaymentType;
        private List<Double> amountForUsers;

        /**
         * Sets the timestamp for the transaction.
         *
         * @param inputTimestamp the timestamp of the transaction
         * @return the builder instance
         */
        public Builder timestamp(final int inputTimestamp) {
            this.timestamp = inputTimestamp;
            return this;
        }

        /**
         * Sets the description for the transaction.
         *
         * @param inputDescription the description of the transaction
         * @return the builder instance
         */
        public Builder description(final String inputDescription) {
            this.description = inputDescription;
            return this;
        }

        /**
         * Sets the sender IBAN for the transaction.
         *
         * @param inputSenderIBAN the sender IBAN of the transaction
         * @return the builder instance
         */
        public Builder senderIBAN(final String inputSenderIBAN) {
            this.senderIBAN = inputSenderIBAN;
            return this;
        }

        /**
         * Sets the receiver IBAN for the transaction.
         *
         * @param inputReceiverIBAN the receiver IBAN of the transaction
         * @return the builder instance
         */
        public Builder receiverIBAN(final String inputReceiverIBAN) {
            this.receiverIBAN = inputReceiverIBAN;
            return this;
        }

        /**
         * Sets the amount for the transaction.
         *
         * @param inputAmount the amount of the transaction
         * @return the builder instance
         */
        public Builder amount(final String inputAmount) {
            this.amount = inputAmount;
            return this;
        }

        /**
         * Sets the transfer type for the transaction.
         *
         * @param inputTransferType the transfer type of the transaction
         * @return the builder instance
         */
        public Builder transferType(final String inputTransferType) {
            this.transferType = inputTransferType;
            return this;
        }

        /**
         * Sets the split payment type for the transaction.
         *
         * @param inputSplitPaymentType the split payment type of the transaction
         * @return the builder instance
         */
        public Builder splitPaymentType(final String inputSplitPaymentType) {
            this.splitPaymentType = inputSplitPaymentType;
            return this;
        }

        /**
         * Sets the amount for users for the transaction.
         *
         * @param inputAmountForUsers the amount for users of the transaction
         * @return the builder instance
         */
        public Builder amountForUsers(final List<Double> inputAmountForUsers) {
            this.amountForUsers = inputAmountForUsers;
            return this;
        }

        /**
         * Sets the commerciant for the transaction.
         *
         * @param inputCommerciant the commerciant of the transaction
         * @return the builder instance
         */
        public Builder commerciant(final String inputCommerciant) {
            this.commerciant = inputCommerciant;
            return this;
        }

        /**
         * Sets the account for the transaction.
         *
         * @param inputAccount the account of the transaction
         * @return the builder instance
         */
        public Builder account(final String inputAccount) {
            this.account = inputAccount;
            return this;
        }

        /**
         * Sets the account IBAN for the transaction.
         *
         * @param inputAccountIBAN the account IBAN of the transaction
         * @return the builder instance
         */
        public Builder accountIBAN(final String inputAccountIBAN) {
            this.accountIBAN = inputAccountIBAN;
            return this;
        }
        /**
         * Sets the card for the transaction.
         *
         * @param inputCard the card of the transaction
         * @return the builder instance
         */
        public Builder card(final String inputCard) {
            this.card = inputCard;
            return this;
        }

        /**
         * Sets the cardHolder for the transaction.
         *
         * @param inputCardHolder the cardHolder of the transaction
         * @return the builder instance
         */
        public Builder cardHolder(final String inputCardHolder) {
            this.cardHolder = inputCardHolder;
            return this;
        }

        /**
         * Sets the currency for the transaction.
         *
         * @param inputCurrency the currency of the transaction
         * @return the builder instance
         */
        public Builder currency(final String inputCurrency) {
            this.currency = inputCurrency;
            return this;
        }

        /**
         * Sets the involved accounts for the transaction.
         *
         * @param inputInvolvedAccounts the involved accounts of the transaction
         * @return the builder instance
         */
        public Builder involvedAccounts(final List<String> inputInvolvedAccounts) {
            this.involvedAccounts = inputInvolvedAccounts;
            return this;
        }

        /**
         * Sets the new plan type for the transaction.
         *
         * @param inputNewPlanType the new plan type of the transaction
         * @return the builder instance
         */
        public Builder newPlanType(final String inputNewPlanType) {
            this.newPlanType = inputNewPlanType;
            return this;
        }
        /**
         * Sets the error for the transaction.
         *
         * @param inputError the error of the transaction
         * @return the builder instance
         */
        public Builder error(final String inputError) {
            this.error = inputError;
            return this;
        }

        /**
         * Builds and returns a Transaction instance.
         *
         * @return a new Transaction instance
         */
        public Transaction build() {
            return new Transaction(this);
        }
    }

    /**
     * Gets the timestamp of the transaction.
     *
     * @return the timestamp of the transaction
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the description of the transaction.
     *
     * @return the description of the transaction
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the sender IBAN of the transaction.
     *
     * @return the sender IBAN of the transaction
     */
    public String getSenderIBAN() {
        return senderIBAN;
    }

    /**
     * Gets the account IBAN of the transaction.
     *
     * @return the account IBAN of the transaction
     */
    public String getAccountIBAN() {
        return accountIBAN;
    }
    /**
     * Gets the receiver IBAN of the transaction.
     *
     * @return the receiver IBAN of the transaction
     */
    public String getReceiverIBAN() {
        return receiverIBAN;
    }

    /**
     * Gets the amount of the transaction.
     *
     * @return the amount of the transaction
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Gets the transfer type of the transaction.
     *
     * @return the transfer type of the transaction
     */
    public String getTransferType() {
        return transferType;
    }

    /**
     * Gets the card of the transaction.
     *
     * @return the card of the transaction
     */
    public String getNewPlanType() {
        return newPlanType;
    }

    /**
     * Gets the account of the transaction.
     *
     * @return the account of the transaction
     */
    public String getAccount() {
        return account;
    }

    /**
     * Gets the commerciant of the transaction.
     *
     * @return the commerciant of the transaction
     */
    public String getCommerciant() {
        return commerciant;
    }

    /**
     * Converts the transaction to a JSON object.
     *
     * @param objectMapper the object mapper used for conversion
     * @return the JSON representation of the transaction
     */
    public ObjectNode toJson(final ObjectMapper objectMapper) {
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("timestamp", timestamp);
        transactionNode.put("description", description);
        if (senderIBAN != null) {
            transactionNode.put("senderIBAN", senderIBAN);
        }
        if (receiverIBAN != null) {
            transactionNode.put("receiverIBAN", receiverIBAN);
        }
        if (amount != null) {
            if (description.equals("Card payment") || description.contains("Split payment")
                    || description.contains("Cash withdrawal")
                    || description.equals("Interest rate income")) {
                transactionNode.put("amount", Double.parseDouble(amount));
            } else {
                transactionNode.put("amount", amount);
            }
        }
        if (transferType != null) {
            transactionNode.put("transferType", transferType);
        }
        if (commerciant != null) {
            transactionNode.put("commerciant", commerciant);
        }
        if (account != null) {
            transactionNode.put("account", account);
        }
        if (accountIBAN != null) {
            transactionNode.put("accountIBAN", accountIBAN);
        }
        if (card != null) {
            transactionNode.put("card", card);
        }
        if (cardHolder != null) {
            transactionNode.put("cardHolder", cardHolder);
        }
        if (currency != null) {
            transactionNode.put("currency", currency);
        }
        if (involvedAccounts != null) {
            transactionNode.putArray("involvedAccounts");
            for (String accountIter : involvedAccounts) {
                transactionNode.withArray("involvedAccounts").add(accountIter);
            }
        }
        if (splitPaymentType != null) {
            transactionNode.put("splitPaymentType", splitPaymentType);
        }
        if (amountForUsers != null) {
            transactionNode.putArray("amountForUsers");
            for (Double amountIter : amountForUsers) {
                transactionNode.withArray("amountForUsers").add(amountIter);
            }
        }
        if (newPlanType != null) {
            transactionNode.put("newPlanType", newPlanType);
        }
        if (error != null) {
            transactionNode.put("error", error);
        }
        return transactionNode;
    }
}
