# API Endpoints - Scheduling System

## 🌐 Base URLs

### Frontend
- **URL:** http://localhost:5173
- **Description:** React application

### Backend
- **Base URL:** http://localhost:8080
- **API Base URL:** http://localhost:8080/api
- **Description:** Spring Boot REST API

---

## 📚 Documentation & Monitoring

### Swagger UI
- **URL:** http://localhost:8080/swagger-ui.html
- **Alternative:** http://localhost:8080/swagger-ui/index.html
- **Description:** Interactive API documentation

### OpenAPI Docs (JSON)
- **URL:** http://localhost:8080/api-docs
- **Description:** OpenAPI 3.0 specification in JSON format

### Health Check
- **URL:** http://localhost:8080/api/health
- **Description:** Application health status
- **Response Example:**
```json
{
  "status": "UP",
  "timestamp": 1234567890,
  "database": {
    "status": "UP",
    "type": "PostgreSQL"
  },
  "scheduler": {
    "status": "UP",
    "started": true,
    "runningJobs": 0
  }
}
```

### Actuator Health
- **URL:** http://localhost:8080/actuator/health
- **Description:** Spring Boot Actuator health endpoint

### Actuator Info
- **URL:** http://localhost:8080/actuator/info
- **Description:** Application information

### Metrics
- **URL:** http://localhost:8080/api/metrics
- **Description:** Custom application metrics
- **Response Example:**
```json
{
  "schedules": {
    "total": 10,
    "enabled": 8,
    "disabled": 2
  },
  "executions": {
    "total": 100,
    "successful": 95,
    "failed": 5,
    "successRate": 95.0
  },
  "quartz": {
    "runningJobs": 2,
    "schedulerName": "SchedulingSystemScheduler",
    "started": true
  },
  "timestamp": 1234567890
}
```

---

## 🔧 API Endpoints

### Schedules

#### Get All Schedules
- **Method:** GET
- **URL:** http://localhost:8080/api/schedules
- **Query Params:** 
  - `page` (optional, default: 0)
  - `size` (optional, default: 10)
- **Response:** Paginated list of schedules

#### Get Schedule by ID
- **Method:** GET
- **URL:** http://localhost:8080/api/schedules/{id}
- **Response:** Single schedule object

#### Create Schedule
- **Method:** POST
- **URL:** http://localhost:8080/api/schedules
- **Body:** CreateScheduleDTO
- **Response:** Created schedule object

#### Update Schedule
- **Method:** PUT
- **URL:** http://localhost:8080/api/schedules/{id}
- **Body:** CreateScheduleDTO
- **Response:** Updated schedule object

#### Delete Schedule
- **Method:** DELETE
- **URL:** http://localhost:8080/api/schedules/{id}
- **Response:** 204 No Content

#### Toggle Schedule Status
- **Method:** POST
- **URL:** http://localhost:8080/api/schedules/{id}/toggle
- **Response:** Updated schedule object

#### Get Schedules by Task
- **Method:** GET
- **URL:** http://localhost:8080/api/schedules/task/{taskId}
- **Response:** List of schedules for specific task

---

### Tasks

#### Get All Tasks
- **Method:** GET
- **URL:** http://localhost:8080/api/tasks
- **Response:** List of all available tasks

#### Get Task by ID
- **Method:** GET
- **URL:** http://localhost:8080/api/tasks/{id}
- **Response:** Single task definition

---

### Execution History

#### Get All Executions
- **Method:** GET
- **URL:** http://localhost:8080/api/execution-history
- **Query Params:**
  - `page` (optional, default: 0)
  - `size` (optional, default: 20)
- **Response:** Paginated list of executions

#### Get Executions by Schedule
- **Method:** GET
- **URL:** http://localhost:8080/api/execution-history/scheduling/{schedulingId}
- **Query Params:**
  - `page` (optional, default: 0)
  - `size` (optional, default: 20)
- **Response:** Paginated list of executions for specific schedule

#### Get Executions by Status
- **Method:** GET
- **URL:** http://localhost:8080/api/execution-history/status/{status}
- **Query Params:**
  - `page` (optional, default: 0)
  - `size` (optional, default: 20)
- **Response:** Paginated list of executions with specific status (SUCCESS/FAILED)

---

## 🗄️ Database

### PostgreSQL
- **Host:** localhost
- **Port:** 5432
- **Database:** scheduling_db
- **Username:** scheduling_user
- **Password:** scheduling_password

### pgAdmin
- **URL:** http://localhost:5050
- **Email:** admin@example.com
- **Password:** admin

---

## 🧪 Testing Endpoints

### Using cURL

#### Health Check
```bash
curl http://localhost:8080/api/health
```

#### Get All Schedules
```bash
curl http://localhost:8080/api/schedules
```

#### Create Schedule
```bash
curl -X POST http://localhost:8080/api/schedules \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Schedule",
    "taskId": "log-task",
    "frequencyType": "ONE_TIME",
    "oneTimeExecutionTime": "2024-12-31T10:00:00",
    "enabled": true,
    "parameters": [
      {
        "parameterName": "message",
        "parameterValue": "Hello World",
        "parameterType": "STRING",
        "required": true
      }
    ]
  }'
```

#### Get Metrics
```bash
curl http://localhost:8080/api/metrics
```

---

## 🔍 Troubleshooting

### 404 Not Found
- ✅ Check that the URL includes `/api` prefix for API endpoints
- ✅ Swagger UI should be at `/swagger-ui.html` (no /api prefix)
- ✅ Health check is at `/api/health`

### 500 Internal Server Error
- ✅ Check backend logs: `docker logs scheduling_backend`
- ✅ Verify database is running: `docker ps`
- ✅ Check database connection in logs

### CORS Errors
- ✅ Verify CorsConfig is loaded
- ✅ Check browser console for specific CORS error
- ✅ Ensure frontend is running on http://localhost:5173

---

## 📝 Notes

1. **Context Path:** All API endpoints are under `/api` context path
2. **Swagger UI:** Accessible at root level (no /api prefix)
3. **Actuator:** Endpoints are under `/actuator` path
4. **Custom Endpoints:** Health and Metrics are under `/api` path
5. **Pagination:** Most list endpoints support pagination with `page` and `size` parameters

---

## 🚀 Quick Start

1. Start all services:
```bash
cd docker
docker-compose up --build
```

2. Wait for services to be ready (check logs)

3. Access the application:
   - Frontend: http://localhost:5173
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Health Check: http://localhost:8080/api/health

4. Test the API using Swagger UI or cURL

---

## 📊 Monitoring

### Check Application Status
```bash
# Health check
curl http://localhost:8080/api/health

# Metrics
curl http://localhost:8080/api/metrics

# Actuator health
curl http://localhost:8080/actuator/health
```

### Check Docker Containers
```bash
# List running containers
docker ps

# Check backend logs
docker logs scheduling_backend --tail 50

# Check frontend logs
docker logs scheduling_frontend --tail 50

# Check database logs
docker logs scheduling_postgres --tail 50
```
