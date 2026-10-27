package io.github.joaoscioli.fraud.rules;

import io.github.joaoscioli.fraud.domain.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.time.Instant;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class HighAmountRuleTest {
    @Test
    void shouldRejectNullThreshold() {
        assertThrows(IllegalArgumentException.class, () -> new HighAmountRule(null, 60));
    }

    @Test
    void shouldRejectZeroThreshold() {
        assertThrows(IllegalArgumentException.class, () -> new HighAmountRule(BigDecimal.ZERO, 60));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-100", "-1", "0", "0.00"})
    void shouldRejectNonPositiveThreshold(String value) {
        BigDecimal threshold = new BigDecimal(value);
        assertThrows(IllegalArgumentException.class, () -> new HighAmountRule(threshold, 60));
    }

    @ParameterizedTest
    @ValueSource(ints = {-100, -1, 0})
    void shouldRejectNonPositiveScore(int score) {
        BigDecimal threshold = new BigDecimal("1000");
        assertThrows(IllegalArgumentException.class, () -> new HighAmountRule(threshold, score));
    }

    @Test
    void shouldNotTriggerWhenAmountEqualsThreshold() {
        HighAmountRule rule = new HighAmountRule(new BigDecimal(1000), 60);
        Transaction transaction = new Transaction(
                "txn-001",
                "account-001",
                new BigDecimal("1000.00"),
                "BR",
                "BR",
                Instant.parse("2023-01-01T00:00:00Z")
        );
        var result = rule.evaluate(transaction);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldTriggerWhenAmountExceedsThreshold() {
        HighAmountRule rule = new HighAmountRule(new BigDecimal("1000.00"), 60);
        Transaction transaction = new Transaction(
                "txn-001",
                "account-001",
                new BigDecimal("1000.01"),
                "BR",
                "BR",
                Instant.parse("2023-01-01T00:00:00Z")
        );
        var result = rule.evaluate(transaction);
        RuleResult ruleResult = result.orElseThrow();

        assertEquals("HIGH_AMOUNT", ruleResult.ruleCode());
        assertEquals(60, ruleResult.score());
    }


    @ParameterizedTest
    @ValueSource(ints = {-100, -1, 0})
    void shouldRejectNonPositiveScoreTest(int score) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CountryMismatchRule(score)
        );
    }
}
