# EasyTune Fuel Map Correction App

Full-stack application for recalculating VE tables using measured/target AFR.

## Folder structure

- `backend/` - Java 21 + Spring Boot REST API.
- `frontend/` - Angular 17 standalone app.

## Backend architecture

- `model/` contains `FuelTable`, `CalculationRequest`, `CalculationResponse`.
- `service/VeCalculatorService` validates tables and applies:
  - `VE_new = VE_old * (AFR_actual / AFR_target)`.
  - Keeps original VE if target/actual is zero or missing.
  - Computes delta % and rounds to 2 decimals.
- `controller/VeCalculatorController` exposes `POST /api/ve/calculate`.
- `exception/` maps validation failures to HTTP 400.

## Frontend architecture

- Standalone `AppComponent` manages tabs and user actions.
- `TableEditorComponent` handles dynamic axis editing (add/remove/reorder/import CSV).
- `VeApiService` sends calculation requests to backend.
- UI supports:
  - VE Current / AFR Actual / AFR Target editing
  - Generate VE corrected
  - Delta highlighting over ±30%
  - CSV export for corrected VE
  - Optional 3x3 smoothing
  - Global AFR target override

## Run backend

```bash
cd backend
mvn spring-boot:run
```

## Run frontend

```bash
cd frontend
npm install
npm start
```
