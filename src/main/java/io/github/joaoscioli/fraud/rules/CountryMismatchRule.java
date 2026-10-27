package io.github.joaoscioli.fraud.rules;

import io.github.joaoscioli.fraud.domain.Transaction;

import java.util.Optional;

public final class CountryMismatchRule implements FraudRule {
    private final int score;

    public CountryMismatchRule(int score) {
        if (score <= 0) {
            throw new IllegalArgumentException(
                    "Score must be a positive integer"
            );
        }
        this.score = score;

    }

    @Override
    public Optional<RuleResult> evaluate(Transaction transaction) {
        if (transaction.merchantCountry().equalsIgnoreCase(transaction.cardholderCountry())) {
            return Optional.empty();
        }

        return Optional.of(new RuleResult(
                "COUNTRY_MISMATCH",
                score,
                "Merchant country differs from cardholder country"
        ));
    }
}
