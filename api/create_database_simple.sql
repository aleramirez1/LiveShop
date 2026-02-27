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
