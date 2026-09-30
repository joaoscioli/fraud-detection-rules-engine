package io.github.joaoscioli.fraud.rules;

import io.github.joaoscioli.fraud.domain.RiskLevel;
import io.github.joaoscioli.fraud.domain.Transaction;

import java.util.List;
import java.util.Objects;

public final class FraudDetectionEngine {
    private final List<FraudRule> rules;
    private final int reviewThreshold;
    private final int blockThreshold;

    public FraudDetectionEngine(List<FraudRule> rules, int reviewThreshold, int blockThreshold) {
        this.rules = List.copyOf(Objects.requireNonNull(rules, "rules must not be null"));
        this.reviewThreshold = reviewThreshold;
        this.blockThreshold = blockThreshold;

        if (reviewThreshold <= 0 || blockThreshold <= reviewThreshold) {
            throw new IllegalArgumentException("thresholds must be positive and block must be greater than review");
        }
    }

    public FraudDecision evaluate(Transaction transaction) {
        List<RuleResult> triggeredRules = rules.stream()
                .map(rule -> rule.evaluate(transaction))
                .flatMap(OptionalUtils::stream)
                .toList();

        int score = triggeredRules.stream()
                .mapToInt(RuleResult::score)
                .sum();

        return new FraudDecision(resolveRiskLevel(score), score, triggeredRules);
    }

    private RiskLevel resolveRiskLevel(int score) {
        if (score >= blockThreshold) {
            return RiskLevel.BLOCKED;
        }

        if (score >= reviewThreshold) {
            return RiskLevel.REVIEW;
        }

        return RiskLevel.APPROVED;
    }
}
