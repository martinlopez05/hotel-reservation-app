# 🏨 Hotel Reservation System 

Aplicación web completa para la **gestión de reservas de hoteles**, desarrollada con **arquitectura de microservicios**.  
El sistema permite administrar hoteles, habitaciones, usuarios, reservas y pagos, con roles (**admin / usuario**), autenticación **JWT** y comunicación entre servicios tanto **síncrona (REST/Feign)** como **asíncrona (RabbitMQ)**.

La aplicación está completamente desplegada en producción y disponible en el siguiente enlace:  

👉 [**https://hotelfly.me**](https://hotelfly.me) 🌐

---


## 🧱 Arquitectura General

<p align="center">
  <img src="./Arquitectura%20de%20la%20Aplicación.jpg" alt="Arquitectura del Sistema" width="800">
</p>

> Diagrama general del sistema, mostrando la comunicación entre los microservicios, el API Gateway, las bases de datos y RabbitMQ.


---

## 📄 Documentación de la API (Swagger)

Toda la API del sistema se encuentra completamente documentada y expuesta de forma pública a través de **Swagger UI** de manera centralizada bajo conexiones seguras:

* 🌐 **Interfaz Gráfica de Swagger:** [https://hotelflyapi.codes/swagger-ui/index.html](https://hotelflyapi.codes/swagger-ui/index.html)
* 📊 **Especificación OpenAPI (JSON):** [https://hotelflyapi.codes/v3/api-docs](https://hotelflyapi.codes/v3/api-docs)

*(A través del menú desplegable de Swagger se puede explorar e interactuar de forma interactiva con los endpoints de cada microservicio: Auth, Hotels, Rooms, Reservations, etc.)*

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

---

### 🎨 Frontend
- ⚛️ **React + TypeScript**
- 🎨 **TailwindCSS**
- ⚡ **Vite**
- 📦 **Axios** → consumo de endpoints del backend  
- 🔄 **React Router DOM** y **Context API** → manejo de sesión y rutas protegidas  

---

### 🐳 Contenedores e Infraestructura (Cloud DevOps)
- 🐋 **Docker** → Contenedorización individual por componente  
- ⚙️ **Docker Compose** → Orquestación del ecosistema completo (Servicios, Gateways, Colas y BDs)  
- ☁️ **DigitalOcean Droplet (VPS)** → Servidor de producción en la nube  
- 🔀 **Nginx** → Proxy inverso para la gestión eficiente del tráfico y enmascaramiento  
- 🔒 **Certbot (Let's Encrypt)** → Emisión y renovación automatizada de certificados SSL (HTTPS)

---

## 🧠 Arquitectura del sistema

El proyecto sigue una **arquitectura de microservicios distribuida**, donde cada servicio es independiente y escalable.  
Todos los servicios se registran en **Eureka Server** y se comunican entre sí mediante el **API Gateway**.

---

## 💎 Microservicio Estrella: msvc-reservations

El microservicio de **Reservations** fue diseñado como el estándar de calidad y robustez del proyecto, implementando una arquitectura tolerante a fallos y una suite de pruebas profesional:

### 🛡️ Tolerancia a Fallos y Alta Disponibilidad (Resilience4j)
Para evitar fallos en cascada dentro del ecosistema distribuido, se aisló la comunicación con los clientes Feign mediante una capa de **Servicios de Integración** decorados con patrones de resiliencia:
* **Circuit Breaker:** Configurado con una ventana deslizante de 20 llamadas y un umbral de error del 50%. Si un servicio externo cae, el circuito se abre durante 60 segundos protegiendo la integridad del sistema.
* **Retry Pattern:** Ante fallos efímeros de red, implementa hasta 3 reintentos automáticos con un mecanismo de retroceso exponencial (`exponentialBackoffMultiplier=2`) para mitigar la sobrecarga.
* **Orden de Aspectos:** Se configuró un orden estricto de precedencia (`Circuit Breaker` -> `Retry`) para optimizar el ciclo de vida de los requests concurrentes.

### 🔬 Estrategia de Testing Automatizado
* **Tests Unitarios:** Cobertura total de la lógica de negocio en la capa de servicios mediante `Mockito`, simulando con precisión los escenarios de éxito y el lanzamiento de excepciones controladas de negocio (`HotelNotFoundException`, etc.).
* **Tests de Integración Reales:** Utiliza **Testcontainers** para levantar una instancia real de **MongoDB en un contenedor Docker** durante la fase de pruebas, garantizando que el comportamiento de persistencia sea idéntico al de producción.
* **Aislamiento de Perfiles:** Implementación de `application-test.properties` para neutralizar el tráfico de red de Eureka, RabbitMQ y los aspectos de infraestructura durante los tests, logrando ejecuciones deterministas y ultrarrápidas.

### 🤖 Integración Continua (CI)
Se estructuró un flujo de trabajo con **GitHub Actions** que compila y ejecuta de manera automatizada los **50 tests del microservicio** en un entorno Linux aislado en cada Push o Pull Request, garantizando que la rama principal permanezca siempre *Production Ready*.

---

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
| 🏨 **Hotel Service** | ABM de hoteles | MySQL |
| 🛏️ **Room Service** | Gestión de habitaciones y disponibilidad | PostgreSQL |
| 📅 **Reservation Service** | Creación, consulta y cancelación de reservas; escucha eventos de Room | MongoDB |
| 👤 **User / Auth Service** | Registro, login y manejo de roles *(admin / user)* | MySQL |
| 💳 **Payment Service** | Procesamiento y gestión de pagos de reservas | PostgreSQL |
| 📝 **Review Service** | Opiniones y calificaciones de usuarios | MongoDB |
---

## 🔐 Seguridad integrada

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
- ✅ Ecosistema completamente contenedorizado con Docker y Docker Compose

---

## ⚙️ Estado Actual del Proyecto

- 🧩 Microservicios independientes con Eureka y Gateway  
- 🔁 Comunicación síncrona con Feign y asíncrona con RabbitMQ  
- 🔐 Seguridad JWT implementada  
- 💻 Frontend base (React + Tailwind + Context API)  
- 🐳 Dockerización final con Compose *(en progreso)*  
- 📄 Documentación Swagger / Postman *(en desarrollo)*  
- ☁️ Despliegue Cloud (Producción): Frontend productivo en Netlify y Backend orquestado en DigitalOcean VPS con HTTPS nativo.

---

## 🧩 Cómo Ejecutar el Proyecto (Local)

### 🔧 Prerequisitos
- ☕ **Java 17+**  
- 🛠️ **Maven 3.6+**  
- 🐳 Docker & Docker Compose (Recomendado para ahorrar configuraciones manuales de bases de datos y colas)

---

### 🚀 Ejecución rápida con Docker
-Si contás con Docker instalado, podés levantar el ecosistema completo (Bases de datos, RabbitMQ, Infraestructura de Spring y Microservicios) con un solo comando en la raíz del proyecto:

```bash
docker-compose up -d --build
```
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



