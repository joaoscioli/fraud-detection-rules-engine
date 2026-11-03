# Fraud Detection Rules Engine

A lightweight, rule-based fraud detection engine built with Java 21.

This project explores how to evaluate financial transactions using independent business rules, aggregate risk scores, and produce explainable fraud decisions.

The goal is to demonstrate clean domain modeling, extensible rule-based architecture, defensive programming, and automated testing without introducing unnecessary infrastructure.

## Features

- Independent fraud detection rules
- Configurable risk scores and decision thresholds
- Three risk classifications: `APPROVED`, `REVIEW`, and `BLOCKED`
- Explainable decisions with triggered rule details
- Support for configurable unusual-hour windows, including overnight intervals
- Immutable domain models using Java records
- Domain invariant validation and fail-fast behavior
- Risk score aggregation using `long` arithmetic
- Automated tests with JUnit 5
- Continuous integration with GitHub Actions

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Core application and domain modeling |
| Maven | Build and dependency management |
| JUnit 5 | Unit and parameterized testing |
| GitHub Actions | Continuous integration |
| Java Records | Immutable domain data |

The project intentionally avoids external frameworks and databases in its initial version.

## Architecture

The engine follows a rule-based architecture in which each fraud rule evaluates a transaction independently.

Each rule implements the `FraudRule` interface and returns an `Optional<RuleResult>`.

The `FraudDetectionEngine` collects triggered rules, aggregates their scores, and determines the final risk classification.

### Main Components

| Component | Responsibility |
|---|---|
| `Transaction` | Represents validated transaction data |
| `FraudRule` | Defines the contract for fraud detection rules |
| `HighAmountRule` | Detects transactions above a configured amount |
| `CountryMismatchRule` | Detects differences between merchant and cardholder countries |
| `UnusualHourRule` | Detects transactions occurring during configured UTC hours |
| `RuleResult` | Contains the rule code, score, and explanation |
| `FraudDetectionEngine` | Orchestrates rules and calculates risk |
| `FraudDecision` | Contains the final classification, total score, and triggered rules |
| `FraudDetectionProfiles` | Provides a predefined balanced configuration |

### Rule Contract

```java
public interface FraudRule {
    Optional<RuleResult> evaluate(Transaction transaction);
}
```

Rules are independent of the engine's orchestration logic. New rules can be introduced by implementing this interface and registering them in an engine configuration.

## Fraud Detection Rules

### High Amount Rule

Triggers when the transaction amount exceeds a configured monetary threshold.

The comparison is strictly greater than the threshold.

### Country Mismatch Rule

Triggers when the merchant country differs from the cardholder country.

The comparison ignores letter case.

Country codes are validated as two-letter alphabetic strings. The current implementation does not verify whether a code belongs to an officially assigned country.

### Unusual Hour Rule

Triggers when the transaction occurs within a configured time window.

Time evaluation uses UTC and supports both ordinary and overnight windows.

For example:

```java
new UnusualHourRule(22, 5, 30);
```

This rule assigns 30 risk points to transactions occurring from 22:00 inclusive until 05:00 exclusive, crossing midnight.

Equal start and end hours are rejected.

## Risk Scoring

Each triggered rule contributes a configured positive integer score.

The engine aggregates the scores using `long` arithmetic to prevent integer overflow when combining multiple rule results.

The final score is compared against configurable review and block thresholds.

| Condition | Decision |
|---|---|
| Score below review threshold | `APPROVED` |
| Score at or above review threshold but below block threshold | `REVIEW` |
| Score at or above block threshold | `BLOCKED` |

### Balanced Profile

The predefined balanced profile uses the following configuration:

| Rule | Condition | Risk Score |
|---|---|---:|
| High Amount | Amount greater than 1000.00 | 60 |
| Country Mismatch | Merchant and cardholder countries differ | 50 |
| Unusual Hour | Transaction occurs from 00:00 to 05:00 UTC | 30 |

**Decision thresholds:**

- Review: 50 points
- Block: 100 points

These values are demonstration parameters, not empirically calibrated fraud detection thresholds.

## Usage Example

The following example evaluates a transaction using the predefined balanced profile.

