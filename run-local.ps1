# GramConnect Local Orchestrator Script (Windows PowerShell)

Write-Host "====================================================" -ForegroundColor Green
Write-Host " GramConnect - Starting Local Development Services " -ForegroundColor Green
Write-Host "====================================================" -ForegroundColor Green

# 1. Check Docker daemon
if (Get-Command docker -ErrorAction SilentlyContinue) {
    Write-Host "[1/3] Launching PostgreSQL 16 and Redis 7 via Docker Compose..." -ForegroundColor Cyan
    docker compose up -d postgres redis
} else {
    Write-Host "[!] Docker not detected in PATH. Ensure PostgreSQL (5432) and Redis (6379) are running locally." -ForegroundColor Yellow
}

# 2. Check Backend Maven & Build
$mavenBin = "$env:USERPROFILE\.maven\apache-maven-3.9.6\bin"
if (Test-Path "$mavenBin\mvn.cmd") {
    $env:Path = "$mavenBin;$env:Path"
}

Write-Host "[2/3] Verifying Backend Test Suite..." -ForegroundColor Cyan
Push-Location "$PSScriptRoot\backend"
mvn test -q
if ($LASTEXITCODE -eq 0) {
    Write-Host " ✔ Backend unit tests passed (0 failures)" -ForegroundColor Green
} else {
    Write-Host " ✘ Backend test failed. Check test logs." -ForegroundColor Red
}
Pop-Location

# 3. Check Frontend Build
Write-Host "[3/3] Checking React Frontend Production Build..." -ForegroundColor Cyan
Push-Location "$PSScriptRoot\frontend"
npm run build
if ($LASTEXITCODE -eq 0) {
    Write-Host " ✔ Frontend build verified successfully!" -ForegroundColor Green
}
Pop-Location

Write-Host "`nReady! To start the development servers:" -ForegroundColor Yellow
Write-Host "  Terminal 1 (Backend):  cd backend && mvn spring-boot:run"
Write-Host "  Terminal 2 (Frontend): cd frontend && npm run dev"
Write-Host "  Frontend URL:          http://localhost:5173"
Write-Host "  Swagger UI:            http://localhost:8080/swagger-ui.html`n"
