
package io.github.joaoscioli.fraud.rules;

import java.util.Objects;

public record RuleResult(
        String ruleCode,
        int score,
        String explanation
) {
    public RuleResult {
        Objects.requireNonNull(
                ruleCode,
                "ruleCode must not be null"
        );

        Objects.requireNonNull(
                explanation,
                "explanation must not be null"
        );

        if (ruleCode.isBlank()) {
            throw new IllegalArgumentException(
                    "ruleCode must not be blank"
            );
        }

        if (explanation.isBlank()) {
            throw new IllegalArgumentException(
                    "explanation must not be blank"
            );
        }

        if (score <= 0) {
            throw new IllegalArgumentException(
                    "score must be positive"
            );
        }
    }
}
