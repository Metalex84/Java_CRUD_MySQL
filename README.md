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

#### Interfaz Gráfica (por defecto)

```bash
# Construir el proyecto
mvn clean compile

# Ejecutar la interfaz gráfica
mvn exec:java
```

#### Versión de línea de comandos

```bash
# Ejecutar el Main original con ejemplos CRUD
mvn exec:java -Dexec.mainClass="com.example.Main"
```

## Estructura del Proyecto

```
src/
├── main/
│   └── java/
│       └── com/
│           └── example/
│               ├── UserGUI.java           # Interfaz gráfica CRUD (principal)
│               ├── Main.java              # Demo CRUD por consola
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
- `UserGUI`: Interfaz gráfica Swing para gestionar usuarios con operaciones CRUD
- `Main`: Demo por consola de las operaciones CRUD

## Uso de la Interfaz Gráfica

La aplicación UserGUI proporciona una interfaz gráfica intuitiva con:

- **Formulario**: Campos para ID, Nombre, Email y Edad
- **Tabla**: Visualización de todos los usuarios registrados
- **Botones de acción**:
  - **Crear**: Agregar nuevo usuario (completa nombre, email y edad)
  - **Actualizar**: Modificar usuario seleccionado de la tabla
  - **Eliminar**: Borrar usuario seleccionado (con confirmación)
  - **Limpiar**: Vaciar todos los campos del formulario
- **Búsqueda**: Filtrar usuarios por nombre
- **Mostrar Todos**: Recargar la lista completa de usuarios

### Flujo de trabajo:

1. **Crear usuario**: Completa los campos (sin ID) y haz clic en "Crear"
2. **Editar usuario**: Haz clic en una fila de la tabla, modifica los datos y presiona "Actualizar"
3. **Eliminar usuario**: Selecciona una fila y haz clic en "Eliminar"
4. **Buscar usuario**: Ingresa un nombre en el campo de búsqueda y presiona "Buscar"

## Características de Seguridad

- Usa PreparedStatements para prevenir inyección SQL
- Gestión adecuada de recursos con try-with-resources
- Listo para pool de conexiones (se puede extender)

## Pruebas Unitarias

El proyecto incluye pruebas unitarias completas en `src/test/java/com/example/UserDAOTest.java`.

### Ejecutar las pruebas

```bash
# Ejecutar todas las pruebas
mvn test

# Ejecutar una prueba específica
mvn test -Dtest=UserDAOTest#testCreateUserWithValidData
```

### Casos de Prueba

#### Operaciones CRUD Básicas
- **Test 1**: `testCreateUserWithValidData` - Verifica la creación exitosa de usuarios con datos válidos
- **Test 2**: `testUpdateUserWithValidData` - Verifica la actualización de usuarios existentes
- **Test 3**: `testDeleteUser` - Verifica la eliminación correcta de usuarios
- **Test 8**: `testGetUserById` - Verifica la recuperación de usuarios por ID
- **Test 7**: `testGetAllUsers` - Verifica la obtención de todos los usuarios

#### Validación y Manejo de Errores
- **Test 4a**: `testCreateUserWithNullName` - Verifica que `createUser` lanza `SQLException` cuando el nombre es null
- **Test 4b**: `testCreateUserWithNullEmail` - Verifica que `createUser` lanza `SQLException` cuando el email es null
- **Test 4c**: `testCreateUserWithNegativeAge` - Verifica el comportamiento con edad negativa
- **Test 4d**: `testUpdateNonExistentUser` - Verifica el fallo al actualizar un usuario inexistente
- **Test 4e**: `testDeleteNonExistentUser` - Verifica el fallo al eliminar un usuario inexistente
- **Test 9**: `testGetNonExistentUserById` - Verifica que retorna null para usuarios inexistentes

#### Funcionalidad de Búsqueda
- **Test 5**: `testSearchFunctionality` - Verifica la búsqueda por nombre con coincidencias parciales
- **Test 6**: `testSearchWithNoMatches` - Verifica el comportamiento cuando no hay coincidencias

#### Conexión a Base de Datos
- **Test 10**: `testDatabaseConnection` - Verifica la conexión y desconexión de la base de datos

### Notas sobre las Pruebas

- Todas las pruebas limpian la base de datos antes y después de ejecutarse para garantizar aislamiento
- Las pruebas utilizan la anotación `@Test(expected = SQLException.class)` para verificar excepciones
- Cada prueba incluye mensajes de consola para seguimiento del progreso
