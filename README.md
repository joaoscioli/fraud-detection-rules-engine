
## Architecture

The fraud detection engine uses independent business rules to evaluate transactions.

Each rule implements the `FraudRule` interface and returns an optional `RuleResult`.

The `FraudDetectionEngine` collects triggered rules, aggregates their scores, and determines the final risk level.

### Main components

- `Transaction`: immutable transaction data.
- `FraudRule`: contract for independent fraud rules.
- `RuleResult`: score and explanation produced by a triggered rule.
- `FraudDetectionEngine`: rule orchestration and risk classification.
- `FraudDecision`: final classification, total score, and triggered rules.

### Risk classification

| Total score | Decision |
|---|---|
| Below review threshold | APPROVED |
| At or above review threshold, below block threshold | REVIEW |
| At or above block threshold | BLOCKED |
