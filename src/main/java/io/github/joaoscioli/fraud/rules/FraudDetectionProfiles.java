package io.github.joaoscioli.fraud.rules;

import java.math.BigDecimal;
import java.util.List;

public final class FraudDetectionProfiles {
    private FraudDetectionProfiles() {
    }

    public static FraudDetectionEngine balanced() {
        return new FraudDetectionEngine(
                List.of(
                        new HighAmountRule(new BigDecimal("1000.00"), 60),
                        new CountryMismatchRule(50),
                        new UnusualHourRule(0, 5, 30)
                ),
                50,
                100
        );
    }
}
