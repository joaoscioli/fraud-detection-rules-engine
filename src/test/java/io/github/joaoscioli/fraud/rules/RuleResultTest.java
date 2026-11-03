
package io.github.joaoscioli.fraud.rules;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class RuleResultTest {

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void shouldRejectBlankRuleCode(String ruleCode) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RuleResult(
                        ruleCode,
                        50,
                        "High risk transaction"
                )
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void shouldRejectBlankExplanation(String explanation) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RuleResult(
                        "HIGH_AMOUNT",
                        50,
                        explanation
                )
        );
    }

    @Test
    void shouldRejectNullRuleCode() {
        assertThrows(
                NullPointerException.class,
                () -> new RuleResult(
                        null,
                        50,
                        "High risk transaction"
                )
        );
    }

    @Test
    void shouldRejectNullExplanation() {
        assertThrows(
                NullPointerException.class,
                () -> new RuleResult(
                        "HIGH_AMOUNT",
                        50,
                        null
                )
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {-100, -1, 0})
    void shouldRejectNonPositiveScore(int score) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RuleResult(
                        "HIGH_AMOUNT",
                        score,
                        "High risk transaction"
                )
        );
    }

    @Test
    void shouldAcceptValidRuleResult() {
        RuleResult result = new RuleResult(
                "HIGH_AMOUNT",
                60,
                "Transaction amount exceeds threshold"
        );

        assertAll(
                () -> assertEquals("HIGH_AMOUNT", result.ruleCode()),
                () -> assertEquals(60, result.score()),
                () -> assertEquals(
                        "Transaction amount exceeds threshold",
                        result.explanation()
                )
        );
    }
}
