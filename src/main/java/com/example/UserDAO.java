package com.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Objeto de Acceso a Datos (DAO) para la entidad Usuario
 * Implementa operaciones CRUD usando JDBC
 */
public class UserDAO {
    
    /**
     * Crea un nuevo usuario en la base de datos
     * @param user el usuario a crear
     * @return el usuario creado con ID generado
     * @throws SQLException si la operación de base de datos falla
     */
    public User createUser(User user) throws SQLException {
        // Validate required fields
        if (user.getName() == null) {
            throw new SQLException("User name cannot be null");
        }
        if (user.getEmail() == null) {
            throw new SQLException("User email cannot be null");
        }
        
        String sql = "INSERT INTO users (name, email, age) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setInt(3, user.getAge());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Obtener el ID generado
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
            }
            
            return user;
        }
    }
    
    /**
     * Lee un usuario por ID
     * @param id el ID del usuario
     * @return el usuario si se encuentra, null en caso contrario
     * @throws SQLException si la operación de base de datos falla
     */
    public User getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("age")
                    );
                }
            }
        }
        
        return null;
    }
    
    /**
     * Lee todos los usuarios de la base de datos
     * @return lista de todos los usuarios
     * @throws SQLException si la operación de base de datos falla
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getInt("age")
                ));
            }
        }
        
        return users;
    }
    
    /**
     * Actualiza un usuario existente
     * @param user el usuario con información actualizada
     * @return true si la actualización fue exitosa, false en caso contrario
     * @throws SQLException si la operación de base de datos falla
     */
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, age = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setInt(3, user.getAge());
            pstmt.setInt(4, user.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Elimina un usuario por ID
     * @param id el ID del usuario a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     * @throws SQLException si la operación de base de datos falla
     */
    public boolean deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Encuentra usuarios por nombre (coincidencia parcial)
     * @param name el nombre a buscar
     * @return lista de usuarios que coinciden con el nombre
     * @throws SQLException si la operación de base de datos falla
     */
    public List<User> findUsersByName(String name) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE name LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + name + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("age")
                    ));
                }
            }
        }
        
        return users;
    }
}