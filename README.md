
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
git clone <https://github.com/amogh8021/Clinzo-Telemedicine>
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
<img width="1532" height="851" alt="image" src="https://github.com/user-attachments/assets/6d02ce6b-6352-40cf-8cda-296be9402a93" />
<img width="1532" height="851" alt="image" src="https://github.com/user-attachments/assets/b3cfa897-a690-45b0-a2d0-e8d01e60c785" />

## Postman Responses
<img width="1920" height="1080" alt="Screenshot From 2026-07-25 20-33-35" src="https://github.com/user-attachments/assets/ee1a16f9-9d8b-4f0f-802d-d9df4e5a1e09" />
<img width="1920" height="1080" alt="Screenshot From 2026-07-27 02-00-26" src="https://github.com/user-attachments/assets/4bb55696-8fde-46a0-8ad4-5fa077cad2c0" />
<img width="1920" height="1080" alt="Screenshot From 2026-07-27 02-12-58" src="https://github.com/user-attachments/assets/a5cc7fd4-a135-47a3-9ff3-334e0962ea4a" />
<img width="1920" height="1080" alt="Screenshot From 2026-07-27 02-22-01" src="https://github.com/user-attachments/assets/4b6c46e5-3cfa-4aad-bf16-ee7b37493260" />
<img width="1920" height="1080" alt="Screenshot From 2026-07-27 02-50-09" src="https://github.com/user-attachments/assets/5e019d41-d2c0-4d43-a0af-b5356d4b88ce" />
<img width="1920" height="1080" alt="Screenshot From 2026-07-27 02-52-25" src="https://github.com/user-attachments/assets/f7cf1f4d-57a5-4ec8-946b-b1e92cd51da1" />
<img width="1920" height="1080" alt="Screenshot From 2026-07-27 02-54-14" src="https://github.com/user-attachments/assets/e328dc11-8b9c-400f-abda-6b77d561b47a" />
<img width="1920" height="1080" alt="Screenshot From 2026-07-27 02-55-27" src="https://github.com/user-attachments/assets/5608944e-f700-46ac-8bf3-7d0c45760020" />
<img width="1920" height="1080" alt="Screenshot From 2026-07-27 13-20-58" src="https://github.com/user-attachments/assets/4fffb7c1-f609-4c60-8fdf-0b73c9dd7ba4" />
<img width="1920" height="1080" alt="Screenshot From 2026-07-27 13-20-58" src="https://github.com/user-attachments/assets/1b230ee9-c192-47c0-8dcf-4377f72b7657" />
<img width="1920" height="1080" alt="Screenshot From 2026-07-27 16-08-02" src="https://github.com/user-attachments/assets/0f0100d1-e8ee-42c4-ab4e-bfe04bdd0f17" />
<img width="1920" height="1080" alt="Screenshot From 2026-07-27 16-08-02" src="https://github.com/user-attachments/assets/979b3463-06e9-4382-a7ce-08c09cbf1ace" />

# Submission

GitHub Repository:
<add link>

Video Walkthrough:
<add link>

Swagger:
http://localhost:8080/swagger-ui/index.html

Postman API link
you can directly import these api in your postman
https://amogh-8021-7-a-80112-es-team.postman.co/workspace/Personal-Workspace~351a8937-67e4-42ac-82b5-e4ed385382fe/collection/45718098-c1e77ee8-cd81-45df-b707-636091b382ca?action=share&creator=45718098


