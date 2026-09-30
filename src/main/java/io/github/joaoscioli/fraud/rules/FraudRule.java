package io.github.joaoscioli.fraud.rules;

import io.github.joaoscioli.fraud.domain.Transaction;

import java.util.Optional;

public interface FraudRule {
    Optional<RuleResult> evaluate(Transaction transaction);
}
