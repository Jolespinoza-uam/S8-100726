--Creación base de datos
CREATE DATABASE bdventas;

-- Crear esquema
CREATE SCHEMA catalogos;

-- Crear tabla categorias
CREATE TABLE catalogos.categorias
(
	id_categoria SERIAL PRIMARY KEY,
	nombre VARCHAR(80) NOT NULL,
	descripcion VARCHAR(200) NOT NULL,
	estado SMALLINT NOT NULL DEFAULT 1

	CONSTRAINT chk_categoria_estado
		CHECK (estado IN (0,1))
);

INSERT INTO catalogos.categorias(nombre, descripcion)
VALUES ('Cereales 2', 'Categoria adiciona');

-- Crear tabla marcas
CREATE TABLE catalogos.marcas
(
	id_marca SERIAL PRIMARY KEY,
	nombre VARCHAR(80) NOT NULL UNIQUE,
	pais_origen VARCHAR(80),
	estado SMALLINT NOT NULL DEFAULT 1

	CONSTRAINT chk_marca_estado
		CHECK(estado IN (0,1))
);

-- Prueba de Inserts en tabla marcas
-- Estado
INSERT INTO catalogos.marcas(nombre, pais_origen, estado)
VALUES ('Lenovo', 'Estados Unidos', 3);

-- NOT NULL
INSERT INTO catalogos.marcas(nombre, pais_origen, estado)
VALUES(NULL, 'Japón', 1);

-- UNIQUE
INSERT INTO catalogos.marcas(nombre, pais_origen, estado)
VALUES ('Lenovo', 'Estados Unidos', 1);

INSERT INTO catalogos.marcas(nombre, pais_origen, estado)
VALUES ('Lenovo', 'SegundoPais', 1);

-- Crear tabla productos
CREATE TABLE catalogos.productos
(
	id_producto SERIAL NOT NULL PRIMARY KEY,
	codigo VARCHAR(20) NOT NULL UNIQUE,
	nombre VARCHAR(100) NOT NULL,
	descripcion VARCHAR(200),
	precio NUMERIC(10, 2) NOT NULL,
	stock INTEGER NOT NULL DEFAULT 0,
	fecha_creacion DATE NOT NULL DEFAULT CURRENT_DATE,
	id_categoria INTEGER NOT NULL,
	id_marca INTEGER NOT NULL,
	estado SMALLINT NOT NULL DEFAULT 1,

	CONSTRAINT fk_producto_categoria
		FOREIGN KEY (id_categoria)
		REFERENCES catalogos.categorias(id_categoria),

	CONSTRAINT fK_producto_marca
		FOREIGN KEY (id_marca)
		REFERENCES catalogos.marcas(id_marca),

	CONSTRAINT chk_producto_precio
		CHECK(precio > 0),

	CONSTRAINT chk_producto_stock
		CHECK(stock > 0),

	CONSTRAINT chk_producto_estado
		CHECK(estado IN (0,1))
);

-- Crear cinco registros de marcas
INSERT INTO catalogos.marcas(nombre, pais_origen, estado)
VALUES ('Samsung', 'Corea', 1);
INSERT INTO catalogos.marcas(nombre, pais_origen, estado)
VALUES ('Toyota', 'Japón', 1);
INSERT INTO catalogos.marcas(nombre, pais_origen, estado)
VALUES ('Hyundai', 'Corea', 0);
INSERT INTO catalogos.marcas(nombre, pais_origen, estado)
VALUES ('HP', 'Estados Unidos', 0);
INSERT INTO catalogos.marcas(nombre, pais_origen, estado)
VALUES ('Apple', 'Estados Unidos', 1);

-- Crear cinco registros de categorias

INSERT INTO catalogos.categorias(nombre, descripcion, estado)
VALUES ('Autos', '', 1);
INSERT INTO catalogos.categorias(nombre, descripcion, estado)
VALUES ('Computadoras', 'Dispositivos electronicos computacionales', 1);

INSERT INTO catalogos.categorias(nombre, descripcion, estado)
VALUES ('Celulares', '', 1);

INSERT INTO catalogos.categorias(nombre, descripcion, estado)
VALUES ('Comida', 'Productos consumibles', 0);

INSERT INTO catalogos.categorias(nombre, descripcion, estado)
VALUES ('Ropa', 'Prendas de vestir', 0);

-- Crear diez registros de productos
INSERT INTO catalogos.productos(codigo, nombre, descripcion, precio, stock, fecha_creacion, id_categoria, id_marca, estado)
VALUES 
('PROD-001', 'Laptop ThinkPad T14', 'Computadora portátil ideal para trabajo', 1250.00, 15, CURRENT_DATE, 3, 1, 1),
('PROD-002', 'Samsung Galaxy S23', 'Smartphone de alta gama con 256GB', 950.00, 30, CURRENT_DATE, 4, 2, 1),
('PROD-003', 'Toyota Corolla 2024', 'Sedán familiar muy eficiente', 22500.00, 5, CURRENT_DATE, 2, 3, 1),
('PROD-004', 'Hyundai Tucson 2023', 'SUV compacto color gris', 26000.00, 3, CURRENT_DATE, 2, 4, 1),
('PROD-005', 'HP Envy x360', 'Laptop 2 en 1 con pantalla táctil', 1100.50, 10, CURRENT_DATE, 3, 5, 1),
('PROD-006', 'iPhone 15 Pro', 'Teléfono de Apple con chasis de titanio', 1200.00, 45, CURRENT_DATE, 4, 6, 1),
('PROD-007', 'MacBook Air M2', 'Laptop ligera de 13 pulgadas', 1350.00, 20, CURRENT_DATE, 3, 6, 1),
('PROD-008', 'Monitor Samsung 27"', 'Monitor 4K para diseño gráfico', 350.00, 40, CURRENT_DATE, 3, 2, 1),
('PROD-009', 'Toyota Yaris', 'Auto compacto ideal para la ciudad', 18000.00, 8, CURRENT_DATE, 2, 3, 1),
('PROD-010', 'iPad Pro', 'Tablet para ilustradores y diseñadores', 999.99, 12, CURRENT_DATE, 3, 6, 1);
