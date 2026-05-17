# 👤 User Service

Microservice responsible for managing user profiles (clients and contractors) in the Clean Pro Solutions platform. Supports geospatial search for nearby contractors using MongoDB 2dsphere index.

---

## 📋 Service Info

| Property     | Value                                         |
|--------------|-----------------------------------------------|
| Port         | `8082`                                        |
| Database     | MongoDB — `user_db`                           |
| RabbitMQ     | Consumer (`RatingCreatedEvent`)               |
| Registry     | Eureka (`user-service`)                       |

---

## 🔄 Main Flow — Sequence Diagrams

### User Creation Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant UC as UserController
    participant US as UserService
    participant DB as MongoDB (user_db)

    C->>UC: POST /users { name, email, phone, role }
    UC->>US: createUser(request)
    US->>DB: findByEmail(email)
    DB-->>US: null (not found)
    US->>DB: save(user)
    DB-->>US: savedUser
    US-->>UC: UserResponse
    UC-->>C: 201 Created { id, name, email, role }
```

### Nearby Contractor Search Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant UC as UserController
    participant US as UserService
    participant DB as MongoDB (user_db)

    C->>UC: GET /users/nearby?lat=-23.5&lon=-46.6&radiusKm=10
    UC->>US: findNearbyContractors(lat, lon, radiusKm)
    US->>DB: geoNear query (2dsphere index)
    DB-->>US: List<User> within radius
    US-->>UC: List<UserResponse>
    UC-->>C: 200 OK [ { id, name, location, ... } ]
```

### Rating Update Flow (Event Consumer)

```mermaid
sequenceDiagram
    participant MQ as RabbitMQ
    participant UL as UserEventListener
    participant US as UserService
    participant DB as MongoDB (user_db)

    MQ->>UL: RatingCreatedEvent { reviewedId, score }
    UL->>US: updateContractorRating(reviewedId, score)
    US->>DB: findById(reviewedId)
    DB-->>US: user
    US->>US: recalculateAverageRating()
    US->>DB: save(updatedUser)
```

---

## 🏗️ Internal Architecture

```mermaid
flowchart TD
    A[UserController] --> B[UserService]
    B --> C[UserRepository]
    B --> D[UserMapper]
    C --> E[(MongoDB user_db)]
    F[RabbitMQ Consumer] --> B
    G[2dsphere Index] --> C
```

---

## 📡 API Endpoints

| Method | Path                           | Request Body / Params                          | Response                             |
|--------|--------------------------------|------------------------------------------------|--------------------------------------|
| POST   | `/users`                       | `{ name, email, phone, role }`                 | `201 { id, name, email, role }`      |
| GET    | `/users/{id}`                  | —                                              | `200 UserResponse`                   |
| GET    | `/users/email/{email}`         | —                                              | `200 UserResponse`                   |
| PUT    | `/users/{id}`                  | `{ name, phone, ... }`                         | `200 UserResponse`                   |
| DELETE | `/users/{id}`                  | —                                              | `204 No Content`                     |
| PUT    | `/users/{id}/contractor-profile` | `{ specialties, hourlyRate, bio }`           | `200 UserResponse`                   |
| POST   | `/users/{id}/device-tokens`    | `{ token, platform }`                          | `200 UserResponse`                   |
| GET    | `/users/nearby`                | `?lat=X&lon=Y&radiusKm=Z`                      | `200 [ UserResponse ]`               |

---

## ⚙️ Environment Variables

| Variable                    | Description                  | Default                                |
|-----------------------------|------------------------------|----------------------------------------|
| `SPRING_DATA_MONGODB_URI`   | MongoDB connection URI       | `mongodb://localhost:27017/user_db`    |
| `RABBITMQ_HOST`             | RabbitMQ host                | `localhost`                            |
| `RABBITMQ_PORT`             | RabbitMQ port                | `5672`                                 |
| `EUREKA_SERVER_URL`         | Eureka registry URL          | `http://localhost:8761/eureka`         |

---

## 🚀 Build & Run

### Build
```bash
mvn clean install
```

### Run locally
```bash
mvn spring-boot:run
```

### Run with Docker Compose
```bash
docker-compose up user-service
```

---

## 🧪 How to Test

### Create a user
```bash
curl -X POST http://localhost:8082/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@exemplo.com",
    "phone": "11999990000",
    "role": "CLIENT"
  }'
```

### Get user by ID
```bash
curl http://localhost:8082/users/64a1b2c3d4e5f6a7b8c9d0e1
```

### Update contractor profile
```bash
curl -X PUT http://localhost:8082/users/64a1b2c3d4e5f6a7b8c9d0e1/contractor-profile \
  -H "Content-Type: application/json" \
  -d '{
    "specialties": ["RESIDENTIAL", "COMMERCIAL"],
    "hourlyRate": 80.00,
    "bio": "10 years of experience"
  }'
```

### Find nearby contractors
```bash
curl "http://localhost:8082/users/nearby?lat=-23.5505&lon=-46.6333&radiusKm=15"
```

---

## 🗂️ Project Structure

```
clean-pro-solutions-user-service/
├── src/main/java/
│   └── com/cleanpro/user/
│       ├── controller/     # REST endpoints
│       ├── service/        # Business logic
│       ├── repository/     # MongoDB repositories (2dsphere)
│       ├── dto/            # Request/Response records
│       ├── model/          # User entity
│       ├── mapper/         # Entity <-> DTO mapping
│       ├── config/         # RabbitMQ config
│       └── exception/      # Custom exceptions
├── src/test/
└── pom.xml
```
