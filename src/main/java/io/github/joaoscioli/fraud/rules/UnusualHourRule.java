package io.github.joaoscioli.fraud.rules;

import io.github.joaoscioli.fraud.domain.Transaction;

import java.time.ZoneOffset;
import java.util.Optional;

public final class UnusualHourRule implements FraudRule {
    private final int startHourInclusive;
    private final int endHourExclusive;
    private final int score;

    public UnusualHourRule(int startHourInclusive, int endHourExclusive, int score) {
        if (startHourInclusive < 0 || startHourInclusive > 23 || endHourExclusive < 1 || endHourExclusive > 24) {
            throw new IllegalArgumentException("hours must be within a 24-hour day");
        }

        if (startHourInclusive >= endHourExclusive) {
            throw new IllegalArgumentException("start hour must be before end hour");
        }

        this.startHourInclusive = startHourInclusive;
        this.endHourExclusive = endHourExclusive;
        this.score = score;
    }

    @Override
    public Optional<RuleResult> evaluate(Transaction transaction) {
        int hour = transaction.occurredAt()
                .atZone(ZoneOffset.UTC)
                .getHour();

        if (hour < startHourInclusive || hour >= endHourExclusive) {
            return Optional.empty();
        }

        return Optional.of(new RuleResult(
                "UNUSUAL_HOUR",
                score,
                "Transaction happened inside a configured unusual-hour window"
        ));
    }
}
