# Library Management System — Project Specification & Roadmap

A small, complete CLI practice project for Java + Maven + PostgreSQL + Hibernate, built to consolidate Hibernate fundamentals before moving on to Spring.

**Target duration:** 2–4 days
**Philosophy:** small → understandable → practical → complete. No enterprise patterns.

---

## 1. Project Overview

**What it does:** A terminal application that manages a small library's books and members — adding/removing/searching books, registering members, and tracking who has borrowed what.

**Problem it solves:** Gives you a realistic reason to practice entity mapping, relationships, DAOs, HQL, and transactions, without any of it being contrived.

**com.karam.library.Main features:**
- Manage books (add, remove, list, search)
- Manage members (register, list)
- Borrow a book
- Return a book
- View currently borrowed books
- A few HQL-based reports (overdue books, books by member, etc.)

**Intentionally excluded:**
- No web layer, no REST, no Spring anything
- No authentication/authorization
- No GUI
- No multi-branch/multi-library support
- No fines, reservations, or renewals (these appear only as optional challenges)
- No DTOs/mappers — the CLI can work with entities directly given the small scope

---

## 2. Entities

### Book
- `id` (Long, PK)
- `title` (String)
- `author` (String)
- `isbn` (String)
- `publishedYear` (Integer)
- `available` (boolean)

### Member
- `id` (Long, PK)
- `name` (String)
- `email` (String)

### Borrowing
- `id` (Long, PK)
- `book` (→ Book)
- `member` (→ Member)
- `borrowDate` (LocalDate)
- `returnDate` (LocalDate, nullable — null means "still borrowed")

### Relationships

```text
Member 1 ──── * Borrowing * ──── 1 Book
```

- One `Member` can have many `Borrowing` records.
- One `Book` can have many `Borrowing` records (over its lifetime, not concurrently).
- `Borrowing` is the join entity that carries its own data (`borrowDate`, `returnDate`).

**Why a `Borrowing` entity instead of a `member_id` on `Book`?**

A raw `member_id` column on `Book` can only represent "who has it *right now*," and only one relationship at a time. It can't answer basic questions your app needs to answer:
- Who has borrowed this book *historically*?
- When was it borrowed and returned?
- How many books does a member currently have out?
- Is this book overdue, and since when?

`Borrowing` turns a single fact ("this book is out") into a proper historical record with its own attributes. This is the classic case for a **many-to-many relationship that needs extra data on the join** — which Hibernate models as its own entity rather than a plain join table.

---

## 3. Database Design

```sql
CREATE TABLE book (
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    author          VARCHAR(255) NOT NULL,
    isbn            VARCHAR(20)  NOT NULL,
    published_year  INTEGER,
    available       BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE member (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    email   VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE borrowing (
    id           BIGSERIAL PRIMARY KEY,
    book_id      BIGINT NOT NULL REFERENCES book(id),
    member_id    BIGINT NOT NULL REFERENCES member(id),
    borrow_date  DATE NOT NULL,
    return_date  DATE
);
```

Notes:
- `book_id` and `member_id` are foreign keys in `borrowing` — this is the standard "many" side of both relationships living on the join table.
- `email` is unique to keep member lookups sane (also gives you practice with a DB-level constraint).
- `return_date IS NULL` is your signal for "currently borrowed." You'll rely on this in several HQL queries.
- Optional: a partial unique index to prevent a book being borrowed twice concurrently (`UNIQUE (book_id) WHERE return_date IS NULL`) — nice-to-have, not required for v1.

This is genuinely the whole schema — three tables, two foreign keys.

---

## 4. Hibernate Mapping Guidance

Practice exactly these annotations, nothing more:

- `@Entity`, `@Table` (optional, only if you want explicit table names)
- `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` (matches `BIGSERIAL`)
- `@Column` (mainly for `nullable`, `unique`, `length`)
- `@ManyToOne`, `@OneToMany`, `@JoinColumn`

**Directionality:**
- `Borrowing → Book` and `Borrowing → Member`: these should be **`@ManyToOne`**, unidirectional is enough. `Borrowing` needs to know its book and member; `Book` and `Member` do *not* need a `List<Borrowing>` back-reference for this project's use cases.
- If you want the practice, you *can* add a `@OneToMany(mappedBy = "book")` on `Book` and/or `Member` — this is a good candidate for one of your two entities so you get bidirectional practice, but you don't need it on both. A reasonable choice: make `Member → Borrowing` bidirectional (useful for "list all borrowings for this member" via the object graph), and keep `Book → Borrowing` unidirectional (you'll just query it via HQL instead).
- Where you use `mappedBy`, it goes on the **non-owning** side (the side without the foreign key column) — i.e., on `Book`/`Member`, pointing at the field name in `Borrowing` (`mappedBy = "book"` / `mappedBy = "member"`).

