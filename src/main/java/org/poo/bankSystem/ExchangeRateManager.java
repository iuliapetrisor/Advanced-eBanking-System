package org.poo.bankSystem;

import org.poo.fileio.ExchangeInput;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExchangeRateManager {
    public static final int SCALE_PRECISION = 20;
    private final Map<String, Map<String, Double>> exchangeRates = new HashMap<>();
    /**
     * Constructor for ExchangeRateManager.
     * @param exchangeInputs the exchange rates input
     */
    public ExchangeRateManager(final ExchangeInput[] exchangeInputs) {
        Set<String> currencies = new HashSet<>();
        for (ExchangeInput exchangeInput : exchangeInputs) {
            exchangeRates
                    .computeIfAbsent(exchangeInput.getFrom(), k -> new HashMap<>())
                    .put(exchangeInput.getTo(), exchangeInput.getRate());
            exchangeRates
                    .computeIfAbsent(exchangeInput.getTo(), k -> new HashMap<>())
                    .put(exchangeInput.getFrom(), 1.0 / exchangeInput.getRate());

            currencies.add(exchangeInput.getFrom());
            currencies.add(exchangeInput.getTo());
        }
        calculateAllExchangeRates(currencies);
    }


    /**
     * Calculates all possible exchange rates using the Floyd-Warshall algorithm.
     * @param currencies the set of all currencies
     */
    private void calculateAllExchangeRates(final Set<String> currencies) {
        for (String k : currencies) {
            for (String i : currencies) {
                for (String j : currencies) {
                    if (exchangeRates.containsKey(i) && exchangeRates.get(i).containsKey(k)
                            && exchangeRates.containsKey(k)
                            && exchangeRates.get(k).containsKey(j)) {
                        double newRate = exchangeRates.get(i).get(k) * exchangeRates.get(k).get(j);
                        exchangeRates
                                .computeIfAbsent(i, x -> new HashMap<>())
                                .put(j, newRate);
                    }
                }
            }
        }
    }

    /**
     * Converts an amount from one currency to another.
     * @param amount the amount to be converted
     * @param from the source currency
     * @param to the target currency
     * @return the converted amount
     */
    public double convert(final double amount, final String from, final String to) {
        if (from.equals(to)) {
            return amount;
        }
        if (exchangeRates.containsKey(from) && exchangeRates.get(from).containsKey(to)) {
            BigDecimal amountBD = BigDecimal.valueOf(amount);
            BigDecimal rateBD = BigDecimal.valueOf(exchangeRates.get(from).get(to));
            BigDecimal convertedAmount = amountBD.multiply(rateBD);
            return Math.round(convertedAmount.setScale(SCALE_PRECISION, RoundingMode.HALF_UP)
                    .doubleValue() * 100000000000000.0) / 100000000000000.0;
        }
        throw new IllegalArgumentException("Invalid conversion");
    }
}
