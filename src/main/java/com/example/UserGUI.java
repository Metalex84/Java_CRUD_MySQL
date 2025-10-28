package com.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz gráfica para gestionar operaciones CRUD de usuarios
 */
public class UserGUI extends JFrame {
    private UserDAO userDAO;
    
    // Componentes de interfaz
    private JTextField txtId, txtName, txtEmail, txtAge, txtSearch;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnCreate, btnUpdate, btnDelete, btnClear, btnSearch, btnRefresh;
    
    public UserGUI() {
        userDAO = new UserDAO();
        initComponents();
        loadAllUsers();
    }
    
    private void initComponents() {
        setTitle("Gestión de Usuarios - CRUD");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Panel superior - Formulario
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.NORTH);
        
        // Panel central - Tabla
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Panel inferior - Búsqueda
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Usuario"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // ID
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        txtId = new JTextField(15);
        txtId.setEditable(false);
        panel.add(txtId, gbc);
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtName = new JTextField(15);
        panel.add(txtName, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(15);
        panel.add(txtEmail, gbc);
        
        // Edad
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Edad:"), gbc);
        gbc.gridx = 1;
        txtAge = new JTextField(15);
        panel.add(txtAge, gbc);
        
        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnCreate = new JButton("Crear");
        btnUpdate = new JButton("Actualizar");
        btnDelete = new JButton("Eliminar");
        btnClear = new JButton("Limpiar");
        
        btnCreate.addActionListener(e -> createUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnClear.addActionListener(e -> clearForm());
        
        buttonPanel.add(btnCreate);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Usuarios"));
        
        // Crear tabla
        String[] columns = {"ID", "Nombre", "Email", "Edad"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedUser();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Búsqueda"));
        
        panel.add(new JLabel("Buscar por nombre:"));
        txtSearch = new JTextField(20);
        panel.add(txtSearch);
        
        btnSearch = new JButton("Buscar");
        btnRefresh = new JButton("Mostrar Todos");
        
        btnSearch.addActionListener(e -> searchUsers());
        btnRefresh.addActionListener(e -> loadAllUsers());
        
        panel.add(btnSearch);
        panel.add(btnRefresh);
        
        return panel;
    }
    
    private void createUser() {
        try {
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            String ageStr = txtAge.getText().trim();
            
            if (name.isEmpty() || email.isEmpty() || ageStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor complete todos los campos", 
                    "Error de Validación", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int age = Integer.parseInt(ageStr);
            User user = new User(name, email, age);
            user = userDAO.createUser(user);
            
            JOptionPane.showMessageDialog(this, 
                "Usuario creado exitosamente con ID: " + user.getId(), 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
            
            clearForm();
            loadAllUsers();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "La edad debe ser un número válido", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al crear usuario: " + e.getMessage(), 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateUser() {
        try {
            String idStr = txtId.getText().trim();
            if (idStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Seleccione un usuario de la tabla para actualizar", 
                    "Error de Validación", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            String ageStr = txtAge.getText().trim();
            
            if (name.isEmpty() || email.isEmpty() || ageStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor complete todos los campos", 
                    "Error de Validación", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int id = Integer.parseInt(idStr);
            int age = Integer.parseInt(ageStr);
            User user = new User(id, name, email, age);
            
            boolean success = userDAO.updateUser(user);
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Usuario actualizado exitosamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadAllUsers();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No se encontró el usuario con ID: " + id, 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "ID y edad deben ser números válidos", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al actualizar usuario: " + e.getMessage(), 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteUser() {
        try {
            String idStr = txtId.getText().trim();
            if (idStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Seleccione un usuario de la tabla para eliminar", 
                    "Error de Validación", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int id = Integer.parseInt(idStr);
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de eliminar el usuario con ID: " + id + "?", 
                "Confirmar Eliminación", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = userDAO.deleteUser(id);
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Usuario eliminado exitosamente", 
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadAllUsers();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "No se encontró el usuario con ID: " + id, 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "ID debe ser un número válido", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al eliminar usuario: " + e.getMessage(), 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadAllUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            refreshTable(users);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar usuarios: " + e.getMessage(), 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchUsers() {
        String searchTerm = txtSearch.getText().trim();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Ingrese un término de búsqueda", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            List<User> users = userDAO.findUsersByName(searchTerm);
            refreshTable(users);
            
            if (users.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No se encontraron usuarios con ese nombre", 
                    "Información", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al buscar usuarios: " + e.getMessage(), 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshTable(List<User> users) {
        tableModel.setRowCount(0);
        for (User user : users) {
            Object[] row = {
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge()
            };
            tableModel.addRow(row);
        }
    }
    
    private void loadSelectedUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtName.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtEmail.setText(tableModel.getValueAt(selectedRow, 2).toString());
            txtAge.setText(tableModel.getValueAt(selectedRow, 3).toString());
        }
    }
    
    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtEmail.setText("");
        txtAge.setText("");
        txtSearch.setText("");
        table.clearSelection();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserGUI gui = new UserGUI();
            gui.setVisible(true);
        });
    }
}