**Fetch type:**
- `@ManyToOne` defaults to `EAGER` in JPA — override it to `FetchType.LAZY` explicitly. You almost never want to eagerly pull in the full `Book`/`Member` just because you loaded a `Borrowing`.
- Any `@OneToMany` you add should also be `LAZY` (it actually already defaults to lazy, but stating it explicitly is good practice).

**Cascade:**
- Don't cascade `Borrowing` deletes onto `Book`/`Member` (deleting a book shouldn't delete its borrowing history, and vice versa).
- If you added `Member → List<Borrowing>`, you *could* consider `CascadeType.PERSIST` if you ever create borrowings through the member object — but in this design, you'll create `Borrowing` directly via its own DAO, so no cascade is actually needed. Keep it off unless you have a concrete reason.

---

## 5. Project Structure

```text
src/
└── main/
    └── java/
        └── com.example.library/
            ├── model/        # @Entity classes: Book, Member, Borrowing
            ├── dao/          # Persistence access — one DAO per entity
            ├── service/       # Business logic — borrow/return workflows, transactions
            ├── util/          # HibernateUtil (SessionFactory setup)
            ├── exception/     # Custom checked/unchecked exceptions
            └── com.karam.library.Main.java      # CLI entry point / menu loop
```

- `model` — pure data + mapping annotations, no logic.
- `dao` — CRUD + queries against the database, one `Session`/transaction per call (or passed in — your call).
- `service` — orchestrates DAOs, enforces business rules, owns transaction boundaries.
- `util` — a single `HibernateUtil` class exposing a `SessionFactory`.
- `exception` — your 3–4 custom exceptions.
- `com.karam.library.Main` — Scanner loop, calls into `service`, prints results.

No `controller`, no `dto`, no `mapper`, no `config` package beyond `util`. Anything more is over-engineering for this scope.

---

## 6. DAO Layer

### BookDao
```text
save(Book book)
findById(Long id)
findAll()
delete(Long id)
searchByTitle(String title)          // HQL, partial match
findAvailableBooks()                 // HQL
findByIsbn(String isbn)              // useful for duplicate-ISBN check
```

### MemberDao
```text
save(Member member)
findById(Long id)
findAll()
findByEmail(String email)            // useful for duplicate-email check
```

### BorrowingDao
```text
save(Borrowing borrowing)
findById(Long id)
findActiveByBookId(Long bookId)      // HQL — the current open borrowing for a book, if any
findAllByMember(Long memberId)       // HQL — a member's borrowing history
findAllActive()                      // HQL — every book currently out
findOverdue(LocalDate cutoffDate)    // HQL — active borrowings older than cutoffDate
```

**HQL requirements (write these yourself):**
- Find books whose title contains a given string (case-insensitive)
- Find all books where `available = true`
- Find all borrowings for a given member, ordered by `borrowDate`
- Find the currently-open borrowing (if any) for a given book (`return_date IS NULL`)
- Find all borrowings where `return_date IS NULL AND borrow_date < :cutoff` (overdue)

Don't hardcode SQL — use HQL against your entity/field names, and parameter binding (`setParameter`), not string concatenation.

---

## 7. Service Layer

### `BorrowingService.borrowBook(Long bookId, Long memberId)`

1. Find the book by id — throw `BookNotFoundException` if missing.
2. Check `book.isAvailable()` — throw `BookUnavailableException` if false.
3. Find the member by id — throw `MemberNotFoundException` if missing.
4. Create a new `Borrowing` (book, member, `borrowDate = today`, `returnDate = null`).
5. Mark the book as unavailable (`available = false`).
6. Save both changes and commit.

### `BorrowingService.returnBook(Long bookId)`

1. Find the active borrowing for that book — throw `BorrowingNotFoundException` if none exists.
2. Set `returnDate = today` on it.
3. Mark the book as available (`available = true`).
4. Save both changes and commit.

**Why these need to be transactions:** each operation touches *two* rows across *two* tables (a `Book` row and a `Borrowing` row) that must change together. If the app crashes, or an exception is thrown, between updating the book and saving the borrowing, you'd end up with an inconsistent state — e.g., a book marked unavailable with no borrowing record explaining why, or a borrowing record with no corresponding availability flip. Wrapping both writes in one transaction means either both happen or neither does.

