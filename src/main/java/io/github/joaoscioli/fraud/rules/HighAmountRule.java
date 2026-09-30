package io.github.joaoscioli.fraud.rules;

import io.github.joaoscioli.fraud.domain.Transaction;

import java.math.BigDecimal;
import java.util.Optional;

public final class HighAmountRule implements FraudRule {
    private final BigDecimal threshold;
    private final int score;

    public HighAmountRule(BigDecimal threshold, int score) {
        this.threshold = threshold;
        this.score = score;
    }

    @Override
    public Optional<RuleResult> evaluate(Transaction transaction) {
        if (transaction.amount().compareTo(threshold) <= 0) {
            return Optional.empty();
        }

        return Optional.of(new RuleResult(
                "HIGH_AMOUNT",
                score,
                "Transaction amount is above the configured threshold"
        ));
    }
}
