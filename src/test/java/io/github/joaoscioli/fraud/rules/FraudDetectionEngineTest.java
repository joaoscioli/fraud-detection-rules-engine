package io.github.joaoscioli.fraud.rules;

import io.github.joaoscioli.fraud.domain.RiskLevel;
import io.github.joaoscioli.fraud.domain.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FraudDetectionEngineTest {
    private final FraudDetectionEngine engine = new FraudDetectionEngine(
            List.of(
                    new HighAmountRule(new BigDecimal("1000.00"), 60),
                    new CountryMismatchRule(50),
                    new UnusualHourRule(0, 5, 30)
            ),
            50,
            100
    );
    private final FraudDetectionEngine balancedEngine = FraudDetectionProfiles.balanced();

    @Test
    void approvesLowRiskTransaction() {
        Transaction transaction = transaction("250.00", "BR", "BR");

        FraudDecision decision = engine.evaluate(transaction);

        assertEquals(RiskLevel.APPROVED, decision.riskLevel());
        assertEquals(0, decision.score());
        assertEquals(0, decision.triggeredRules().size());
    }

    @Test
    void sendsTransactionToReviewWhenSingleRuleIsTriggered() {
        Transaction transaction = transaction("1500.00", "BR", "BR");

        FraudDecision decision = engine.evaluate(transaction);

        assertEquals(RiskLevel.REVIEW, decision.riskLevel());
        assertEquals(60, decision.score());
        assertEquals(List.of("HIGH_AMOUNT"), ruleCodes(decision));
    }

    @Test
    void blocksTransactionWhenCombinedScoreCrossesBlockThreshold() {
        Transaction transaction = transaction("1500.00", "US", "BR");

        FraudDecision decision = engine.evaluate(transaction);

        assertEquals(RiskLevel.BLOCKED, decision.riskLevel());
        assertEquals(110, decision.score());
        assertEquals(List.of("HIGH_AMOUNT", "COUNTRY_MISMATCH"), ruleCodes(decision));
    }

    @Test
    void includesUnusualHourRiskWhenTransactionHappensAtNight() {
        Transaction transaction = new Transaction(
                "txn-002",
                "account-001",
                new BigDecimal("250.00"),
                "BR",
                "BR",
                Instant.parse("2026-01-01T02:30:00Z")
        );

        FraudDecision decision = engine.evaluate(transaction);

        assertEquals(RiskLevel.APPROVED, decision.riskLevel());
        assertEquals(30, decision.score());
        assertEquals(List.of("UNUSUAL_HOUR"), ruleCodes(decision));
    }

    @Test
    void exposesBalancedProfileForCommonRiskEvaluation() {
        Transaction transaction = transaction("1500.00", "US", "BR");

        FraudDecision decision = balancedEngine.evaluate(transaction);

        assertEquals(RiskLevel.BLOCKED, decision.riskLevel());
        assertEquals(110, decision.score());
        assertEquals(List.of("HIGH_AMOUNT", "COUNTRY_MISMATCH"), ruleCodes(decision));
    }

    private static Transaction transaction(String amount, String merchantCountry, String cardholderCountry) {
        return new Transaction(
                "txn-001",
                "account-001",
                new BigDecimal(amount),
                merchantCountry,
                cardholderCountry,
                Instant.parse("2026-01-01T10:00:00Z")
        );
    }

    private static List<String> ruleCodes(FraudDecision decision) {
        return decision.triggeredRules().stream()
                .map(RuleResult::ruleCode)
                .toList();
    }

        @Test
    void shouldReviewWhenScoreEqualsReviewThreshold() {
        // Arrange
        FraudRule rule = transaction ->
                Optional.of(new RuleResult(
                        "TEST_RULE",
                        50,
                        "Test rule"
                ));

        FraudDetectionEngine engine = new FraudDetectionEngine(
                List.of(rule),
                50,
                100
        );

        Transaction transaction = new Transaction(
                "txn-001",
                "account-001",
                new BigDecimal("250.00"),
                "BR",
                "BR",
                Instant.parse("2026-01-01T10:00:00Z")
        );

        // Act
        FraudDecision decision = engine.evaluate(transaction);

        // Assert
        assertEquals(RiskLevel.REVIEW, decision.riskLevel());
        assertEquals(50L, decision.score());
        assertEquals(1, decision.triggeredRules().size());
        assertEquals(
                "TEST_RULE",
                decision.triggeredRules().get(0).ruleCode()
        );
    }


    @Test
    void shouldHandleScoresBeyondIntegerMaxValue() {
        FraudRule ruleA = transaction ->
                Optional.of(new RuleResult(
                        "RULE_A",
                        2_000_000_000,
                        "High risk A"
                ));

        FraudRule ruleB = transaction ->
                Optional.of(new RuleResult(
                        "RULE_B",
                        500_000_000,
                        "High risk B"
                ));

        FraudDetectionEngine engine = new FraudDetectionEngine(
                List.of(ruleA, ruleB),
                50,
                100
        );

        Transaction transaction = transaction(
                "250.00",
                "BR",
                "BR"
        );

        FraudDecision decision = engine.evaluate(transaction);

        assertEquals(2_500_000_000L, decision.score());
        assertEquals(RiskLevel.BLOCKED, decision.riskLevel());
        assertEquals(2, decision.triggeredRules().size());
    }


    @Test
    void shouldBlockWhenScoreEqualsBlockThreshold() {
        // Arrange
        FraudRule rule = transaction ->
                Optional.of(new RuleResult(
                        "TEST_RULE",
                        100,
                        "Score exactly at block threshold"
                ));

        FraudDetectionEngine engine = new FraudDetectionEngine(
                List.of(rule),
                50,
                100
        );

        Transaction transaction = transaction(
                "250.00",
                "BR",
                "BR"
        );

        // Act
        FraudDecision decision = engine.evaluate(transaction);

        // Assert
        assertEquals(RiskLevel.BLOCKED, decision.riskLevel());
        assertEquals(100L, decision.score());
        assertEquals(1, decision.triggeredRules().size());
    }


    @Test
    void shouldReviewWhenScoreIsBelowBlockThreshold() {
        // Arrange
        FraudRule rule = transaction ->
                Optional.of(new RuleResult(
                        "TEST_RULE",
                        99,
                        "Score immediately below block threshold"
                ));

        FraudDetectionEngine engine = new FraudDetectionEngine(
                List.of(rule),
                50,
                100
        );

        Transaction transaction = transaction(
                "250.00",
                "BR",
                "BR"
        );

        // Act
        FraudDecision decision = engine.evaluate(transaction);

        // Assert
        assertEquals(RiskLevel.REVIEW, decision.riskLevel());
        assertEquals(99L, decision.score());
        assertEquals(1, decision.triggeredRules().size());
    }


}
