package io.github.joaoscioli.fraud.rules;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

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

}
