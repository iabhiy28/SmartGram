# GramConnect — Cloud Production Deployment Guide 🚀

This document outlines the step-by-step instructions to deploy **GramConnect** to production across leading cloud providers.

---

## 🌟 Option A: 1-Click Deployment on Render (Recommended)

Render allows you to deploy the entire stack (PostgreSQL + Redis + Spring Boot + React) using the included `render.yaml` Blueprint.

### Steps:
1. Push your repository to **GitHub**:
   ```bash
   git init
   git add .
   git commit -m "Initial commit of GramConnect"
   git remote add origin https://github.com/<your-username>/GramConnect.git
   git push -u origin main
   ```
2. Log into [Render Dashboard](https://dashboard.render.com).
3. Click **"New +"** -> **"Blueprint"**.
4. Connect your `GramConnect` GitHub repository.
5. Render will automatically detect `render.yaml` and provision:
   * **`gramconnect-postgres`**: Managed PostgreSQL 16 database.
   * **`gramconnect-redis`**: Managed Redis 7 cache.
   * **`gramconnect-backend`**: Dockerized Spring Boot 3.3.3 web service.
   * **`gramconnect-frontend`**: High-performance static web application with Nginx / CDN.
6. Click **"Apply"**. In ~3 minutes, your full-stack application will be live at `https://gramconnect-frontend.onrender.com`!

---

## ⚡ Option B: Railway Deployment

1. Install Railway CLI:
   ```bash
   npm i -g @railway/cli
   railway login
   ```
2. In the project root, run:
   ```bash
   railway init
   railway up
   ```
3. Add **PostgreSQL** and **Redis** plugins from the Railway dashboard.
4. Set environment variables on the backend service:
   * `SPRING_PROFILES_ACTIVE=prod`
   * `DB_HOST=${{Postgres.PGHOST}}`
   * `DB_PORT=${{Postgres.PGPORT}}`
   * `DB_NAME=${{Postgres.PGDATABASE}}`
   * `DB_USER=${{Postgres.PGUSER}}`
   * `DB_PASSWORD=${{Postgres.PGPASSWORD}}`
   * `REDIS_HOST=${{Redis.REDISHOST}}`
   * `REDIS_PORT=${{Redis.REDISPORT}}`

---

## 🐳 Option C: AWS / DigitalOcean / Ubuntu VPS (Docker Compose)

Deploy to any Linux server with a single command:

```bash
# 1. SSH into your VPS
ssh ubuntu@your-server-ip

# 2. Clone the repository
git clone https://github.com/<your-username>/GramConnect.git
cd GramConnect

# 3. Launch with Docker Compose
docker compose up -d --build

# 4. Verify running containers
docker compose ps
```

### Services Mapping:
* **Frontend:** `http://your-server-ip:80` (or `http://localhost:5173`)
* **Backend API:** `http://your-server-ip:8080`
* **Swagger UI:** `http://your-server-ip:8080/swagger-ui.html`

---

## 🛡️ Production Environment Variables Reference

| Variable | Description | Example |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Spring active profile | `prod` |
| `DB_HOST` | PostgreSQL Host | `localhost` or RDS endpoint |
| `DB_PORT` | PostgreSQL Port | `5432` |
| `DB_NAME` | Database Name | `gramconnect_db` |
| `DB_USER` | Database User | `postgres` |
| `DB_PASSWORD` | Database Password | `StrongSecretPassword123!` |
| `REDIS_HOST` | Redis Cache Host | `localhost` or ElastiCache |
| `REDIS_PORT` | Redis Port | `6379` |
| `JWT_SECRET` | 256-bit Base64 signing key | Auto-generated or custom |
| `PORT` | Backend HTTP Port | `8080` (default) |
