# Architecture

## Design Goal

The engine keeps fraud rules isolated and composable. Each rule evaluates a
transaction and returns a scored signal when it detects suspicious behavior.

## Core Concepts

- `Transaction`: input data being evaluated.
- `FraudRule`: independent business rule.
- `RuleResult`: score and explanation produced by a rule.
- `FraudDecision`: final result returned by the engine.
- `FraudDetectionEngine`: orchestrates rules and aggregates score.

## Why Rule-Based First

A rule-based engine is transparent, testable, and easy to explain in a technical
interview. It is also a practical baseline before introducing machine learning
or streaming event processing.

## Future Evolution

- Spring Boot REST API.
- Rule configuration by risk policy.
- Decision audit trail in a database.
- Metrics for rule hit rate and false-positive analysis.
