
# 🩺 Telemedicine Doctor Slot Scheduling Engine

> Enterprise-grade Spring Boot 3 REST API for doctor slot scheduling with concurrency-safe booking using Optimistic Locking.

## Table of Contents
- Executive Summary
- Problem Statement
- Features
- Tech Stack
- Architecture
- ER Diagram
- API Reference
- Slot Generation
- Booking Workflow
- Cancellation Workflow
- Rescheduling Workflow
- Concurrency Handling
- UTC Time Handling
- Design Decisions
- Evaluation Criteria Mapping
- Setup
- File/Folder structure
- Screenshots
- Submission Links

# Executive Summary
Doctors publish broad availability windows while patients reserve precise appointments.
This project materializes bookable slots from availability windows and prevents double-booking under concurrent access.

# Problem Statement
Implements the assessment requirements:
- Generate slots from availability.
- Configurable appointment duration.
- Optional buffer time.
- Show only available slots.
- Cancellation releases slots.
- Rescheduling preserves booking consistency.
- Store timestamps internally in UTC.
- Prevent concurrent double booking.

# Features
- Doctor CRUD
- Patient CRUD
- Availability Management
- Automatic Slot Generation
- GCD based Base Slot Duration
- Multi-slot Booking
- Cancellation
- Rescheduling
- Optimistic Locking
- Swagger/OpenAPI

# Tech Stack
| Layer | Technology |
|-------|------------|
| Language | Java 21 |
| Framework | Spring Boot 3 |
| ORM | Spring Data JPA / Hibernate |
| Database | MySQL |
| Mapping | MapStruct |
| Docs | Swagger |
| Build | Maven |

# Architecture
```text
Client
   |
Controller
   |
Service
   |
Repository
   |
MySQL
```

# ER Diagram
```text
Doctor (1) ------ (*) Availability (1) ------ (*) Slot
   |                                            |
   +----------------(*) Booking (*)-------------+
                        |
                     Patient
```

# API Reference

## Doctors
POST /doctors — Create doctor

GET /doctors — List doctors

GET /doctors/{id} — Doctor details

PUT /doctors/{id} — Update doctor

DELETE /doctors/{id} — Delete doctor

## Patients
POST /patients

GET /patients

GET /patients/{id}

PUT /patients/{id}

DELETE /patients/{id}

## Availability
POST /availability — Publish availability & generate slots

GET /availability

GET /availability/{id}

## Slots
GET /slots/doctor/{doctorId} — List AVAILABLE slots

## Bookings
POST /bookings — Book slot(s)

GET /bookings/{id}

GET /bookings/doctor/{doctorId}

GET /bookings/patient/{patientId}

PUT /bookings/{id}/cancel

PUT /bookings/{id}/reschedule

# Slot Generation

Instead of directly generating slots using consultation duration, the application computes a Base Slot Duration.

Base Slot Duration = GCD(firstVisitDuration, followUpDuration)

Example:

First Visit = 30 min

Follow-up = 20 min

Base Slot = 10 min

Availability:
09:00–10:00

Generated slots:
09:00-09:10
09:10-09:20
09:20-09:30
09:30-09:40
09:40-09:50
09:50-10:00

Appointments reserve contiguous base slots.
Buffer time is inserted between slots if configured.

# Booking Workflow

1. Validate patient.
2. Validate doctor.
3. Validate slot ownership.
4. Validate active availability.
5. Calculate required slots.
6. Find contiguous available slots.
7. Reserve all slots inside one transaction.
8. Save booking.
9. Return response.

# Cancellation Workflow

- Booking status → CANCELLED
- Reserved slots → AVAILABLE
- Slots become immediately bookable again.

# Rescheduling Workflow

1. Load booking.
2. Release existing slots.
3. Find new contiguous slots.
4. Reserve new slots.
5. Update booking atomically.

# Concurrency Handling

Each Slot contains an @Version column.

Concurrent updates trigger Hibernate Optimistic Locking.

If two patients attempt to reserve the same slot simultaneously:

- First transaction succeeds.
- Second transaction throws ObjectOptimisticLockingFailureException.
- API returns SlotUnavailableException.

This guarantees no double booking.

# UTC Time Handling

Doctors define schedules in their local timezone.

The application converts LocalDate + LocalTime into Instant before persistence.

Benefits:
- Consistent storage
- Timezone independent
- Distributed-system friendly

### Design Decisions & Trade-offs

 # 1. Materialized Slots instead of Computed Slots

Instead of calculating available slots every time a patient searches for appointments, the application materializes (pre-generates) slot records whenever a doctor publishes an availability window.

Why?
Faster slot lookup (WHERE status = AVAILABLE)
Simpler booking logic
Easy cancellation and rescheduling
Enables optimistic locking on individual slot records
Better scalability for read-heavy systems
Trade-off
Uses more database storage because every slot is persisted.
Storage overhead is acceptable since it greatly simplifies booking and concurrency management.


# 2. Optimistic Locking instead of Pessimistic Locking

The system uses JPA Optimistic Locking (@Version) on the Slot entity to prevent double booking.

Why?

Healthcare booking systems typically experience:

Many users viewing slots
Relatively few users booking simultaneously

Optimistic locking allows all users to read slots freely while detecting conflicts only during updates.

If two patients try to reserve the same slot simultaneously:

First transaction succeeds.
Second transaction fails with an OptimisticLockException.
The application returns an appropriate error indicating that the slot is no longer available.
Trade-off
Some transactions may need to be retried under heavy contention.
However, overall database throughput is much higher compared to locking rows for every read.


# Evaluation Criteria Mapping

| Requirement | Implementation |
|-------------|---------------|
| Slot Generation | ✔ |
| Variable Duration | ✔ |
| Buffer Time | ✔ |
| Booking | ✔ |
| Cancellation | ✔ |
| Rescheduling | ✔ |
| UTC Storage | ✔ |
| Concurrency Safety | ✔ |
| Clean Architecture | ✔ |

# Setup

```bash
git clone <repo>
cd Telemedicine
```
# configure database
`spring.datasource.url=jdbc:mysql://localhost:3306/telemedicine_db?createDatabaseIfNotExist=true&useSSL=false`
`spring.datasource.username=root`
`spring.datasource.password=your_password`
`spring.jpa.hibernate.ddl-auto=update`

# build and run
`mvn clean install`
`mvn spring-boot:run`
 # Swagger

http://localhost:8080/swagger-ui/index.html

# Screenshots

## Swagger
(Add screenshot)

## Doctor APIs
(Add screenshot)

## Availability APIs
(Add screenshot)

## Slot APIs
(Add screenshot)

## Booking APIs
(Add screenshot)

## Postman Responses
(Add screenshot)

# Submission

GitHub Repository:
<add link>

Video Walkthrough:
<add link>

Swagger:
http://localhost:8080/swagger-ui/index.html