# Schedora Backend

Schedora is a salon marketplace backend foundation, similar in spirit to an OYO-style partner marketplace for salons. This version implements the salon-owner and super-admin sides only; customer marketplace, booking, payments, reviews, and search APIs are intentionally left for later.

## Current Scope

- Salon-owner registration and login
- Super-admin login through seeded admin account
- JWT authentication with BCrypt password hashing
- Role-based authorization for `SUPER_ADMIN` and `SALON_OWNER`
- Service-layer owner checks to prevent cross-salon data access
- Salon onboarding with explicit approval lifecycle
- Salon profile, branch, service, staff, working-hour, and staff-availability management
- Development seed data for API testing

## Technology

- Java 21 source level
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA / Hibernate
- Spring Security
- JWT via JJWT
- Jakarta Validation
- H2 in-memory database
- Maven

## Architecture

The project is a modular monolith. Controllers are thin, DTOs are used at API boundaries, business rules live in services, and repositories handle persistence.

```text
com.schedora
├── auth
├── availability
├── branch
├── common
├── config
├── offering
├── salon
├── staff
└── user
```

Core relationship model:

```text
AppUser(SALON_OWNER) 1 -> 1 Salon
Salon 1 -> many Branch
Branch 1 -> many ServiceOffering
Branch 1 -> many Staff
Staff many -> many ServiceOffering
Branch 1 -> many WorkingHour
Staff 1 -> many StaffAvailability
```

## Salon Lifecycle

New salon-owner registrations create a salon application in `PENDING`.

```text
PENDING -> ACTIVE      by super-admin approve
PENDING -> REJECTED    by super-admin reject
ACTIVE  -> SUSPENDED   by super-admin suspend
SUSPENDED -> ACTIVE    by super-admin activate
```

Salon owners cannot change approval status. Rejected and suspended salons cannot mutate operational data.

## Running Locally

From this folder:

```bash
mvn spring-boot:run
```

If Maven is not on `PATH` on this machine, use the cached Maven binary:

```powershell
cmd /c "C:\Users\ASUS-PC\.m2\wrapper\dists\apache-maven-3.9.16-bin\5grr65jo27hi51sujmtcldfovl\apache-maven-3.9.16\bin\mvn.cmd -Dmaven.repo.local=C:\Users\ASUS-PC\.m2\repository spring-boot:run"
```

The API starts on `http://localhost:8080`.

H2 console:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:schedora`
- User: `sa`
- Password: empty

## Configuration

Important properties in `src/main/resources/application.properties`:

- `schedora.jwt.secret`: use `SCHEDORA_JWT_SECRET` outside development
- `schedora.jwt.expiration-ms`: token lifetime
- `schedora.cors.allowed-origins`: comma-separated frontend origins
- `schedora.seed.enabled`: enable or disable development seed data

## Seed Accounts

Seed data is enabled by default.

| Role | Email | Password |
| --- | --- | --- |
| Super Admin | `admin@schedora.local` | `Admin@12345` |
| Salon Owner | `pending.owner@schedora.local` | `Owner@12345` |
| Salon Owner | `active.owner@schedora.local` | `Owner@12345` |
| Salon Owner | `rejected.owner@schedora.local` | `Owner@12345` |
| Salon Owner | `suspended.owner@schedora.local` | `Owner@12345` |

## Main API Endpoints

Authentication:

- `POST /api/auth/salon-owner/register`
- `POST /api/auth/login`

Salon owner:

- `GET /api/salon-owner/profile`
- `GET /api/salon-owner/salon`
- `PUT /api/salon-owner/salon`
- `GET /api/salon-owner/branches`
- `POST /api/salon-owner/branches`
- `GET /api/salon-owner/branches/{branchId}`
- `PUT /api/salon-owner/branches/{branchId}`
- `DELETE /api/salon-owner/branches/{branchId}`
- `GET /api/salon-owner/services`
- `POST /api/salon-owner/services`
- `GET /api/salon-owner/services/{serviceId}`
- `PUT /api/salon-owner/services/{serviceId}`
- `DELETE /api/salon-owner/services/{serviceId}`
- `GET /api/salon-owner/staff`
- `POST /api/salon-owner/staff`
- `GET /api/salon-owner/staff/{staffId}`
- `PUT /api/salon-owner/staff/{staffId}`
- `DELETE /api/salon-owner/staff/{staffId}`
- `GET /api/salon-owner/working-hours?branchId={branchId}`
- `PUT /api/salon-owner/working-hours`
- `GET /api/salon-owner/staff/{staffId}/availability`
- `PUT /api/salon-owner/staff/{staffId}/availability`

Super admin:

- `GET /api/admin/salons`
- `GET /api/admin/salons/pending`
- `GET /api/admin/salons/{salonId}`
- `PATCH /api/admin/salons/{salonId}/approve`
- `PATCH /api/admin/salons/{salonId}/reject`
- `PATCH /api/admin/salons/{salonId}/suspend`
- `PATCH /api/admin/salons/{salonId}/activate`
- `GET /api/admin/salon-owners`
- `PATCH /api/admin/salon-owners/{userId}/activate`
- `PATCH /api/admin/salon-owners/{userId}/deactivate`
- `PATCH /api/admin/salon-owners/{userId}/block`

## Example Requests

Login:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"admin@schedora.local\",\"password\":\"Admin@12345\"}"
```

Create a branch as a salon owner:

```bash
curl -X POST http://localhost:8080/api/salon-owner/branches \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Main Branch\",\"phone\":\"+910000000111\",\"addressLine\":\"MG Road\",\"city\":\"Bengaluru\",\"state\":\"Karnataka\",\"pincode\":\"560001\"}"
```

Approve a salon as super admin:

```bash
curl -X PATCH http://localhost:8080/api/admin/salons/1/approve \
  -H "Authorization: Bearer <admin-token>"
```

## Testing

```bash
mvn clean test
```

The test suite covers registration, login failures, password hashing, protected endpoints, admin lifecycle transitions, owner isolation, validation, working hours, and staff availability.

## Future Roadmap

- Customer registration and login
- Public salon search and discovery
- Slot generation
- Bookings
- Payments
- Reviews and ratings
- PostgreSQL migration profile
