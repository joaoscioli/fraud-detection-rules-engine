package io.github.joaoscioli.fraud.rules;

import io.github.joaoscioli.fraud.domain.RiskLevel;

import java.util.List;

public record FraudDecision(RiskLevel riskLevel, int score, List<RuleResult> triggeredRules) {
    public FraudDecision {
        triggeredRules = List.copyOf(triggeredRules);
    }
}
