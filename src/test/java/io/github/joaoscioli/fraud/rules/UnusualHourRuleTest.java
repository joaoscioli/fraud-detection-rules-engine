
package io.github.joaoscioli.fraud.rules;

import io.github.joaoscioli.fraud.domain.Transaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UnusualHourRuleTest {

    @ParameterizedTest
    @ValueSource(ints = {-30, -1, 0})
    void shouldRejectNonPositiveScore(int score) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new UnusualHourRule(0, 5, score)
        );
    }

    @Test
    void shouldRejectEqualStartAndEndHours() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new UnusualHourRule(5, 5, 30)
        );
    }

    @Test
    void shouldAcceptOvernightWindow() {
        assertDoesNotThrow(
                () -> new UnusualHourRule(22, 5, 30)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {22, 23, 0, 2, 4})
    void shouldTriggerDuringOvernightWindow(int hour) {
        UnusualHourRule rule = new UnusualHourRule(22, 5, 30);

        Transaction transaction = transactionAtHour(hour);

        RuleResult result = rule.evaluate(transaction)
                .orElseThrow();

        assertEquals("UNUSUAL_HOUR", result.ruleCode());
        assertEquals(30, result.score());
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 6, 14, 21})
    void shouldNotTriggerOutsideOvernightWindow(int hour) {
        UnusualHourRule rule = new UnusualHourRule(22, 5, 30);

        Transaction transaction = transactionAtHour(hour);

        assertTrue(rule.evaluate(transaction).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 4})
    void shouldTriggerDuringNormalWindow(int hour) {
        UnusualHourRule rule = new UnusualHourRule(0, 5, 30);

        Transaction transaction = transactionAtHour(hour);

        assertTrue(rule.evaluate(transaction).isPresent());
    }
    // test function

    @ParameterizedTest
    @ValueSource(ints = {5, 12, 23})
    void shouldNotTriggerOutsideNormalWindow(int hour) {
        UnusualHourRule rule = new UnusualHourRule(0, 5, 30);

        Transaction transaction = transactionAtHour(hour);

        assertTrue(rule.evaluate(transaction).isEmpty());
    }

    private Transaction transactionAtHour(int hour) {
        Instant occurredAt = Instant.parse(
                String.format("2026-01-01T%02d:00:00Z", hour)
        );

        return new Transaction(
                "txn-001",
                "account-001",
                new BigDecimal("250.00"),
                "BR",
                "BR",
                occurredAt
        );
    }

    @ParameterizedTest
    @CsvSource({
            "-1, 5",
            "24, 5",
            "0, -1",
            "0, 25"
    })
    void shouldRejectInvalidHours(int start, int end) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new UnusualHourRule(start, end, 30)
        );
    }

}
