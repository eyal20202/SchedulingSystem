# Scheduling System

מערכת ניהול תזמון משימות מלאה עם ממשק React, Backend ב-Spring Boot, ו-Quartz Scheduler.

## 📋 תוכן עניינים

- [סקירה כללית](#סקירה-כללית)
- [טכנולוגיות](#טכנולוגיות)
- [תכונות עיקריות](#תכונות-עיקריות)
- [התקנה והרצה](#התקנה-והרצה)
- [ארכיטקטורה](#ארכיטקטורה)
- [API Documentation](#api-documentation)
- [בדיקות](#בדיקות)
- [החלטות עיצוב](#החלטות-עיצוב)

## 🎯 סקירה כללית

מערכת תזמון משימות המאפשרת יצירה וניהול של ביצועים מתוזמנים של משימות מוגדרות מראש. המערכת תומכת בתזמון חד-פעמי, חוזר, שבועי וביטויי Cron מתקדמים.

## 🛠 טכנולוגיות

### Frontend
- **React 18** - ספריית UI
- **TypeScript** - Type safety
- **Vite** - Build tool
- **Zustand** - State management
- **Axios** - HTTP client
- **Tailwind CSS** - Styling
- **Lucide React** - Icons

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA** - Data access
- **Quartz Scheduler** - Job scheduling
- **PostgreSQL** - Database
- **Lombok** - Code generation
- **Springdoc OpenAPI** - API documentation

### Infrastructure
- **Docker & Docker Compose**
- **PostgreSQL 15**
- **pgAdmin** - Database management

## ✨ תכונות עיקריות

### ניהול תזמונים (Schedulings)
- ✅ יצירה, עריכה, מחיקה וצפייה בתזמונים
- ✅ תצוגת טבלה עם כל התזמונים הקיימים
- ✅ סינון ומיון תזמונים

### אפשרויות תזמון
- **One-time** - ביצוע חד-פעמי בזמן מוגדר
- **Recurring** - ביצוע חוזר כל X דקות/שעות
- **Weekly** - ביצוע שבועי בימים ושעות מוגדרים
- **Cron** - ביטויי Cron מתקדמים

### משימות מוגדרות מראש
- **Log Task** - כתיבת הודעה ללוג
- **Email Task** - שליחת אימייל (dummy)
- **Database Query Task** - ביצוע שאילתת DB
- כל משימה עם פרמטרים מוגדרים וולידציה

### פרמטרים למשימות
- הגדרת schema לכל משימה (שם, טיפוס, required/optional)
- ולידציה ב-UI וב-Backend
- העברת פרמטרים בזמן ביצוע

### היסטוריית ביצועים
- מעקב אחר כל ביצוע של משימה
- סטטוס הצלחה/כישלון
- זמני התחלה וסיום
- הודעות שגיאה במקרה של כישלון

## 🚀 התקנה והרצה

### דרישות מקדימות
- Docker & Docker Compose
- (אופציונלי) Java 17 + Maven לפיתוח Backend
- (אופציונלי) Node.js 18+ לפיתוח Frontend

### הרצה מהירה עם Docker

1. **Clone הפרויקט**
```bash
git clone <repository-url>
cd SchedulingSystem
```

2. **הרצת המערכת**
```bash
cd docker
docker-compose up --build
```

3. **גישה לשירותים**

| Service | URL | Description |
|---------|-----|-------------|
| Frontend | http://localhost:5173 | React Application |
| Backend API | http://localhost:8080/api | REST API Base URL |
| Swagger UI | http://localhost:8080/swagger-ui.html | Interactive API Documentation |
| API Docs (JSON) | http://localhost:8080/api-docs | OpenAPI 3.0 Specification |
| Health Check | http://localhost:8080/api/health | Custom Health Endpoint |
| Metrics | http://localhost:8080/api/metrics | Application Metrics |
| Actuator Health | http://localhost:8080/actuator/health | Spring Boot Actuator |
| Actuator Info | http://localhost:8080/actuator/info | Application Info |
| pgAdmin | http://localhost:5050 | Database Management (admin@example.com / admin) |

### הרצה לפיתוח

#### Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

#### Frontend
```bash
cd frontend
npm install
npm run dev
```

#### Database
```bash
cd docker
docker-compose up postgres pgadmin
```

## 🏗 ארכיטקטורה

### Backend Structure
```
backend/src/main/java/com/scheduling/
├── config/          # Configuration classes (Quartz, CORS)
├── controller/      # REST Controllers
├── dto/            # Data Transfer Objects
├── entity/         # JPA Entities
├── exception/      # Custom exceptions & handlers
├── job/            # Quartz Job implementations
├── repository/     # JPA Repositories
├── service/        # Business logic
└── task/           # Task definitions & registry
```

### Frontend Structure
```
frontend/src/
├── components/     # React components
├── config/        # Configuration
├── hooks/         # Custom React hooks
├── services/      # API services
├── stores/        # Zustand stores
├── types/         # TypeScript types
└── utils/         # Utility functions
```

### Database Schema
- **schedulings** - תזמונים
- **tasks** - משימות מוגדרות מראש
- **task_parameters** - פרמטרים למשימות
- **execution_history** - היסטוריית ביצועים
- **qrtz_*** - טבלאות Quartz

## 📚 API Documentation

### Endpoints

#### Schedulings
- `GET /api/schedulings` - קבלת כל התזמונים
- `GET /api/schedulings/{id}` - קבלת תזמון ספציפי
- `POST /api/schedulings` - יצירת תזמון חדש
- `PUT /api/schedulings/{id}` - עדכון תזמון
- `DELETE /api/schedulings/{id}` - מחיקת תזמון

#### Tasks
- `GET /api/tasks` - קבלת כל המשימות
- `GET /api/tasks/{id}` - קבלת משימה ספציפית

#### Execution History
- `GET /api/execution-history` - קבלת היסטוריית ביצועים
- `GET /api/execution-history/scheduling/{schedulingId}` - היסטוריה לתזמון ספציפי

### דוגמת Request - יצירת תזמון

```json
{
  "name": "Daily Log Task",
  "taskId": 1,
  "scheduleType": "RECURRING",
  "enabled": true,
  "startTime": "2024-01-01T10:00:00",
  "intervalMinutes": 60,
  "parameters": {
    "message": "Hello from scheduled task"
  }
}
```

## 🧪 בדיקות

### הרצת בדיקות Backend
```bash
cd backend
mvn test
```

### כיסוי בדיקות
- Unit tests לכל ה-Services
- Integration tests ל-Controllers
- Repository tests
- Job execution tests

## 💡 החלטות עיצוב

### Backend
1. **Quartz Scheduler** - נבחר בגלל יכולות תזמון מתקדמות, persistence, ותמיכה ב-clustering
2. **Task Registry Pattern** - מאפשר הוספת משימות חדשות בקלות ללא שינוי קוד קיים
3. **DTO Pattern** - הפרדה בין entities ל-API responses למען גמישות ואבטחה
4. **Execution History** - מעקב אחר כל ביצוע למטרות audit ו-debugging
5. **Parameter Schema** - ולידציה מובנית של פרמטרים עם type safety

### Frontend
1. **Zustand** - state management פשוט וקל משקל
2. **TypeScript** - type safety ו-better developer experience
3. **Component-based** - ארכיטקטורה מודולרית וניתנת לשימוש חוזר
4. **Tailwind CSS** - styling מהיר ועקבי
5. **Axios Interceptors** - error handling מרכזי

### Database
1. **PostgreSQL** - database יציב ומתקדם עם תמיכה מלאה ב-Quartz
2. **JPA/Hibernate** - ORM לניהול קל של entities
3. **Flyway/Liquibase** - (אופציונלי) לניהול migrations

## 🔧 הגדרות נוספות

### Environment Variables

#### Backend
```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/scheduling_db
SPRING_DATASOURCE_USERNAME=scheduling_user
SPRING_DATASOURCE_PASSWORD=scheduling_password
```

#### Frontend
```properties
VITE_API_BASE_URL=http://localhost:8080
```

## 🤖 שימוש בכלי AI

במהלך הפיתוח נעשה שימוש בכלים הבאים:
- **Amazon Q Developer** - סיוע בכתיבת קוד, debugging, ו-code review
- **Cursor AI** - עזרה בארכיטקטורה ובעיצוב מערכת
- השימוש היה בעיקר לייעול זמן פיתוח, הצעות לפתרונות, ובדיקת best practices

## 📝 הנחות

1. המערכת מיועדת לסביבת development/demo
2. Authentication & Authorization לא מומשו (ניתן להוסיף בעתיד)
3. Email Task הוא dummy implementation
4. הזמנים בממשק הם בזמן מקומי של המשתמש
5. הגבלת concurrent executions לא מומשת (ניתן להוסיף)

## 🔮 שיפורים עתידיים

- [ ] Authentication & Authorization
- [ ] Real-time notifications
- [ ] Advanced monitoring & metrics
- [ ] Task dependencies
- [ ] Retry mechanisms
- [ ] Email notifications on failures
- [ ] Export/Import configurations
- [ ] Multi-tenancy support

## 📄 License

MIT License

## 👥 Contact

לשאלות או הערות, אנא פנה ל-[your-email@example.com]
