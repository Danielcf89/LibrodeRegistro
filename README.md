LibrodeRegistro es una aplicación web completa para la gestión financiera personal que permite a los usuarios registrar ingresos y gastos, crear metas de ahorro inteligentes y generar reportes financieros detallados. Desarrollada con Spring Boot y Spring Security para garantizar máxima seguridad.

🌟 Características Implementadas
🔐 Autenticación Segura
Registro de nuevos usuarios

Inicio de sesión con JWT

Gestión de sesiones seguras

Autorización basada en roles

💰 Gestión de Movimientos Financieros
Registro de ingresos y gastos

Categorización de transacciones

Historial completo de movimientos

Búsqueda y filtrado avanzado

🎯 Sistema de Alcancías Inteligentes
Creación de metas de ahorro personalizadas

Seguimiento de progreso en tiempo real

Alertas para metas próximas a vencer

Gestión de múltiples alcancías

📊 Reportes Financieros
Cálculo automático de saldo actual

Resumen por categorías

Visualización de tendencias financieras

Histórico mensual/anual

🚀 Tecnologías Utilizadas
Capa	Tecnologías
Backend	Java 17, Spring Boot 3, Spring MVC, Spring Data JPA
Seguridad	Spring Security, JWT (JSON Web Tokens)
Base de Datos	MySQL 8.0
Frontend	Thymeleaf, Bootstrap, HTML5, CSS3
Herramientas	Maven, Docker, Git
📂 Estructura del Proyecto

LibrodeRegistro/
├── src/
│   ├── main/
│   │   ├── java/com/example/LibrodeRegistro/
│   │   │   ├── config/           # Configuraciones del sistema
│   │   │   ├── controllers/      # Controladores REST
│   │   │   ├── entity/           # Entidades JPA
│   │   │   ├── repository/       # Repositorios de datos
│   │   │   ├── security/         # Configuración de seguridad
│   │   │   ├── service/          # Lógica de negocio
│   │   │   └── LibrodeRegistroApplication.java
│   │   └── resources/
│   │       ├── static/           # Recursos estáticos (CSS, JS)
│   │       ├── templates/        # Plantillas Thymeleaf
│   │       └── application.properties
├── .mvn/
├── lib/
├── docker-compose.yml            # Configuración de MySQL en Docker
└── pom.xml

⚙️ Configuración y Ejecución
Requisitos Previos
Java 17+

MySQL 8.0+

Maven 3.8+

Autenticación
Método	Endpoint	Descripción
POST	/api/auth/registro	Registrar nuevo usuario
POST	/api/auth/login	Iniciar sesión (obtener JWT)
Movimientos
Método	Endpoint	Descripción
GET	/api/movimientos	Obtener movimientos del usuario
POST	/api/movimientos	Crear nuevo movimiento
Alcancías
Método	Endpoint	Descripción
GET	/api/alcancia	Listar alcancías del usuario
POST	/api/alcancia	Crear nueva alcancía
POST	/api/alcancia/{id}/add	Agregar fondos a alcancía
Reportes
Método	Endpoint	Descripción
GET	/api/reportes/saldo	Obtener saldo actual
GET	/api/reportes/resumen	Generar resumen financiero
