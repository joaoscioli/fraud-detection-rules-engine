# Fraud Detection Rules Engine

Java portfolio project for a rule-based fraud detection engine.

## Purpose

This repository demonstrates how to model fraud detection as a small, testable
domain instead of hiding risk decisions inside unstructured conditionals.

## Technical Focus

- Java 21
- Rule engine design
- Domain modeling
- Testable business rules
- Risk scoring
- Clear decision explanations

## Current Scope

The first version evaluates card transactions with independent rules and returns
a decision containing:

- final risk status;
- total score;
- triggered rule explanations.

## Run Tests

```bash
mvn test
```

## Interview Signal

This project is useful in interviews because it gives space to discuss business
rules, test strategy, extensibility, false positives, auditability, and how to
evolve from rule-based decisions toward event-driven or ML-assisted detection.

## Next Steps

- Add more fraud rules.
- Add rule severity levels.
- Add JSON/API adapter with Spring Boot.
- Add persistence for decision audit trails.
- Add observability around rule hit rates.
