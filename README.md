# 🏨 Hotel Reservation System (En desarrollo)

Aplicación web completa para la **gestión de reservas de hoteles**, desarrollada con **arquitectura de microservicios**.  
El sistema permite administrar hoteles, habitaciones, usuarios y reservas, con roles (admin / usuario) y comunicación entre servicios tanto **síncrona** como **asíncrona**.

> ⚠️ Este proyecto se encuentra **actualmente en desarrollo**.  
> Estoy trabajando en la integración final del frontend con los microservicios y la **dockerización** del sistema completo.

---

## 🚀 Tecnologías utilizadas

### 🧩 Backend (Microservicios)
- ☕ **Java 17** + **Spring Boot**
- ⚙️ **Spring Cloud OpenFeign** → comunicación síncrona entre microservicios  
- 🐇 **RabbitMQ** → mensajería asíncrona para eventos del sistema  
- 🗄️ **MySQL**, **PostgreSQL** y **MongoDB**
- 🔐 **Spring Security** → autenticación y manejo de roles  


### 🎨 Frontend
- ⚛️ **React + TypeScript**
- 🎨 **TailwindCSS**
- ⚡ **Vite**
- 📦 **Axios** → consumo de endpoints del backend  
- 🔄 **React Router** y **Context API** → rutas y manejo de sesión

### 🐳 Contenedores
- **Docker** y **Docker Compose** *(en progreso)*  
  Cada microservicio contará con su contenedor y base de datos independiente.

---

## 🧠 Arquitectura del sistema

El sistema está dividido en microservicios independientes que se comunican mediante **FeignClient** (sincronía) y **RabbitMQ** (asincronía):

| Microservicio | Descripción | Base de Datos |
|----------------|-------------|---------------|
| 🏨 **Hotel Service** | Gestión de hoteles (alta, baja, modificación) | MySQL |
| 🛏️ **Room Service** | Gestión de habitaciones y disponibilidad | PostgreSQL |
| 📅 **Reservation Service** | Creación, consulta y cancelación de reservas | MongoDB |
| 👤 **Auth Service** | Registro, login y roles de usuario (admin / cliente) | MySQL |

---

## 💻 Funcionalidades principales

✅ Listado de hoteles y detalles individuales  
✅ Gestión de habitaciones por hotel  
✅ Creación y cancelación de reservas  
✅ Autenticación de usuario (login / logout)  
✅ Roles: usuario y administrador  
✅ Comunicación entre microservicios con RabbitMQ  
🛠️ Dockerización en progreso  

---

## ⚙️ Estado actual del proyecto

- [x] Backend funcional con comunicación RabbitMQ  
- [x] Frontend base con React y autenticación  
- [x] Integración parcial de reservas y habitaciones  
- [ ] Implementación completa de roles (admin / usuario)  
- [ ] Dockerización final de microservicios  
- [ ] Documentación de API y endpoints  

---

## 🧩 Cómo ejecutar el proyecto

> 🧠 Una vez dockerizado, la ejecución será completamente automática con Docker Compose.

