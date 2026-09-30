# Interview Guide

## 30-Second Pitch

This project models fraud detection as a small Java rule engine. The important
part is not only detecting suspicious transactions, but making each decision
explainable, testable, and easy to evolve.

## Strong Talking Points

- Rules are isolated, so new fraud signals can be added without rewriting the engine.
- Decisions include explanations, which matters for auditability and support.
- Tests validate business behavior instead of only implementation details.
- The design can evolve toward Spring Boot, persistence, messaging, or ML scoring.

## Questions To Expect

- How would you avoid too many false positives?
- How would you audit a fraud decision?
- How would you prioritize rules?
- When would rules stop being enough?
- How would this become a production API?
