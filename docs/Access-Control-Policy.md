# Access Control Policy & Justification
## SecureLibrarySystem

**Document Version:** 1.0  
**Date:** March 3, 2026  
**System:** SecureLibrarySystem (Spring Boot 4.0.2 / Java 21)  
**Classification:** Internal Security Document

---

## 1. Purpose

This document defines the access control policy for the SecureLibrarySystem web application. It specifies which roles exist in the system, what resources and actions each role is permitted to perform, and provides a security justification for every access decision. This follows the **Principle of Least Privilege** — users are granted only the minimum permissions necessary to perform their job function.

---

## 2. Authentication Requirements

All access to protected resources requires:

| Requirement | Mechanism |
|---|---|
| Valid username + password | SHA-256 password hash with random 16-byte salt |
| Email-based OTP verification | 6-digit one-time password sent via Gmail SMTP |
| Active session | Spring HTTP Session with `AUTHENTICATED = true` |
| No cached page bypass | `Cache-Control: no-cache, no-store, must-revalidate` headers on all responses |

Unauthenticated requests to any protected URL are redirected to `/login-page`.  
Authenticated users visiting `/login-page`, `/register-page`, or `/otp-page` are redirected to `/dashboard`.

---

## 3. Roles Defined

The system defines three roles in `Role.java`:

| Role | Description | Who Holds This Role |
|---|---|---|
| **ADMIN** | System administrator with full control | Library system administrators responsible for governance and oversight |
| **LIBRARIAN** | Library staff with operational control | Library employees who manage day-to-day book operations |
| **STUDENT** | End user / borrower | Registered students who borrow books |

---

## 4. Access Rights Matrix

The table below maps every protected endpoint to the roles that are permitted to access it, along with the justification.

### 4.1 Authentication Endpoints (Public — No Login Required)

| Endpoint | Method | Who Can Access | Justification |
|---|---|---|---|
| `/` | GET | Everyone | Root redirect — sends unauthenticated users to login, authenticated to dashboard |
| `/login-page` | GET | Unauthenticated users only | Entry point for the system; authenticated users have no reason to revisit it |
| `/register-page` | GET | Unauthenticated users only | Self-registration for new students; authenticated users are already registered |
| `/otp-page` | GET | Unauthenticated users only | OTP entry page during login flow; not needed once session is established |
| `/auth/register` | POST | Everyone (unauthenticated) | Allows new students to create an account |
| `/auth/login` | POST | Everyone (unauthenticated) | Entry point for credential validation |
| `/auth/verify-otp` | POST | Users with OTP_REQUIRED session flag | Second factor — only accessible mid-login flow |
| `/auth/logout` | GET | Authenticated users | Invalidates session and redirects to login |

---

### 4.2 Dashboard

| Endpoint | Method | ADMIN | LIBRARIAN | STUDENT | Justification |
|---|---|:---:|:---:|:---:|---|
| `/dashboard` | GET | ✅ | ✅ | ✅ | Central landing page after login; every authenticated role needs a home screen |

---

### 4.3 Book Catalogue (`/books`)

| Endpoint | Method | ADMIN | LIBRARIAN | STUDENT | Justification |
|---|---|:---:|:---:|:---:|---|
| `/books` | GET | ✅ | ✅ | ✅ | All authenticated users must be able to browse the catalogue — core purpose of the library system |
| `/books/add` | GET | ✅ | ❌ | ❌ | Only ADMIN can add books directly; LIBRARIAN must go through a request workflow (separation of duties) |
| `/books/add` | POST | ✅ | ❌ | ❌ | Same as above — direct book creation is an admin-only privilege |
| `/books/delete/{id}` | POST | ✅ | ❌ | ❌ | Permanent deletion is destructive and irreversible; restricted to ADMIN to prevent accidental or malicious removals |

**Justification summary:** Students are consumers of the catalogue, not managers of it. Librarians propose changes through request workflows that require admin approval, enforcing a four-eyes principle for catalogue changes.

---

### 4.4 Book Borrow Requests (`/requests`)

