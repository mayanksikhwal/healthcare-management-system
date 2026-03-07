# Healthcare Management System

A full-stack microservices application for managing patient appointments in healthcare facilities. Built this to get hands-on experience with microservices architecture, async messaging, and cloud deployment — things I kept reading about but wanted to actually build.

🌐 **Live Demo: [bit.ly/Healthcare-System](https://bit.ly/Healthcare-System)**

---

## What it does

Patients can register, log in, and book appointments with doctors. Doctors get their own dashboard to view upcoming appointments. When an appointment is booked, the patient automatically receives a confirmation email — all handled asynchronously via RabbitMQ.

## Architecture

Three independent Spring Boot services talking to each other:

- **User Service** — handles registration, login, and JWT authentication
- **Appointment Service** — manages appointment booking and status updates
- **Notification Service** — listens to RabbitMQ and sends confirmation emails via SendGrid

The frontend is a React app that talks to all three services through their respective REST APIs.

## Tech Stack

- Java 17 + Spring Boot 3
- React + Material UI
- MySQL (Aiven) — persistent data storage
- RabbitMQ (CloudAMQP) — async messaging between services
- JWT — stateless authentication
- SendGrid — transactional email delivery
- Docker + Docker Compose — local development
- Render — cloud deployment

## Things I learned building this

Async messaging was the trickiest part. I initially had the appointment service calling the notification service directly via HTTP, which works but defeats the purpose of microservices. Switching to RabbitMQ meant the appointment service didn't need to care whether the notification service was even running.

JWT across multiple services was also interesting — each service validates the token independently without calling the user service, which keeps things decoupled.

Deployment on Render had its own surprises — SMTP ports are blocked, services spin down on the free tier, and environment variables behave differently than local Docker. Figured it all out eventually.

## Running locally

Make sure Docker is installed, then:

```bash
git clone https://github.com/mayanksikhwal/healthcare-management-system
cd healthcare-management-system
docker-compose up
```

That's it. All 6 containers (MySQL, RabbitMQ, 3 services, frontend) start up together.

Open http://localhost:3000 to use the app.

## Project Structure

```
healthcare-management-system/
├── user-service/          # Auth + user management (port 8080)
├── appointment-service/   # Appointment CRUD (port 8081)
├── notification-service/  # Email notifications (port 8082)
├── healthcare-frontend/   # React app (port 3000)
└── docker-compose.yml
```

## Status

✅ Live and working