---

## 8. Transactions

- **Open a transaction** at the start of each service method that performs a write (or a multi-step read-then-write sequence), not inside the DAO methods themselves — the service layer owns the transaction boundary since it knows the full unit of work.
- **What belongs in one transaction:** every step of `borrowBook` (steps 1–5 above) should be one transaction; same for `returnBook`. Pure reads (`listBooks`, `searchByTitle`) don't need explicit transactions in Hibernate (a session with no explicit transaction can still do a simple read, though wrapping reads is also fine and consistent).
- **Commit** once every step of the use case has succeeded.
- **Rollback** on any exception — catch broadly around the transaction body, call `transaction.rollback()`, then rethrow (or translate into your custom exception) so the caller/CLI knows it failed.
- **Partial failure:** if, say, the book update succeeds in memory but the borrowing insert throws (e.g., a DB constraint violation), rolling back must undo *both* — this is the entire point of doing it in one transaction rather than two separate commits.

**Failure scenarios to actually test:**
1. Try to borrow a book that's already borrowed (checked in your code — should throw `BookUnavailableException` before any write happens).
2. Simulate a lower-level failure: e.g., borrow with a `memberId` that doesn't exist in the DB — your DAO's member lookup should catch this before attempting the insert, but you can also deliberately break something (e.g., temporarily remove the `memberId` null-check) to watch a `ConstraintViolationException` happen mid-transaction and confirm the book's `available` flag does *not* get incorrectly persisted as `false`. Restore the check afterward — this is just to observe rollback behavior.

---

## 9. Exceptions

- **`BookNotFoundException`** — thrown when a lookup by book id/isbn finds nothing but the operation requires the book to exist.
- **`MemberNotFoundException`** — same, for member lookups.
- **`BookUnavailableException`** — thrown in `borrowBook` when the book exists but `available == false`.
- **`BorrowingNotFoundException`** — thrown in `returnBook` when there's no active (open) borrowing for the given book.

Four is enough. Make them unchecked (`extends RuntimeException`) so you're not forced to declare `throws` everywhere in the DAO/service chain — catch them at the CLI boundary and print a friendly message.

---

## 10. CLI

```text
========================
 Library Management
========================

1. List books
2. Add book
3. Remove book
4. Search books
5. Register member
6. List members
7. Borrow book
8. Return book
9. View borrowed books
10. Exit

Choose an option:
```

| Option | Input | Behavior | Success output | Invalid input |
|---|---|---|---|---|
| List books | none | Calls `BookService.findAll()` | Prints table of all books | n/a |
| Add book | title, author, isbn, year | Validates fields, saves | "Book added with id X" | Re-prompt or reject with message if a field is blank |
| Remove book | book id | Deletes if exists | "Book removed" | "Book not found" if bad id |
| Search books | search term | HQL title search | List of matches or "No books found" | n/a |
| Register member | name, email | Validates, checks duplicate email, saves | "Member registered with id X" | Message on blank field or duplicate email |
| List members | none | Lists all | Table of members | n/a |
| Borrow book | book id, member id | Runs `borrowBook` service | "Book borrowed successfully" | Catch each custom exception, print its message |
| Return book | book id | Runs `returnBook` service | "Book returned successfully" | Catch `BorrowingNotFoundException`, print message |
| View borrowed books | none | HQL `findAllActive()` | Table: book title, member name, borrow date | n/a |
| Exit | none | Closes `SessionFactory`, exits | "Goodbye" | n/a |

General invalid-input handling: wrap numeric parsing (`Integer.parseInt` / `Long.parseLong`) in try/catch and re-prompt rather than crashing on a non-numeric menu choice or id.

---

## 11. Validation

Keep it as plain Java `if` checks in the service (or a small `Validator` utility method), thrown as `IllegalArgumentException` or handled inline in the CLI before calling the service:

