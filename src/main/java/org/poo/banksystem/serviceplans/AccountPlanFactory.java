package org.poo.banksystem.serviceplans;

/**
 * Factory class for creating instances of different account plans.
 * <p>
 * This class uses the Factory design pattern to create objects of different
 * account plan types based on the provided plan type string. The factory
 * method `getAccountPlan` returns an instance of the appropriate account plan
 * implementation.
 * </p>
 * <p>
 * The Factory design pattern is used here to encapsulate the creation logic
 * for different account plans, making it easier to manage and extend the
 * account plan creation process without modifying the client code.
 * </p>
 */
public final class AccountPlanFactory {
    // Private constructor to prevent instantiation
    private AccountPlanFactory() {
        throw new UnsupportedOperationException("Utility class");
    }
    /**
     * Factory method to get an instance of an account plan based on the plan type.
     *
     * @param planType the type of the account plan (e.g., "student", "silver", "gold")
     * @return an instance of the corresponding AccountPlan implementation
     */
    public static AccountPlan getAccountPlan(final String planType) {
        return switch (planType) {
            case "student" -> new StudentAccount();
            case "silver" -> new SilverAccount();
            case "gold" -> new GoldAccount();
            default -> new StandardAccount();
        };
    }
}

