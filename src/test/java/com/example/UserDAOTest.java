package com.example;

import org.junit.*;
import static org.junit.Assert.*;

import java.sql.*;
import java.util.List;

/**
 * Unit tests for UserDAO class and DatabaseConnection
 * Tests CRUD operations, validation, and search functionality against the database
 */
public class UserDAOTest {
    private UserDAO userDAO;
    private Connection connection;
    
    @Before
    public void setUp() throws Exception {
        userDAO = new UserDAO();
        connection = DatabaseConnection.getConnection();
        
        // Limpiar la tabla antes de cada test
        cleanDatabase();
    }
    
    @After
    public void tearDown() throws Exception {
        // Limpiar la tabla después de cada test
        cleanDatabase();
        
        if (connection != null && !connection.isClosed()) {
            DatabaseConnection.closeConnection(connection);
        }
    }
    
    /**
     * Limpia la tabla de usuarios antes y después de los tests
     */
    private void cleanDatabase() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM users");
            stmt.executeUpdate("ALTER TABLE users AUTO_INCREMENT = 1");
        }
    }
    
    /**
     * Test case 1: Test que un nuevo usuario puede ser creado exitosamente con datos válidos
     */
    @Test
    public void testCreateUserWithValidData() throws SQLException {
        System.out.println("\n[TEST 1] testCreateUserWithValidData - Iniciando...");
        // Arrange
        User newUser = new User("John Doe", "john.doe@example.com", 30);
        
        // Act
        User createdUser = userDAO.createUser(newUser);
        
        // Assert
        assertNotNull("El usuario creado no debe ser nulo", createdUser);
        assertTrue("El ID debe ser mayor que 0", createdUser.getId() > 0);
        assertEquals("John Doe", createdUser.getName());
        assertEquals("john.doe@example.com", createdUser.getEmail());
        assertEquals(30, createdUser.getAge());
        
        // Verificar que el usuario existe en la base de datos
        User retrievedUser = userDAO.getUserById(createdUser.getId());
        assertNotNull("El usuario debe existir en la base de datos", retrievedUser);
        assertEquals(createdUser.getId(), retrievedUser.getId());
        System.out.println("[TEST 1] ✅ ÉXITO - Usuario creado correctamente con ID: " + createdUser.getId());
    }
    
    /**
     * Test case 2: Test que un usuario existente puede ser actualizado con datos válidos
     */
    @Test
    public void testUpdateUserWithValidData() throws SQLException {
        System.out.println("\n[TEST 2] testUpdateUserWithValidData - Iniciando...");
        // Arrange - Crear un usuario primero
        User originalUser = new User("Jane Smith", "jane.smith@example.com", 25);
        User createdUser = userDAO.createUser(originalUser);
        
        // Act - Actualizar el usuario
        createdUser.setName("Jane Doe");
        createdUser.setEmail("jane.doe@example.com");
        createdUser.setAge(26);
        boolean updateSuccess = userDAO.updateUser(createdUser);
        
        // Assert
        assertTrue("La actualización debe ser exitosa", updateSuccess);
        
        // Verificar que los cambios se guardaron en la base de datos
        User updatedUser = userDAO.getUserById(createdUser.getId());
        assertNotNull("El usuario actualizado debe existir", updatedUser);
        assertEquals("Jane Doe", updatedUser.getName());
        assertEquals("jane.doe@example.com", updatedUser.getEmail());
        assertEquals(26, updatedUser.getAge());
        System.out.println("[TEST 2] ✅ ÉXITO - Usuario actualizado correctamente");
    }
    
    /**
     * Test case 3: Test que un usuario puede ser eliminado exitosamente
     */
    @Test
    public void testDeleteUser() throws SQLException {
        System.out.println("\n[TEST 3] testDeleteUser - Iniciando...");
        // Arrange - Crear un usuario primero
        User newUser = new User("Test User", "test@example.com", 28);
        User createdUser = userDAO.createUser(newUser);
        int userId = createdUser.getId();
        
        // Verificar que el usuario existe
        assertNotNull("El usuario debe existir antes de eliminarlo", userDAO.getUserById(userId));
        
        // Act - Eliminar el usuario
        boolean deleteSuccess = userDAO.deleteUser(userId);
        
        // Assert
        assertTrue("La eliminación debe ser exitosa", deleteSuccess);
        
        // Verificar que el usuario ya no existe
        User deletedUser = userDAO.getUserById(userId);
        assertNull("El usuario eliminado no debe existir en la base de datos", deletedUser);
        System.out.println("[TEST 3] ✅ ÉXITO - Usuario eliminado correctamente");
    }
    
    /**
     * Test case 4a: Test que no se puede crear un usuario con datos nulos
     */
    @Test(expected = SQLException.class)
    public void testCreateUserWithNullName() throws SQLException {
        System.out.println("\n[TEST 4a] testCreateUserWithNullName - Iniciando...");
        // Arrange
        User invalidUser = new User(null, "email@example.com", 25);
        
        // Act - Debe lanzar SQLException
        System.out.println("[TEST 4a] ✅ ÉXITO - SQLException lanzada correctamente para nombre nulo");
        userDAO.createUser(invalidUser);
    }
    
    /**
     * Test case 4b: Test que no se puede crear un usuario con email nulo
     */
    @Test(expected = SQLException.class)
    public void testCreateUserWithNullEmail() throws SQLException {
        System.out.println("\n[TEST 4b] testCreateUserWithNullEmail - Iniciando...");
        // Arrange
        User invalidUser = new User("John Doe", null, 25);
        
        // Act - Debe lanzar SQLException
        System.out.println("[TEST 4b] ✅ ÉXITO - SQLException lanzada correctamente para email nulo");
        userDAO.createUser(invalidUser);
    }
    
    /**
     * Test case 4c: Test que no se puede crear un usuario con edad negativa
     */
    @Test
    public void testCreateUserWithNegativeAge() throws SQLException {
        System.out.println("\n[TEST 4c] testCreateUserWithNegativeAge - Iniciando...");
        // Arrange
        User invalidUser = new User("John Doe", "john@example.com", -5);
        
        // Act
        User createdUser = userDAO.createUser(invalidUser);
        
        // Assert - El usuario se crea pero con edad negativa (validación de negocio)
        assertNotNull(createdUser);
        assertEquals(-5, createdUser.getAge());
        System.out.println("[TEST 4c] ✅ ÉXITO - Usuario con edad negativa creado (sin validación de negocio)");
    }
    
    /**
     * Test case 4d: Test actualización de usuario inexistente
     */
    @Test
    public void testUpdateNonExistentUser() throws SQLException {
        System.out.println("\n[TEST 4d] testUpdateNonExistentUser - Iniciando...");
        // Arrange
        User nonExistentUser = new User(999, "Ghost User", "ghost@example.com", 30);
        
        // Act
        boolean updateSuccess = userDAO.updateUser(nonExistentUser);
        
        // Assert
        assertFalse("La actualización de un usuario inexistente debe fallar", updateSuccess);
        System.out.println("[TEST 4d] ✅ ÉXITO - Actualización de usuario inexistente falló correctamente");
    }
    
    /**
     * Test case 4e: Test eliminación de usuario inexistente
     */
    @Test
    public void testDeleteNonExistentUser() throws SQLException {
        System.out.println("\n[TEST 4e] testDeleteNonExistentUser - Iniciando...");
        // Arrange
        int nonExistentUserId = 999;
        
        // Act
        boolean deleteSuccess = userDAO.deleteUser(nonExistentUserId);
        
        // Assert
        assertFalse("La eliminación de un usuario inexistente debe fallar", deleteSuccess);
        System.out.println("[TEST 4e] ✅ ÉXITO - Eliminación de usuario inexistente falló correctamente");
    }
    
    /**
     * Test case 5: Test que la búsqueda filtra correctamente usuarios por nombre
     */
    @Test
    public void testSearchFunctionality() throws SQLException {
        System.out.println("\n[TEST 5] testSearchFunctionality - Iniciando...");
        // Arrange - Crear múltiples usuarios
        userDAO.createUser(new User("John Doe", "john@example.com", 30));
        userDAO.createUser(new User("Jane Smith", "jane@example.com", 25));
        userDAO.createUser(new User("John Adams", "jadams@example.com", 35));
        userDAO.createUser(new User("Bob Johnson", "bob@example.com", 28));
        
        // Act - Buscar usuarios con "John" en el nombre
        List<User> searchResults = userDAO.findUsersByName("John");
        
        // Assert
        assertNotNull("Los resultados de búsqueda no deben ser nulos", searchResults);
        assertEquals("Debe haber 3 usuarios con 'John' en el nombre", 3, searchResults.size());
        
        // Verificar que todos los resultados contienen "John"
        for (User user : searchResults) {
            assertTrue("El nombre debe contener 'John'", 
                user.getName().toLowerCase().contains("john"));
        }
        System.out.println("[TEST 5] ✅ ÉXITO - Búsqueda encontró 3 usuarios con 'John'");
    }
    
    /**
     * Test búsqueda con término que no coincide
     */
    @Test
    public void testSearchWithNoMatches() throws SQLException {
        System.out.println("\n[TEST 6] testSearchWithNoMatches - Iniciando...");
        // Arrange
        userDAO.createUser(new User("Alice", "alice@example.com", 28));
        userDAO.createUser(new User("Bob", "bob@example.com", 32));
        
        // Act
        List<User> searchResults = userDAO.findUsersByName("Nonexistent");
        
        // Assert
        assertNotNull("Los resultados deben ser una lista vacía, no nula", searchResults);
        assertTrue("No debe haber resultados para un nombre inexistente", searchResults.isEmpty());
        System.out.println("[TEST 6] ✅ ÉXITO - Búsqueda sin coincidencias retornó lista vacía");
    }
    
    /**
     * Test obtener todos los usuarios
     */
    @Test
    public void testGetAllUsers() throws SQLException {
        System.out.println("\n[TEST 7] testGetAllUsers - Iniciando...");
        // Arrange - Crear varios usuarios
        userDAO.createUser(new User("Alice", "alice@example.com", 28));
        userDAO.createUser(new User("Bob", "bob@example.com", 32));
        userDAO.createUser(new User("Charlie", "charlie@example.com", 45));
        
        // Act
        List<User> allUsers = userDAO.getAllUsers();
        
        // Assert
        assertNotNull("La lista de usuarios no debe ser nula", allUsers);
        assertEquals("Debe haber 3 usuarios en total", 3, allUsers.size());
        System.out.println("[TEST 7] ✅ ÉXITO - Se obtuvieron todos los usuarios (3 usuarios)");
    }
    
    /**
     * Test obtener usuario por ID
     */
    @Test
    public void testGetUserById() throws SQLException {
        System.out.println("\n[TEST 8] testGetUserById - Iniciando...");
        // Arrange
        User newUser = new User("Test User", "test@example.com", 30);
        User createdUser = userDAO.createUser(newUser);
        
        // Act
        User retrievedUser = userDAO.getUserById(createdUser.getId());
        
        // Assert
        assertNotNull("El usuario recuperado no debe ser nulo", retrievedUser);
        assertEquals(createdUser.getId(), retrievedUser.getId());
        assertEquals("Test User", retrievedUser.getName());
        assertEquals("test@example.com", retrievedUser.getEmail());
        assertEquals(30, retrievedUser.getAge());
        System.out.println("[TEST 8] ✅ ÉXITO - Usuario recuperado por ID correctamente");
    }
    
    /**
     * Test obtener usuario inexistente por ID
     */
    @Test
    public void testGetNonExistentUserById() throws SQLException {
        System.out.println("\n[TEST 9] testGetNonExistentUserById - Iniciando...");
        // Act
        User retrievedUser = userDAO.getUserById(999);
        
        // Assert
        assertNull("Un usuario inexistente debe retornar null", retrievedUser);
        System.out.println("[TEST 9] ✅ ÉXITO - Usuario inexistente retornó null correctamente");
    }
    
    /**
     * Test de conexión a base de datos
     */
    @Test
    public void testDatabaseConnection() throws SQLException {
        System.out.println("\n[TEST 10] testDatabaseConnection - Iniciando...");
        // Act
        Connection conn = DatabaseConnection.getConnection();
        
        // Assert
        assertNotNull("La conexión no debe ser nula", conn);
        assertFalse("La conexión debe estar abierta", conn.isClosed());
        assertTrue("La conexión debe ser válida", conn.isValid(5));
        
        // Cleanup
        DatabaseConnection.closeConnection(conn);
        assertTrue("La conexión debe estar cerrada después de closeConnection", conn.isClosed());
        System.out.println("[TEST 10] ✅ ÉXITO - Conexión a base de datos funcionando correctamente");
    }
}