- Book title, author, isbn: not null/blank
- Member name, email: not null/blank
- Basic email sanity check (contains `@`) is enough — don't reach for regex validation libraries
- A book cannot be borrowed if `available == false` (already enforced by `BookUnavailableException` in the service — don't duplicate this logic in the CLI)

No Bean Validation (`@NotNull`, `@Email` annotations), no Spring validation — just code.

---

## 12. Development Steps

**Step 1 — Create Maven project.** Verify: `mvn compile` succeeds with an empty `com.karam.library.Main.java`.

**Step 2 — Configure PostgreSQL.** Create the database and the three tables (or let Hibernate generate them via `hbm2ddl.auto=update` initially, then switch to `validate` once stable). Verify: you can connect via `psql` or a client and see an empty schema/tables.

**Step 3 — Configure Hibernate.** Add `hibernate.cfg.xml` or a properties-based config with your DB URL, dialect, credentials, and `hbm2ddl.auto`. Verify: `HibernateUtil` builds a `SessionFactory` without throwing.

**Step 4 — Create entities.** `Book`, `Member`, `Borrowing` with mappings from Section 4. Verify: Hibernate startup logs show the correct DDL (or `validate` passes against your manually created tables).

**Step 5 — Test that Hibernate can persist a Book.** Write a tiny throwaway `main()` that saves one `Book` and reads it back. Verify: row appears in `book` table via `psql`.

**Step 6 — Implement Book DAO.** All methods from Section 6. Verify: each method manually exercised via a scratch test/main method.

**Step 7 — Implement Member DAO.** Same approach.

**Step 8 — Implement Borrowing DAO** (entity + DAO together, since Borrowing depends on Book/Member existing). Verify: can manually insert a borrowing referencing a real book and member.

**Step 9 — Implement service layer** (`BookService`, `MemberService`, `BorrowingService`) — business rules, no transaction handling yet if you want to separate concerns while learning, or combine with Step 10.

**Step 10 — Implement transactions** inside `BorrowingService.borrowBook`/`returnBook`. Verify: the two failure scenarios from Section 8 behave correctly — nothing half-committed.

**Step 11 — Build CLI.** Wire `com.karam.library.Main.java`'s menu loop to the services. Verify: full manual walkthrough of the Section 13 test list.

**Step 12 — Add HQL search/report queries** (`searchByTitle`, `findAvailableBooks`, `findOverdue`, etc.) if not already done in Step 6/8. Verify: each query against seeded data returns exactly what you expect.

**Step 13 — Test edge cases.** Run through Section 13's failure cases deliberately.

---

## 13. Testing (Manual CLI Scenarios)

**Happy path:**
```text
1. Add a book
2. List books
3. Add a member
4. Borrow the book
5. View borrowed books
6. Return the book
7. Borrow it again (should succeed — it's available again)
8. Search for the book by partial title
9. Delete a book
10. List books (confirm it's gone)
```

**Failure cases:**
```text
1. Try to borrow a book that's already borrowed → expect BookUnavailableException message
2. Try to borrow with a nonexistent book id → expect BookNotFoundException message
3. Try to borrow with a nonexistent member id → expect MemberNotFoundException message
4. Try to return a book that was never borrowed → expect BorrowingNotFoundException message
5. Register a member with a blank name/email → expect validation rejection
6. Register a member with a duplicate email → expect rejection
7. Add a book with a blank title → expect validation rejection
8. Enter non-numeric input at a menu prompt → expect graceful re-prompt, not a crash
9. Search for a title that doesn't exist → expect "No books found", not an error
```

JUnit is a reasonable optional extension later (e.g., testing service-layer logic against an in-memory/test DB), but it's not part of the core scope here — manual CLI testing is sufficient for this project's purpose.

---

## 14. Optional Challenges

Pick zero or more, only after the base app fully works:

- Search books by author (in addition to title)
- Prevent duplicate ISBNs on add
- Add a maximum borrowing limit per member (e.g., max 3 active borrowings)
- Add a simple `category` field to `Book` and allow filtering by it
- Add an overdue report using a configurable "loan period" (e.g., 14 days) and the `findOverdue` HQL query
- Add a "most borrowed books" report (HQL with `GROUP BY` and `COUNT`)
- Add simple pagination to `List books` if your seed data grows large

---

## 15. Completion Checklist

```text
[*] Maven project created
[*] PostgreSQL configured
[*] Hibernate configured
[*] Book entity created
[*] Member entity created
[*] Borrowing entity created
[*] Relationships mapped
[*] Book DAO implemented
[*] Member DAO implemented
[*] Borrowing DAO implemented
[ ] Service layer implemented
[ ] Transactions implemented
[ ] Custom exceptions implemented
[ ] CLI implemented
[ ] Search implemented
[ ] Borrowing works
[ ] Returning works
[ ] Error cases handled
[ ] Project tested manually
```