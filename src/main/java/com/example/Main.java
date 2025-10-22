package com.example;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();

        try {
            // CREAR
            User newUser = new User("Alice", "alice@example.com", 28);
            newUser = userDAO.createUser(newUser);
            System.out.println("Creado: " + newUser);

            // LEER por ID
            User fetched = userDAO.getUserById(newUser.getId());
            System.out.println("Obtenido por ID: " + fetched);

            // LEER todos
            List<User> users = userDAO.getAllUsers();
            System.out.println("Todos los usuarios: " + users);

            // ACTUALIZAR
            fetched.setAge(29);
            boolean updated = userDAO.updateUser(fetched);
            System.out.println("Actualizado: " + updated);
            System.out.println("Después de actualizar: " + userDAO.getUserById(fetched.getId()));

            // BUSCAR por nombre
            List<User> alices = userDAO.findUsersByName("Ali");
            System.out.println("Encontrado por nombre 'Ali': " + alices);

            // ELIMINAR
            boolean deleted = userDAO.deleteUser(fetched.getId());
            System.out.println("Eliminado: " + deleted);

        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }
}