| Endpoint | Method | ADMIN | LIBRARIAN | STUDENT | Justification |
|---|---|:---:|:---:|:---:|---|
| `/requests` | GET | ✅ (all requests) | ✅ (all requests) | ✅ (own requests only) | ADMIN and LIBRARIAN need full visibility to manage lending; STUDENTs can only see their own requests (data minimisation / privacy) |
| `/requests/create` | POST | ❌ | ❌ | ✅ | Only students borrow books — staff do not need to borrow |
| `/requests/accept/{id}` | POST | ✅ | ✅ | ❌ | Approval is a staff function; students cannot approve their own requests (conflict of interest prevention) |
| `/requests/reject/{id}` | POST | ✅ | ✅ | ❌ | Same as above — rejection authority belongs to staff |

**Justification summary:** A student requesting a book and also approving it would be a conflict of interest and a security risk. The approval step by a staff member ensures accountability.

---

### 4.5 Book Addition Requests (`/book-add-requests`)

| Endpoint | Method | ADMIN | LIBRARIAN | STUDENT | Justification |
|---|---|:---:|:---:|:---:|---|
| `/book-add-requests` | GET | ✅ (all requests) | ✅ (own requests only) | ❌ | ADMIN needs full oversight; LIBRARIAN views only their own submissions; STUDENT has no catalogue management role |
| `/book-add-requests/create` | POST | ❌ | ✅ | ❌ | Librarians are the operational staff who identify books to add; ADMINs govern but delegate identification to Librarians |
| `/book-add-requests/approve/{id}` | POST | ✅ | ❌ | ❌ | Final addition to the system catalogue must be ADMIN-authorised — prevents unauthorised catalogue inflation |

**Justification summary:** This workflow enforces a segregation of duties: Librarians identify and request, Admins approve and commit. Neither can complete the full process alone.

---

### 4.6 Restock Requests (`/restock-requests`)

| Endpoint | Method | ADMIN | LIBRARIAN | STUDENT | Justification |
|---|---|:---:|:---:|:---:|---|
| `/restock-requests` | GET | ✅ (all requests) | ✅ (own requests + unavailable books) | ❌ | ADMIN oversees all; LIBRARIAN needs to see unavailable books to create restock requests; STUDENTs have no inventory role |
| `/restock-requests/create` | POST | ❌ | ✅ | ❌ | Librarians are on the floor and identify stock shortages; they initiate the restock process |
| `/restock-requests/approve/{id}` | POST | ✅ | ❌ | ❌ | Procurement approval is an admin-level financial/governance decision |
| `/restock-requests/reject/{id}` | POST | ✅ | ❌ | ❌ | Rejection carries the same authority level as approval |

**Justification summary:** Restocking involves resource allocation (budget / procurement). Restricting final approval to ADMIN ensures financial accountability.

---

### 4.7 Transactions (`/transactions`)

| Endpoint | Method | ADMIN | LIBRARIAN | STUDENT | Justification |
|---|---|:---:|:---:|:---:|---|
| `/transactions` | GET | ✅ (all transactions) | ✅ (all transactions) | ✅ (own transactions only) | ADMIN and LIBRARIAN require full audit trail for compliance; STUDENTs can only see their own borrowing history (privacy protection, GDPR principle of data minimisation) |

**Additional security:** Every transaction record carries a **digital signature**. If a record is tampered with in the database, the system marks it as `❌ TAMPERED` when the signature verification fails.

---

### 4.8 User Management (`/users`)

| Endpoint | Method | ADMIN | LIBRARIAN | STUDENT | Justification |
|---|---|:---:|:---:|:---:|---|
| `/users` | GET | ✅ | ✅ (view only) | ❌ | ADMIN and LIBRARIAN may need to look up user accounts for operational purposes; STUDENT has no administrative function |
| `/users/edit/{id}` | GET | ✅ | ❌ | ❌ | Editing user accounts (including role changes) is an ADMIN-only privilege — prevents privilege escalation |
| `/users/edit/{id}` | POST | ✅ | ❌ | ❌ | Saving role/username/email changes is restricted to ADMIN; a LIBRARIAN editing their own role to ADMIN would be a critical privilege escalation vulnerability |

**Justification summary:** User management, especially role assignment, is the highest-privilege operation in the system. Limiting it to ADMIN prevents privilege escalation attacks.

---

### 4.9 Notifications (`/notifications`)

