package io.github.joaoscioli.fraud.rules;

import io.github.joaoscioli.fraud.domain.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CountryMismatchRuleTest {
    @Test
    void shouldNotTriggerWhenCountriesMatch() {
        CountryMismatchRule rule = new CountryMismatchRule(50);

        Transaction transaction = new Transaction(
                "txn-001",
                "account-001",
                new BigDecimal("250.00"),
                "br",
                "BR",
                Instant.parse("2026-01-01T10:00:00Z")
        );
        var result = rule.evaluate(transaction);
        assertTrue(result.isEmpty());
    }


    @Test
    void shouldTriggerWhenCountriesDiffer() {
        CountryMismatchRule rule = new CountryMismatchRule(50);

        Transaction transaction = new Transaction(
                "txn-001",
                "account-001",
                new BigDecimal("250.00"),
                "US",
                "BR",
                Instant.parse("2026-01-01T10:00:00Z")
        );

        var result = rule.evaluate(transaction);

        RuleResult ruleResult = result.orElseThrow();

        assertEquals("COUNTRY_MISMATCH", ruleResult.ruleCode());
        assertEquals(50, ruleResult.score());
    }

}
