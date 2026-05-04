# Step-by-Step Implementation Guide

## 1. Install Required Software

Install these first:

- JDK 17 or newer
- IntelliJ IDEA or Eclipse
- MySQL Server
- MySQL Workbench
- Maven, optional if your IDE supports Maven

## 2. Create The Database

Open MySQL Workbench and run:

1. `database/schema.sql`
2. `database/sample_data.sql`

This creates the database, tables, indexes, sample users, reports, claims, and notifications.

## 3. Configure JDBC

Open:

```text
src/main/resources/application.properties
```

Change these values if your MySQL username/password is different:

```properties
db.user=root
db.password=root
```

## 4. Understand The Flow

When the app starts:

1. `App.java` installs the Swing theme.
2. It checks whether MySQL is reachable.
3. `LoginFrame` opens.
4. On login, `AuthController` calls `AuthService`.
5. `AuthService` checks the user through `UserDAO`.
6. After login, `MainFrame` opens the dashboard.

## 5. Add A New Lost Report

Student opens `Report Item`:

1. Select `LOST`.
2. Fill item name, category, location, date, description, and contact.
3. Optional: select image path.
4. Submit.
5. Report is saved as `PENDING`.
6. Admin can approve it.

## 6. Add A New Found Report

Student/admin opens `Report Item`:

1. Select `FOUND`.
2. Fill the same details.
3. Enter storage location, like `Security Desk, Admin Block`.
4. Submit.
5. Admin approves it.
6. Students can request a claim.

## 7. How Search Works

`BrowsePanel` collects filters and sends them to:

```text
AppController -> ItemService -> ItemReportDAO
```

The DAO builds a safe `PreparedStatement` query based on selected filters.

## 8. How Matching Works

`MatchService` compares a lost report against found reports, or a found report against lost reports.

It checks:

- Same category
- Same or nearby campus location
- Common keywords in item name and description

If the score is at least `35%`, the system shows it in possible matches.

## 9. How Claims Work

Student selects a found item and clicks `Request Claim`.

The request goes to:

```text
claim_requests table
```

Admin opens `Admin -> Claims` and approves or rejects. If approved, the item report becomes `CLAIMED`.

## 10. What To Explain To Your Teacher

Say this:

> I separated UI, business logic, and database logic. Swing screens only collect input and display results. Controllers connect UI to services. Services contain validation and matching rules. DAOs contain JDBC queries. This makes the project modular and easy to extend.

## 11. Easy Extensions

You can extend this project later with:

- Email/SMS notifications
- Image storage in a server folder
- QR code claim slips
- Fine collection reminders
- Department-wise admin accounts
- More advanced ML-based matching
