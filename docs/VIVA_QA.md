# Viva Questions And Answers

## 1. What problem does your project solve?

It solves the problem of lost items on a university campus by giving students a proper way to report lost/found items, search them, request claims, and let admins verify ownership.

## 2. Why did you use Java Swing?

Swing is part of core Java, works well for desktop applications, and is suitable for a university office system where staff can run a desktop app connected to MySQL.

## 3. Why did you use MySQL?

MySQL is reliable for structured data like users, reports, claims, statuses, and notifications. It also supports indexing, constraints, and relationships through foreign keys.

## 4. What is MVC in this project?

The views are Swing screens, controllers handle UI actions, services contain business logic, DAOs contain SQL/JDBC code, and models represent data.

## 5. Why did you create a DAO layer?

DAO separates database logic from UI and business logic. If the database changes later, we mainly update DAO classes instead of changing every screen.

## 6. How is password security handled?

Passwords are not stored directly. The project uses PBKDF2 with SHA-256, salt, and iterations. The stored value contains iterations, salt, and hash.

## 7. Explain the matching algorithm.

The algorithm compares lost and found reports using category match, location proximity, and keyword similarity. Each part has a weight and the final score decides whether a match is shown.

## 8. What is keyword similarity?

The system extracts important words from item name and description, removes common words, and compares overlap using a Jaccard-style method.

## 9. How do you prevent SQL injection?

All dynamic database values are passed using `PreparedStatement`, not string concatenation.

## 10. What are the main tables?

The main tables are `users`, `item_reports`, `claim_requests`, and `notifications`.

## 11. How does the claim workflow work?

A student selects a found item and submits ownership proof. Admin reviews the proof. If approved, the claim becomes `APPROVED` and the item report becomes `CLAIMED`.

## 12. What are the roles?

There are two roles: `STUDENT` and `ADMIN`. Students report/search/claim items. Admins approve reports, handle claims, and manage users.

## 13. What validations are implemented?

The project validates required fields, email format, phone number length, password length, and date format.

## 14. What makes this project scalable?

It has separate layers, modular classes, enums for statuses, reusable UI components, indexed database columns, and service classes for business rules.

## 15. What are possible future improvements?

Future improvements include email notifications, OTP verification, image upload to server storage, QR claim slips, department-level admins, and machine-learning matching.

## 16. Why are statuses needed?

Statuses track the lifecycle of an item: pending approval, approved, matched, claimed, or rejected.

## 17. What is JDBC?

JDBC is Java Database Connectivity. It allows Java programs to connect to databases, run SQL queries, and read results.

## 18. Why did you use enums?

Enums make fixed values like role, report type, report status, and claim status type-safe and easier to maintain.

## 19. How does admin approval improve reliability?

It prevents fake or incomplete reports from appearing directly to everyone and makes the system more realistic for a university environment.

## 20. What is the biggest learning from this project?

The biggest learning is how to build a complete Java application with UI, database, validation, authentication, roles, and real business workflows.
