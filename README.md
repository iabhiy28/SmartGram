# GramConnect (ಗ್ರಾಮಕನೆಕ್ಟ್ / ग्रामकनेक्ट)
> **Digital Help & Service Platform for Indian Villages**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.3-blue.svg)](https://reactjs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7-red.svg)](https://redis.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 🌾 Project Overview

**GramConnect** is a production-grade full-stack digital rural platform engineered to solve real-world fragmentation in Indian village communities. It provides a single unified digital access point for:
* **Local Services Marketplace:** Finding verified electricians, plumbers, masons, carpenters, and appliance mechanics with transparent rate cards.
* **Rural Jobs & Labor Marketplace:** Daily wage farm work, harvesting assistance, and construction labor with concurrency-safe worker capacity locking.
* **Farm Machinery & Equipment Rental:** Booking tractors, rotavators, harvesters, and irrigation pumps with pessimistic date-range collision prevention.
* **Civic Grievance & SLA Tracking:** Reporting water supply, street lighting, road, and sanitation issues with automated `@Scheduled` SLA breach detection daemons.
* **Government Welfare Discovery:** 1-click citizen eligibility evaluation across Central (PM-KISAN, PMAY-G) and State (Gruha Lakshmi) schemes.
* **Village Emergency Directory:** Tap-to-call direct dialer for Primary Health Centers (PHC), police, electricity wiremen, and veterinary care.
* **Panchayat Command Center:** Real-time KPI analytics, grievance queue resolution, and broadcast notification dispatch.

---

## 🏗️ Tech Stack

| Layer | Technologies |
|---|---|
| **Backend** | Java 17, Spring Boot 3.3.3, Spring Security 6, Spring Data JPA, Flyway 10, JJWT 0.12.6, STOMP WebSockets |
| **Frontend** | React 18, Vite 5, TypeScript 5, TanStack Query v5, i18next (English, Hindi, Kannada), Lucide Icons |
| **Database & Caching** | PostgreSQL 16 (Spatial & Partial Indexes, JSONB), Redis 7 (`@Cacheable` Reference Data) |
| **DevOps & Containers** | Docker, Docker Compose, Multi-stage builds (OpenJDK 17 + Nginx Alpine), GitHub Actions CI/CD |

---

## 🚀 Quick Start (Docker Compose)

The fastest way to launch the entire multi-container environment (PostgreSQL + Redis + Spring Boot Backend + React Frontend):

```bash
# Clone the repository
git clone https://github.com/your-username/GramConnect.git
cd GramConnect

# Launch all 4 services in detached mode
docker compose up -d --build
```

### Access URLs:
* **Frontend Web App:** [http://localhost:5173](http://localhost:5173) (or Port 80 in production)
* **Backend REST API:** [http://localhost:8080](http://localhost:8080)
* **Swagger UI / OpenAPI 3.0 Documentation:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* **Health Check:** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

---

## 💻 Local Development Setup

### 1. Backend (Spring Boot 3.3)
```bash
cd backend

# Run automated unit & concurrency test suite
mvn test

# Run Spring Boot backend locally (requires Postgres on port 5432 & Redis on 6379)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 2. Frontend (React 18 + Vite)
```bash
cd frontend

# Install dependencies
npm install

# Run Vite dev server with Hot Module Replacement (HMR)
npm run dev

# Run TypeScript check & production build
npm run build
```

---

## 🔒 Security & Concurrency Architecture

### 1. Concurrency-Safe Date-Range Equipment Booking
Prevents double-booking collisions when multiple farmers attempt to rent the same machinery concurrently during seasonal sowing/harvesting peaks:
$$\text{Start}_1 \le \text{End}_2 \quad \text{AND} \quad \text{End}_1 \ge \text{Start}_2$$
Enforced at the database layer using `SELECT ... FOR UPDATE` row-level pessimistic locking.

### 2. Capacity-Safe Worker Acceptance Engine
Locks the job entity atomically during applicant confirmation, ensuring `workers_accepted < workers_needed` and automatically transitioning status to `FILLED` on capacity saturation.

### 3. Refresh Token Rotation (RTR) with Breach Detection
Refresh tokens are stored as SHA-256 hashes. If an already-revoked refresh token is replayed, all active sessions for that user family are revoked immediately.

---

## 🧪 Automated Testing

```bash
cd backend
mvn test
```
* `AuthServiceTest`: BCrypt password hashing, session creation, duplicate phone prevention.
* `EquipmentServiceTest`: Double-booking date-collision detection, self-booking rejection.
* `JobMarketplaceServiceTest`: Capacity locking and auto-fill status transitions.

---

## 📜 License
Distributed under the MIT License. Built with ❤️ for rural Indian communities.
