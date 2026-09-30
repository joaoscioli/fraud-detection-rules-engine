package io.github.joaoscioli.fraud.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record Transaction(
        String id,
        String accountId,
        BigDecimal amount,
        String merchantCountry,
        String cardholderCountry,
        Instant occurredAt
) {
    public Transaction {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(accountId, "accountId must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(merchantCountry, "merchantCountry must not be null");
        Objects.requireNonNull(cardholderCountry, "cardholderCountry must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");

        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
    }
}
