# 🏨 Hotel Reservation System (En desarrollo)

Aplicación web completa para la **gestión de reservas de hoteles**, desarrollada con **arquitectura de microservicios**.  
El sistema permite administrar hoteles, habitaciones, usuarios, reservas y pagos, con roles (admin / usuario), autenticación JWT y comunicación entre servicios tanto **síncrona (REST/Feign)** como **asíncrona (RabbitMQ)**.

> ⚠️ Este proyecto se encuentra **en desarrollo activo**.  
> Actualmente estoy trabajando en la **dockerización completa** y en la integración final del frontend con todos los microservicios.

---

## 🚀 Tecnologías utilizadas

### 🧩 Backend (Microservicios)
- ☕ **Java 17** + **Spring Boot 3**
- ⚙️ **Spring Cloud Netflix Eureka** → registro y descubrimiento de servicios  
- 🌐 **Spring Cloud Gateway** → enrutamiento de peticiones y balanceo de carga  
- 🔁 **Spring Cloud OpenFeign** → comunicación síncrona entre microservicios  
- 🐇 **RabbitMQ** → mensajería asíncrona (event-driven communication)  
- 🔐 **Spring Security + JWT** → autenticación y autorización  
- 🗄️ **JPA / Hibernate** → persistencia  
- 🧩 **MySQL**, **PostgreSQL** y **MongoDB** → bases de datos distribuidas  
- 🧰 **Lombok**, **Validation**, **ModelMapper**

---

### 🎨 Frontend
- ⚛️ **React + TypeScript**
- 🎨 **TailwindCSS**
- ⚡ **Vite**
- 📦 **Axios** → consumo de endpoints del backend  
- 🔄 **React Router DOM** y **Context API** → manejo de sesión y rutas protegidas  

---

### 🐳 Contenedores y despliegue
- 🐋 **Docker** → contenedores individuales por microservicio  
- ⚙️ **Docker Compose** → orquestación del ecosistema completo (Gateway, Eureka, RabbitMQ, BDs, etc.)  
- ☁️ **VPS Deployment (planeado)** → despliegue del sistema completo usando Docker o Kubernetes  

---

## 🧠 Arquitectura del sistema

El proyecto sigue una **arquitectura de microservicios distribuida**, donde cada servicio es independiente y escalable.  
Todos los servicios se registran en **Eureka Server** y se comunican entre sí mediante el **API Gateway**.

### 🔗 Comunicación
| Tipo | Tecnología | Descripción |
|------|-------------|-------------|
| ⚙️ Síncrona | Spring Cloud OpenFeign | Comunicación directa entre microservicios |
| 🐇 Asíncrona | RabbitMQ | Notificación de eventos (por ejemplo, al eliminar una habitación, se cancelan reservas asociadas) |

---

## 🧩 Microservicios principales

| Microservicio | Descripción | Base de Datos |
|----------------|-------------|---------------|
| 🧭 **Eureka Server** | Registro y descubrimiento de servicios (service registry) | — |
| 🌐 **API Gateway** | Punto de entrada al sistema, balanceo de carga y seguridad global | — |
| 🏨 **Hotel Service** | CRUD de hoteles | MySQL |
| 🛏️ **Room Service** | Gestión de habitaciones y disponibilidad | PostgreSQL |
| 📅 **Reservation Service** | Creación, consulta y cancelación de reservas; escucha eventos de Room | MongoDB |
| 👤 **User / Auth Service** | Registro, login y manejo de roles (admin / user) | MySQL |
| 💳 **Payment Service** | Procesamiento y gestión de pagos de reservas | PostgreSQL |
| 📝 **Review Service** | Opiniones y calificaciones de usuarios | MongoDB |

---

## 🔐 Seguridad

El sistema utiliza **Spring Security + JWT (JSON Web Tokens)** para la autenticación y autorización.

- ✅ Registro e inicio de sesión de usuarios (`/auth/register`, `/auth/login`)  
- ✅ Generación y validación de tokens JWT  
- ✅ Roles definidos: `ROLE_ADMIN` y `ROLE_USER`  
- ✅ El **API Gateway** intercepta todas las peticiones y valida el token antes de enrutar  
- ✅ Los microservicios internos confían en el token propagado por el Gateway  

---

## 📬 Comunicación entre microservicios

### 🔁 Comunicación síncrona (OpenFeign)

Ejemplo:  
`ReservationService` obtiene datos del `RoomService` a través de un cliente Feign:

```java
@FeignClient(name = "msvc-rooms")
public interface RoomClient {
    @GetMapping("/rooms/{id}")
    RoomDTO findRoomById(@PathVariable Long id);
}

