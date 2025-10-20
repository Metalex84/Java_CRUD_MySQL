# Aplicación JDBC CRUD

Un proyecto Maven que demuestra operaciones CRUD con JDBC y base de datos MySQL.

## Prerrequisitos

1. **Java 11+** - Asegúrate de que Java esté instalado
2. **Maven** - Para construir el proyecto
3. **Servidor MySQL** - Ejecutándose localmente en el puerto 3306

## Configuración

### 1. Configuración de MySQL

Inicia tu servidor MySQL y crea la base de datos:

```sql
mysql -u root -p < schema.sql
```

O ejecuta manualmente los comandos SQL:

```sql
CREATE DATABASE IF NOT EXISTS testdb;
USE testdb;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    age INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 2. Configurar Conexión a Base de Datos

Actualiza las credenciales de la base de datos en `src/main/java/com/example/DatabaseConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/testdb";
private static final String USERNAME = "root";
private static final String PASSWORD = "tu_contraseña";
```

### 3. Construir y Ejecutar

```bash
# Construir el proyecto
mvn clean compile

# Ejecutar la aplicación
mvn exec:java -Dexec.mainClass="com.example.Main"
```

## Estructura del Proyecto

```
src/
├── main/
│   └── java/
│       └── com/
│           └── example/
│               ├── Main.java              # Clase principal con demo CRUD
│               ├── User.java              # Clase modelo de usuario
│               ├── UserDAO.java           # Operaciones CRUD
│               └── DatabaseConnection.java # Utilidad de conexión a BD
└── test/
    └── java/
schema.sql                                 # Esquema de base de datos
pom.xml                                   # Configuración de Maven
```

## Operaciones CRUD

La aplicación demuestra:

- **CREAR**: Insertar nuevos usuarios
- **LEER**: Obtener usuario por ID y obtener todos los usuarios
- **ACTUALIZAR**: Modificar datos de usuario existentes
- **ELIMINAR**: Remover usuarios de la base de datos
- **BUSCAR**: Encontrar usuarios por nombre (coincidencia parcial)

## Resumen de Clases

- `User`: Clase modelo que representa la entidad usuario
- `DatabaseConnection`: Utilidad para gestionar conexiones a la base de datos
- `UserDAO`: Objeto de Acceso a Datos con operaciones CRUD usando PreparedStatements
- `Main`: Demuestra el uso de todas las operaciones CRUD

## Características de Seguridad

- Usa PreparedStatements para prevenir inyección SQL
- Gestión adecuada de recursos con try-with-resources
- Listo para pool de conexiones (se puede extender)