| Endpoint | Method | ADMIN | LIBRARIAN | STUDENT | Justification |
|---|---|:---:|:---:|:---:|---|
| `/notifications` | GET | ✅ (role-based notifications) | ✅ (role-based notifications) | ✅ (own notifications only) | All roles need to receive relevant system alerts; data is filtered server-side so users only see notifications appropriate to their role |

---

## 5. Data-Level Access Control

Beyond endpoint protection, the system enforces row-level data restrictions:

| Data Type | ADMIN Sees | LIBRARIAN Sees | STUDENT Sees |
|---|---|---|---|
| Book Requests | All users' requests | All users' requests | Their own requests only |
| Book Addition Requests | All submissions | Their own submissions | Not accessible |
| Restock Requests | All requests | Their own requests | Not accessible |
| Transactions | All records | All records | Their own records only |
| Notifications | All system alerts | Relevant staff alerts | Personal overdue/status alerts |
| User Records | All users | All users (view only) | Not accessible |

---

## 6. Sensitive Data Protection

| Data | Protection Mechanism | Justification |
|---|---|---|
| Username | AES-256/CBC/PKCS5Padding encryption at rest | Prevents database breach from exposing user identities |
| Email | AES-256/CBC/PKCS5Padding encryption at rest | Protects PII in compliance with data protection principles |
| Password | SHA-256 hash + random 16-byte salt (never stored in plaintext) | Ensures passwords cannot be recovered even if database is compromised |
| OTP | Single-use, sent only to registered email | Ensures second-factor cannot be replayed or reused |
| Session data | Server-side `HttpSession` (never exposed to client) | Prevents session tampering from browser-side manipulation |
| Transaction integrity | RSA Digital Signature on every transaction record | Detects database tampering — any modification invalidates the signature |
| Private keys | Stored in `/keys/` directory, outside web root | Keys cannot be accessed via HTTP requests |

---

## 7. Security Controls Summary

| Control | Implementation |
|---|---|
| Authentication | Username + Password + Email OTP (Multi-Factor) |
| Session Management | Spring `HttpSession`, invalidated on logout |
| Back-Button / Cache Attack Prevention | `Cache-Control: no-cache, no-store, must-revalidate` on all responses |
| Privilege Escalation Prevention | Role checked on every protected endpoint server-side |
| Conflict of Interest Prevention | Users cannot approve their own requests |
| Separation of Duties | Librarians propose, Admins approve for catalogue and restock changes |
| Data Minimisation | Students see only their own data; staff see all data only as needed |
| Audit Trail | All borrow/return transactions logged with digital signatures |
| Tampering Detection | Digital signature verification flags modified transaction records |
| CSRF Protection | Disabled (stateless token flow) — compensated by session-based auth |
| SQL Injection Prevention | All DB access via JPA / Hibernate parameterised queries |

---

## 8. Role Assignment Policy

| Rule | Detail |
|---|---|
| Who can register? | Anyone — self-registration creates a STUDENT account by default |
| Who assigns roles? | Only ADMIN can change a user's role via `/users/edit/{id}` |
| Can a user change their own role? | No — role change requires ADMIN credentials in a separate session |
| Default role on registration | STUDENT |

---

## 9. Justification Summary Table

| Access Decision | Principle Applied |
|---|---|
| STUDENTs cannot add or delete books | **Least Privilege** — students are consumers, not administrators |
| STUDENTs cannot approve their own borrow requests | **Conflict of Interest Prevention / Segregation of Duties** |
| Only ADMIN can edit user roles | **Privilege Escalation Prevention** |
| LIBRARIAN sees only their own book-addition submissions | **Data Minimisation / Need-to-Know** |
| STUDENTs see only their own transactions and requests | **Privacy / Data Minimisation** |
| Only ADMIN can delete books | **Irreversibility Control** — destructive actions require highest authority |
| Book and restock additions require ADMIN approval | **Four-Eyes Principle / Segregation of Duties** |
| OTP required for every login | **Multi-Factor Authentication** — compensates for password compromise |
| No-cache headers on all responses | **Session Termination Integrity** — prevents back-button session bypass |
| Passwords hashed with salt, never encrypted | **Cryptographic Best Practice** — encryption is reversible, hashing is not |

---

*End of Document*
