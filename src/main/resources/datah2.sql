-- Crear tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(100),
    correo_electronico VARCHAR(150)
);

-- Crear tabla de categorías de producto
CREATE TABLE IF NOT EXISTS categoria_producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100)
);

-- Crear tabla de productos
CREATE TABLE IF NOT EXISTS producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    marca VARCHAR(255),
    descripcion VARCHAR(500),
    categoria_id BIGINT,
    tamano_porcion_gramos DOUBLE,
    ruta_imagen VARCHAR(255),
    ruta_miniatura VARCHAR(255),
    url_imagen VARCHAR(255),
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    costo_por_unidad DOUBLE,
    FOREIGN KEY (categoria_id) REFERENCES categoria_producto(id)
);

-- Crear tabla de nutrimentos de productos
CREATE TABLE IF NOT EXISTS nutrimentos_producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT,
    calorias DOUBLE,
    proteina DOUBLE,
    carbohidratos DOUBLE,
    azucares DOUBLE,
    grasa DOUBLE,
    grasa_saturada DOUBLE,
    fibra DOUBLE,
    sodio DOUBLE,
    sal DOUBLE,
    FOREIGN KEY (producto_id) REFERENCES producto(id)
);

-- Crear tabla de recetas
CREATE TABLE IF NOT EXISTS receta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255),
    descripcion VARCHAR(500),
    peso_total_gramos DOUBLE,
    usuario_id BIGINT,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Crear tabla de ingredientes de receta
CREATE TABLE IF NOT EXISTS ingrediente_receta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receta_id BIGINT,
    producto_id BIGINT,
    cantidad_gramos INT,
    orden_visualizacion INT,
    FOREIGN KEY (receta_id) REFERENCES receta(id),
    FOREIGN KEY (producto_id) REFERENCES producto(id)
);

-- Insertar usuarios de prueba
INSERT INTO usuarios (nombre_usuario, correo_electronico) VALUES ('usuario_prueba', 'prueba@ejemplo.com');

-- Insertar categorías
INSERT INTO categoria_producto (nombre) VALUES ('Lácteos'), ('Verduras'), ('Carne');

-- Insertar productos de prueba
INSERT INTO producto (nombre, marca, descripcion, categoria_id, tamano_porcion_gramos, costo_por_unidad) VALUES
('Leche', 'MarcaA', 'Leche entera 1L', 1, 200, 1.5),
('Zanahoria', 'MarcaB', 'Zanahoria fresca', 2, 100, 0.8),
('Pechuga de Pollo', 'MarcaC', 'Pechuga sin piel', 3, 150, 5.0);

-- Insertar nutrimentos
INSERT INTO nutrimentos_producto (producto_id, calorias, proteina, carbohidratos, azucares, grasa, grasa_saturada, fibra, sodio)
VALUES
(1, 64, 3.2, 4.8, 4.8, 3.6, 2.3, 0, 50),
(2, 41, 0.9, 9.6, 4.7, 0.2, 0.1, 2.8, 69),
(3, 165, 31, 0, 0, 3.6, 1, 0, 74);

-- Insertar receta de prueba
INSERT INTO receta (nombre, descripcion, peso_total_gramos, usuario_id) VALUES
('Ensalada de Pollo', 'Ensalada saludable de pollo', 400, 1);

-- Insertar ingredientes de receta
INSERT INTO ingrediente_receta (receta_id, producto_id, cantidad_gramos, orden_visualizacion) VALUES
(1, 1, 200, 1),
(1, 2, 100, 2),
(1, 3, 100, 3);