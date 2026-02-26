-- Insertar usuarios de prueba
INSERT INTO usuarios (email, nombre, password, telefono) VALUES
('juan@example.com', 'Juan Pérez', 'password123', '1234567890'),
('maria@example.com', 'María García', 'password123', '0987654321'),
('carlos@example.com', 'Carlos López', 'password123', '5555555555'),
('ana@example.com', 'Ana Martínez', 'password123', '6666666666');

-- Insertar productos de prueba
INSERT INTO productos (id_vendedor, nombre, descripcion, precio, stock, categoria, imagen_url, disponible) VALUES
(1, 'Laptop Dell XPS', 'Laptop de alta gama con procesador Intel i7, 16GB RAM, 512GB SSD', 1299.99, 5, 'Electrónica', 'https://via.placeholder.com/300?text=Laptop', TRUE),
(1, 'Mouse Logitech', 'Mouse inalámbrico de precisión, batería de larga duración', 49.99, 20, 'Accesorios', 'https://via.placeholder.com/300?text=Mouse', TRUE),
(2, 'Teclado Mecánico', 'Teclado mecánico RGB, switches Cherry MX', 149.99, 10, 'Accesorios', 'https://via.placeholder.com/300?text=Teclado', TRUE),
(2, 'Monitor LG 27"', 'Monitor 4K, 60Hz, IPS panel', 399.99, 3, 'Electrónica', 'https://via.placeholder.com/300?text=Monitor', TRUE),
(3, 'Auriculares Sony', 'Auriculares inalámbricos con cancelación de ruido', 299.99, 8, 'Audio', 'https://via.placeholder.com/300?text=Auriculares', TRUE),
(3, 'Webcam Logitech', 'Webcam 1080p, micrófono integrado', 79.99, 15, 'Accesorios', 'https://via.placeholder.com/300?text=Webcam', TRUE),
(4, 'SSD Samsung 1TB', 'SSD NVMe M.2, velocidad de lectura 3500MB/s', 129.99, 12, 'Almacenamiento', 'https://via.placeholder.com/300?text=SSD', TRUE),
(4, 'RAM DDR4 16GB', 'Memoria RAM DDR4 16GB 3200MHz', 89.99, 25, 'Memoria', 'https://via.placeholder.com/300?text=RAM', TRUE);
