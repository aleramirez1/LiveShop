CREATE DATABASE IF NOT EXISTS liveshop;
USE liveshop;

CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_vendedor INT,
    nombre VARCHAR(150) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    FOREIGN KEY (id_vendedor) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_comprador INT,
    id_producto INT,
    cantidad INT NOT NULL,
    entregado BOOLEAN,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_comprador) REFERENCES usuarios(id),
    FOREIGN KEY (id_producto) REFERENCES productos(id)
);

INSERT INTO usuarios (nombre, email, password) VALUES
('Juan Pérez', 'juan@example.com', '$2a$14$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KFm'),
('María García', 'maria@example.com', '$2a$14$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KFm'),
('Carlos López', 'carlos@example.com', '$2a$14$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KFm'),
('Ana Martínez', 'ana@example.com', '$2a$14$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KFm');

INSERT INTO productos (id_vendedor, nombre, precio, stock) VALUES
(1, 'Laptop Dell XPS', 1299.99, 5),
(1, 'Mouse Logitech', 49.99, 20),
(2, 'Teclado Mecánico', 149.99, 10),
(2, 'Monitor LG 27"', 399.99, 3),
(3, 'Auriculares Sony', 299.99, 8),
(3, 'Webcam Logitech', 79.99, 15),
(4, 'SSD Samsung 1TB', 129.99, 12),
(4, 'RAM DDR4 16GB', 89.99, 25);
