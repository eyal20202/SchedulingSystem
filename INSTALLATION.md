# מדריך התקנה והרצה - Scheduling System

## דרישות מקדימות

### חובה
- **Docker Desktop** - [הורדה](https://www.docker.com/products/docker-desktop/)
- **Docker Compose** (כלול ב-Docker Desktop)
- **Git** - [הורדה](https://git-scm.com/downloads)

### אופציונלי (לפיתוח)
- **Java 17** - [הורדה](https://adoptium.net/)
- **Maven 3.9+** - [הורדה](https://maven.apache.org/download.cgi)
- **Node.js 18+** - [הורדה](https://nodejs.org/)

---

## הרצה מהירה עם Docker (מומלץ)

### שלב 1: Clone הפרויקט
```bash
git clone <repository-url>
cd SchedulingSystem
```

### שלב 2: הרצת המערכת
```bash
cd docker
docker-compose up --build
```

**המערכת תעלה תוך 2-3 דקות**

### שלב 3: גישה לשירותים

| שירות | URL | תיאור |
|-------|-----|-------|
| **Frontend** | http://localhost:5173 | ממשק המשתמש |
| **Backend API** | http://localhost:8080/api | REST API |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | תיעוד API אינטראקטיבי |
| **Health Check** | http://localhost:8080/api/health | בדיקת תקינות |
| **pgAdmin** | http://localhost:5050 | ניהול DB (admin@example.com / admin) |

### שלב 4: עצירת המערכת
```bash
docker-compose down
```

---

## הרצה לפיתוח (ללא Docker)

### 1. הרצת Database
```bash
cd docker
docker-compose up postgres pgadmin
```

### 2. הרצת Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Backend יעלה על: http://localhost:8080

### 3. הרצת Frontend
```bash
cd frontend
npm install
npm run dev
```

Frontend יעלה על: http://localhost:5173

---

## בדיקת תקינות

### 1. בדיקת Backend
```bash
curl http://localhost:8080/api/health
```

תגובה צפויה:
```json
{
  "status": "UP",
  "database": {"status": "UP"},
  "scheduler": {"status": "UP"}
}
```

### 2. בדיקת Frontend
פתח דפדפן: http://localhost:5173

### 3. בדיקת Swagger
פתח דפדפן: http://localhost:8080/swagger-ui.html

---

## פתרון בעיות נפוצות

### בעיה: Port כבר בשימוש
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

### בעיה: Docker לא עולה
```bash
docker-compose down -v
docker system prune -a
docker-compose up --build
```

### בעיה: Database connection failed
1. וודא ש-PostgreSQL Container רץ: `docker ps`
2. בדוק logs: `docker logs scheduling_postgres`
3. המתן 10 שניות לאחר עליית ה-DB

### בעיה: Frontend לא מתחבר ל-Backend
1. בדוק ש-Backend רץ: http://localhost:8080/api/health
2. בדוק CORS settings ב-Backend
3. נקה cache של הדפדפן

---

## הרצת בדיקות

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests (אם קיימות)
```bash
cd frontend
npm test
```

---

## מידע נוסף

### Database Connection
- **Host**: localhost
- **Port**: 5432
- **Database**: scheduling_db
- **Username**: scheduling_user
- **Password**: scheduling_password

### pgAdmin Connection
1. פתח: http://localhost:5050
2. התחבר: admin@example.com / admin
3. הוסף Server:
   - Name: Scheduling DB
   - Host: postgres
   - Port: 5432
   - Database: scheduling_db
   - Username: scheduling_user
   - Password: scheduling_password

---

## תמיכה

לשאלות או בעיות, פנה למפתח או בדוק את:
- [README.md](README.md) - תיעוד מלא
- [API_ENDPOINTS.md](API_ENDPOINTS.md) - תיעוד API
- Swagger UI - תיעוד אינטראקטיבי
