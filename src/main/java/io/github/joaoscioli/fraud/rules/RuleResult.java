package io.github.joaoscioli.fraud.rules;

public record RuleResult(String ruleCode, int score, String explanation) {
    public RuleResult {
        if (score <= 0) {
            throw new IllegalArgumentException("score must be positive");
        }
    }
}