```java
import io.github.joaoscioli.fraud.domain.Transaction;
import io.github.joaoscioli.fraud.rules.FraudDecision;
import io.github.joaoscioli.fraud.rules.FraudDetectionEngine;
import io.github.joaoscioli.fraud.rules.FraudDetectionProfiles;

import java.math.BigDecimal;
import java.time.Instant;

public class FraudDemo {

    public static void main(String[] args) {

        FraudDetectionEngine engine =
                FraudDetectionProfiles.balanced();

        Transaction transaction = new Transaction(
                "txn-001",
                "account-001",
                new BigDecimal("1500.00"),
                "US",
                "BR",
                Instant.parse("2026-01-01T10:00:00Z")
        );

        FraudDecision decision = engine.evaluate(transaction);

        System.out.println("Risk level: " + decision.riskLevel());
        System.out.println("Total score: " + decision.score());

        decision.triggeredRules().forEach(rule ->
                System.out.println(
                        rule.ruleCode() + ": " + rule.score()
                )
        );
    }
}
```

### Expected Output

```text
Risk level: BLOCKED
Total score: 110
HIGH_AMOUNT: 60
COUNTRY_MISMATCH: 50
```

The transaction triggers two rules:

1. The amount exceeds the configured threshold.
2. The merchant and cardholder countries differ.

The resulting score is 110, which meets the blocking threshold.

The unusual-hour rule is not triggered because the transaction occurred at 10:00 UTC.

## Running the Project

### Requirements

- JDK 21 or newer, with support for compiling Java 21
- Apache Maven
- Git

Clone the repository:

```bash
git clone https://github.com/joaoscioli/fraud-detection-rules-engine.git
cd fraud-detection-rules-engine
```

Run the automated tests:

```bash
mvn clean test
```

Run the complete Maven verification lifecycle:

```bash
mvn clean verify
```

The project is currently designed as a Java library and learning project, not as a standalone HTTP service. The usage example can be run as a Java main class within the project.

## Testing Strategy

The test suite focuses on business behavior, validation, and boundary conditions.

Areas covered include:

- Rule activation and non-activation
- Invalid configuration parameters
- Positive risk score invariants
- Review and block threshold boundaries
- Aggregation of multiple rule scores
- Scores exceeding `Integer.MAX_VALUE`
- Normal and overnight time windows
- Invalid transaction data
- Invalid rule result data
- Expected risk classification

JUnit 5 parameterized tests are used where multiple inputs share the same expected behavior.

## Continuous Integration

GitHub Actions runs the Maven verification workflow automatically on pushes and pull requests.

The pipeline:

1. Checks out the repository.
2. Configures Java 21 using Eclipse Temurin.
3. Restores or populates the Maven dependency cache.
4. Runs `mvn --batch-mode clean verify`.

The workflow is defined in:

```text
.github/workflows/ci.yml
```

## Engineering Decisions

### Independent Rules

Each rule implements a shared interface, allowing the engine to combine multiple fraud detection strategies without embedding their business logic directly in the orchestration layer.

### Immutable Domain Models

Java records provide concise representations of transaction data, rule results, and fraud decisions.

Defensive copying protects collections exposed through decisions and engine configuration.

### Fail-Fast Validation

Invalid identifiers, monetary values, country code formats, rule scores, and configurations are rejected when objects are created.

### Long Risk Score Aggregation

Individual rules expose positive integer scores, while the engine uses `long` for aggregation and decision scoring.

This prevents overflow when adding multiple valid integer scores.

### Explainable Decisions

The final decision includes the triggered rules and their explanations, allowing callers to understand which conditions contributed to the classification.

### UTC-Based Time Evaluation

Unusual-hour detection uses UTC to provide deterministic behavior independent of the host machine's timezone.

## Current Limitations

This is an educational and portfolio-oriented rules engine, not a production-ready fraud prevention platform.

The current version does not provide:

- REST API endpoints
- Persistent storage or transaction history
- Authentication or authorization
- External fraud intelligence integrations
- Machine learning models
- Data-driven score calibration
- Monitoring dashboards
- Automatic transaction blocking in a payment system

The engine evaluates transaction data supplied by the caller and returns a decision. It does not execute financial transactions or enforce payment actions.

## Future Improvements

Potential enhancements include:

- A REST adapter using Spring Boot
- Persistent fraud decision audit trails
- Versioned rule configurations and risk policies
- Observability and rule hit-rate metrics
- More sophisticated fraud detection rules
- External data integrations
- Mutation testing and extended property-based testing
- Improved risk scoring strategies

## Project Purpose

This project is part of a software engineering portfolio focused on Java backend development, domain modeling, testing, and software architecture.

It provides a practical foundation for exploring how financial business rules can remain understandable, testable, and extensible as a system evolves.

## License

No license has been specified for this repository. Usage and redistribution permissions remain subject to the repository owner's terms.