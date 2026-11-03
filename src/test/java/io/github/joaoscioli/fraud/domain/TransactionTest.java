package io.github.joaoscioli.fraud.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionTest {

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void shouldRejectBlankTransactionId(String id) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        id,
                        "account-001",
                        new BigDecimal("250.00"),
                        "BR",
                        "BR",
                        Instant.parse("2026-01-01T10:00:00Z")
                )
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void shouldRejectBlankAccountId(String accountId) {
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction(
                        "transaction-001",
                        accountId,
                        new BigDecimal("250.00"),
                        "BR",
                        "BR",
                        Instant.parse("2026-01-01T10:00:00Z")
                )
        );
    }


    @ParameterizedTest
    @ValueSource(strings = {"", "B", "BRA", "12", "B1", " BR "})
    void shouldRejectInvalidCardholderCountry(String country) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        "txn-001",
                        "account-001",
                        new BigDecimal("250.00"),
                        "BR",
                        country,
                        Instant.parse("2026-01-01T10:00:00Z")
                )
        );
    }



    @ParameterizedTest
    @ValueSource(strings = {"", "B", "BRA", "12", "B1", " BR "})
    void shouldRejectInvalidMerchantCountry(String country) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        "txn-001",
                        "account-001",
                        new BigDecimal("250.00"),
                        country,
                        "BR",
                        Instant.parse("2026-01-01T10:00:00Z")
                )
        );
    }


    @Test
    void shouldAcceptValidCountryCodes() {
        Transaction transaction = new Transaction(
                "txn-001",
                "account-001",
                new BigDecimal("250.00"),
                "BR",
                "us",
                Instant.parse("2026-01-01T10:00:00Z")
        );

        assertEquals("BR", transaction.merchantCountry());
        assertEquals("us", transaction.cardholderCountry());
    }


}

