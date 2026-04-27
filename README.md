# BarbApp Backend (API REST)
[Español](#español) | [English](#english)
---
<a name="español"></a>
## Español
### Descripción
Motor central de la aplicación BarbApp, desarrollado con Java 21 y Spring Boot 3. Gestiona la lógica de negocio transaccional, el motor de disponibilidad algorítmica para las reservas y la seguridad basada en roles (RBAC) mediante tokens JWT descentralizados.
### Requisitos previos
* Java 21 (JDK)
* Maven
* PostgreSQL (Instancia Local o mediante plataforma Cloud como Supabase)
### Instalación y Despliegue
1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/databaandsc/BarbApp-Backend.git
   cd BarbApp-Backend
Despliegue de la Base de Datos: Localizar el archivo backup_barbapp.sql incluido en este repositorio. Ejecutar este script en tu instancia de PostgreSQL para generar la estructura del modelo relacional (esquemas, tablas y ENUMs).

Variables de Entorno: Configurar el archivo application.yml (o application.properties) o el entorno del sistema con las credenciales de la base de datos (URL, usuario, contraseña) y la configuración de JWT para Supabase.

Ejecutar el servidor:

bash
mvn spring-boot:run

English
Description
The core engine of the BarbApp application, developed with Java 21 and Spring Boot 3. It manages transactional business logic, the algorithmic availability engine for bookings, and Role-Based Access Control (RBAC) security using decentralized JWT tokens.

Prerequisites
Java 21 (JDK)
Maven
PostgreSQL (Local instance or Cloud platform like Supabase)
Installation and Deployment
Clone the repository:

bash
git clone https://github.com/databaandsc/BarbApp-Backend.git
cd BarbApp-Backend
Database Deployment: Locate the backup_barbapp.sql file included in this repository. Run this script in your PostgreSQL instance to generate the relational model structure (schemas, tables, and ENUMs).

Environment Variables: Configure the application.yml (or application.properties) file or your system environment with the database credentials (URL, user, password) and the JWT configuration for Supabase.

Run the server:

bash
mvn spring-boot:run
