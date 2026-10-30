package io.github.joaoscioli.fraud.rules;

import io.github.joaoscioli.fraud.domain.Transaction;

import java.time.ZoneOffset;
import java.util.Optional;

public final class UnusualHourRule implements FraudRule {
    private final int startHourInclusive;
    private final int endHourExclusive;
    private final int score;


    public UnusualHourRule(
            int startHourInclusive,
            int endHourExclusive,
            int score
    ) {
        if (startHourInclusive < 0 || startHourInclusive > 23
                || endHourExclusive < 0 || endHourExclusive > 24) {
            throw new IllegalArgumentException(
                    "Hours must be within a 24-hour day"
            );
        }

        if (startHourInclusive == endHourExclusive) {
            throw new IllegalArgumentException(
                    "Start and end hours must be different"
            );
        }

        if (score <= 0) {
            throw new IllegalArgumentException(
                    "Score must be greater than zero"
            );
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

        boolean crossesMidnight =
                startHourInclusive > endHourExclusive;

        boolean insideWindow;

        if (crossesMidnight) {
            insideWindow =
                    hour >= startHourInclusive
                            || hour < endHourExclusive;
        } else {
            insideWindow =
                    hour >= startHourInclusive
                            && hour < endHourExclusive;
        }

        if (!insideWindow) {
            return Optional.empty();
        }

        return Optional.of(new RuleResult(
                "UNUSUAL_HOUR",
                score,
                "Transaction occurred during unusual hours"
        ));
    }

}
