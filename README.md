# Personal Finance Manager - Backend

Backend service for the Personal Finance Manager application built using Spring Boot.  
This API handles user authentication and transaction management for tracking income and expenses.

---

## Tech Stack

- Java 17
- Spring Boot
- Spring Security (Session Authentication)
- Spring Data JPA (Hibernate)
- H2 / MySQL
- Maven

---

## Features

- User Registration and Login
- Secure Session-Based Authentication
- Create, Update, Delete Transactions
- Track Income and Expenses
- Input Validation
- REST API Architecture

---

## 📦 Installation & Setup

### Backend Setup

1. Navigate to the backend directory:
```bash
cd backend
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The backend server will start on `http://localhost:8080`


---

## Public APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login user |
| GET | / 

---

## Protected APIs

(Login required)

| Method | Endpoint |
|---|---|
| POST | /api/transactions |
| GET | /api/transactions |
| PUT | /api/transactions/{id} |
| DELETE | /api/transactions/{id} |
