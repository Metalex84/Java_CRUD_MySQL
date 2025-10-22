-- Create users table (no se crea si ya existe)
CREATE TABLE IF NOT EXISTS users (
id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    age INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data (optional)
INSERT INTO users (name, email, age) VALUES 
    ('John Doe', 'john.doe@example.com', 30),
    ('Jane Smith', 'jane.smith@example.com', 25),
    ('Bob Johnson', 'bob.johnson@example.com', 35);