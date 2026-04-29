# 🏨 Hotel Reservation System 

Aplicación web completa para la **gestión de reservas de hoteles**, desarrollada con **arquitectura de microservicios**.  
El sistema permite administrar hoteles, habitaciones, usuarios, reservas y pagos, con roles (**admin / usuario**), autenticación **JWT** y comunicación entre servicios tanto **síncrona (REST/Feign)** como **asíncrona (RabbitMQ)**.

La aplicación está desplegada y disponible en el siguiente enlace:  

👉 [https://hotelfly.netlify.app/](https://hotelfly.netlify.app/)  🌐

---


## 🧱 Arquitectura General

<p align="center">
  <img src="./Arquitectura%20de%20la%20Aplicación.jpg" alt="Arquitectura del Sistema" width="800">
</p>

> Diagrama general del sistema, mostrando la comunicación entre los microservicios, el API Gateway, las bases de datos y RabbitMQ.


---

## 🚀 Tecnologías utilizadas

### 🧩 Backend (Microservicios)
- ☕ **Java 17** + **Spring Boot 3**
- ⚙️ **Spring Cloud Netflix Eureka** → registro y descubrimiento de servicios  
- 🌐 **Spring Cloud Gateway** → enrutamiento de peticiones y balanceo de carga  
- 🔁 **Spring Cloud OpenFeign** → comunicación síncrona entre microservicios  
- 🐇 **RabbitMQ** → mensajería asíncrona *(event-driven communication)*  
- 🔐 **Spring Security + JWT** → autenticación y autorización  
- 🗄️ **JPA / Hibernate** → persistencia  
- 🧩 **MySQL**, **PostgreSQL** y **MongoDB** → bases de datos distribuidas  
- 🧰 **Lombok**, **Validation**, **ModelMapper**

---

### 🧪 Testing & Calidad (Enfoque en msvc-reservations)
- ✅ **JUnit 5 & Mockito** → Tests unitarios robustos.
- 🐋 **Testcontainers** → Tests de integración real con instancias de **Docker (MongoDB)**.
- 📊 **Jacoco** → Reportes de cobertura de código.
- 🤖 **GitHub Actions** → Pipeline de **CI (Continuous Integration)** para ejecución automática de tests en cada push.

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
- ☁️ **VPS Deployment (planeado)** → despliegue del sistema completo usando **Docker o Kubernetes**  

---

## 🧠 Arquitectura del sistema

El proyecto sigue una **arquitectura de microservicios distribuida**, donde cada servicio es independiente y escalable.  
Todos los servicios se registran en **Eureka Server** y se comunican entre sí mediante el **API Gateway**.

---

## 💎 Microservicio Estrella: msvc-reservations

El microservicio de **Reservations** ha sido diseñado como el estándar de calidad del proyecto, implementando una suite de pruebas profesional:

### 🔬 Estrategia de Testing
- **Tests Unitarios:** Cobertura total de la lógica de negocio en la capa de `Service` utilizando **Mockito**.
- **Tests de Integración Reales:** A diferencia de los mocks tradicionales, se utiliza **Testcontainers** para levantar un contenedor real de **MongoDB** durante las pruebas. Esto garantiza que las consultas, índices y persistencia funcionen exactamente igual que en producción.
- **Aislamiento de Perfiles:** Implementación de `application-test.properties` para neutralizar servicios externos (Eureka, RabbitMQ) durante los tests, logrando una ejecución rápida y confiable.

### 🤖 Integración Continua (CI)
Se implementó un flujo de trabajo con **GitHub Actions** que automatiza la validación del código:
1. **Build Check:** Validación de compilación en entornos Linux.
2. **Test Automation:** Ejecución de los 52 tests de integración contra un entorno Dockerizado en la nube.
3. **Quality Gate:** El pipeline bloquea merges si algún test de infraestructura falla.

> Este enfoque garantiza que el microservicio de Reservas sea **"Production Ready"** en todo momento.


## 📬 Comunicación entre Microservicios

### 🔁 Comunicación Síncrona (OpenFeign)

El sistema utiliza **Spring Cloud OpenFeign** para comunicación síncrona entre microservicios,  
permitiendo llamadas HTTP directas entre servicios de manera declarativa.

#### Ejemplo:
`ReservationService` obtiene datos del `RoomService` a través de un cliente Feign:

```java
@FeignClient(name = "msvc-rooms")
public interface RoomClient {
    @GetMapping("/rooms/{id}")
    RoomDTO findRoomById(@PathVariable Long id);
}
```

**Ventajas:**
- ✅ Interfaz declarativa y fácil de implementar  
- 🔗 Integración nativa con Eureka para descubrimiento de servicios  
- ⚖️ Balanceo de carga automático  
- 📦 Manejo simplificado de requests/responses  

---

### 🐇 Comunicación Asíncrona (RabbitMQ)

Para operaciones que requieren **desacoplamiento y tolerancia a fallos**,  
el sistema implementa **RabbitMQ** como broker de mensajería.

#### Ejemplo de flujo de evento:
Cuando se elimina una habitación, `RoomService` emite un evento a RabbitMQ y  
`ReservationService` escucha el evento para eliminar las reservas asociadas automáticamente:

```java
@RabbitListener(queues = RabbitRoomConfig.QUEUE)
public void handleRoomDeleted(Long roomId) {
    repositoryReservation.deleteAllByRoomId(roomId);
}
```

**Beneficios de este enfoque:**
- 🚀 **Desacoplamiento:** Los servicios no dependen directamente entre sí  
- 🛡️ **Tolerancia a fallos:** Si `ReservationService` está caído, los mensajes se mantienen en la cola  
- ⚡ **Escalabilidad:** Múltiples consumidores pueden procesar mensajes en paralelo  
- 🔄 **Consistencia:** Garantiza la integridad de datos entre servicios  

---

## 🧩 Microservicios principales

| Microservicio | Descripción | Base de Datos |
|----------------|-------------|---------------|
| 🧭 **Eureka Server** | Registro y descubrimiento de servicios *(service registry)* | — |
| 🌐 **API Gateway** | Punto de entrada al sistema, balanceo de carga y seguridad global | — |
| 🏨 **Hotel Service** | CRUD de hoteles | MySQL |
| 🛏️ **Room Service** | Gestión de habitaciones y disponibilidad | PostgreSQL |
| 📅 **Reservation Service** | Creación, consulta y cancelación de reservas; escucha eventos de Room | MongoDB |
| 👤 **User / Auth Service** | Registro, login y manejo de roles *(admin / user)* | MySQL |
| 💳 **Payment Service** | Procesamiento y gestión de pagos de reservas | PostgreSQL |
| 📝 **Review Service** | Opiniones y calificaciones de usuarios | MongoDB |
---

## 🔐 Seguridad

El sistema utiliza **Spring Security + JWT (JSON Web Tokens)** para la autenticación y autorización.

**Características principales:**
- ✅ Registro e inicio de sesión de usuarios (`/auth/register`, `/auth/login`)  
- ✅ Generación y validación de tokens JWT  
- ✅ Roles definidos: `ROLE_ADMIN` y `ROLE_USER`  
- ✅ El **API Gateway** intercepta todas las peticiones y valida el token antes de enrutar  
- ✅ Los microservicios internos confían en el token propagado por el Gateway  

---

## 💻 Funcionalidades Principales

- ✅ Gestión de hoteles y habitaciones  
- ✅ Creación y cancelación de reservas  
- ✅ Sistema de usuarios con roles y autenticación JWT  
- ✅ Comunicación asíncrona con RabbitMQ  
- ✅ Integración con múltiples bases de datos  
- ✅ Balanceo dinámico y descubrimiento de servicios (Eureka)  
- ✅ API Gateway con validación centralizada  
- 🛠️ Dockerización completa en progreso  

---

## ⚙️ Estado Actual del Proyecto

- 🧩 Microservicios independientes con Eureka y Gateway  
- 🔁 Comunicación síncrona con Feign y asíncrona con RabbitMQ  
- 🔐 Seguridad JWT implementada  
- 💻 Frontend base (React + Tailwind + Context API)  
- 🐳 Dockerización final con Compose *(en progreso)*  
- 📄 Documentación Swagger / Postman *(en desarrollo)*  
- ☁️ Despliegue en VPS / Kubernetes *(planeado)*
-  🛠️ Refactorización en curso: Implementando cobertura de pruebas unitarias en la capa de servicios con **JUnit 5 y Mockito**.

---

## 🧩 Cómo Ejecutar el Proyecto (Local)

### 🔧 Prerequisitos
- ☕ **Java 17+**  
- 🛠️ **Maven 3.6+**  
- 🐇 **RabbitMQ**  
- 🗄️ **MySQL**, **PostgreSQL** y **MongoDB**  

---

### 🚀 Pasos de Ejecución

#### 1️⃣ Clonar el repositorio
```bash
git clone https://github.com/martinlopez05/hotel-reservation-system.git
cd hotel-reservation-system
```

#### 2️⃣ Iniciar RabbitMQ y las bases de datos necesarias  
*(MySQL, PostgreSQL, MongoDB)*

#### 3️⃣ Levantar Eureka Server
```bash
mvn spring-boot:run -pl eureka-server
```

#### 4️⃣ Levantar los microservicios
```bash
mvn spring-boot:run -pl msvc-hotels
mvn spring-boot:run -pl msvc-rooms
mvn spring-boot:run -pl msvc-reservations
mvn spring-boot:run -pl msvc-users
mvn spring-boot:run -pl msvc-payments
mvn spring-boot:run -pl msvc-reviews
```

#### 5️⃣ Acceder al dashboard de Eureka  
👉 [http://localhost:8761](http://localhost:8761)

#### 6️⃣ Acceder al sistema a través del Gateway  
👉 [http://localhost:8090](http://localhost:8090)

---

## 🐳 Dockerización y Despliegue

Todo el **backend** fue **dockerizado** para garantizar un entorno reproducible.  
Se utiliza **Docker Compose** para orquestar los servicios principales:

- 🧩 **Microservicios (Spring Boot)**
- 🐇 **RabbitMQ**
- 🗄️ **Bases de datos** (MySQL, PostgreSQL, MongoDB)
- 🌐 **Eureka Server y API Gateway**

```bash
docker-compose up -d --build
```
Esto levanta todo el ecosistema y gestiona las dependencias entre contenedores.

---

## 🌍 Despliegue del Frontend

El **Frontend (React)** fue desplegado en **Netlify**, comunicándose con el **API Gateway** mediante **HTTPS + JWT**.

---

## 🧭 Infraestructura en el VPS

El backend está desplegado en un **VPS** utilizando **Docker Compose** junto con **Nginx** como *reverse proxy*:

- 🔒 **Certificado SSL (HTTPS)**
- 🔁 **Redirección hacia el API Gateway**
- 🛡️ **Seguridad y rendimiento optimizado**

---

## 🚀 Resultado Final

- ✅ **Backend** desplegado en **VPS** con **Docker Compose**
- ✅ **Frontend público** en **Netlify**
- ✅ **Comunicación segura** mediante **HTTPS + JWT**
- ✅ **Integración completa** entre **RabbitMQ** y **FeignClient**
- ✅ **Balanceo y seguridad** gestionados con **Nginx**


## 👤 Autor

**Martín López**  
🎓 *Estudiante de Licenciatura en Sistemas de Información – Universidad Nacional de Luján*  

- 💻 [GitHub](https://github.com/martinlopez05)  
- 💼 [LinkedIn](https://www.linkedin.com/in/martin-lopez-8264132a8/)



