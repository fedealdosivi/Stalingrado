-- =====================================================
-- Stalingrado Battle Simulator - Database Schema
-- Updated: 2025
-- Description: Complete database schema for battle simulation
-- =====================================================

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS batallas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE batallas;

-- =====================================================
-- Table: ejercitos (Armies)
-- Stores information about participating armies
-- =====================================================
CREATE TABLE IF NOT EXISTS ejercitos (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    bonificacion_ataque DECIMAL(5,2) NOT NULL DEFAULT 1.00,
    bonificacion_defensa DECIMAL(5,2) NOT NULL DEFAULT 1.00,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table: tipos_unidad (Unit Types)
-- Reference table for soldier unit types
-- =====================================================
CREATE TABLE IF NOT EXISTS tipos_unidad (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    tipo_arma VARCHAR(50),
    poder_ataque INT NOT NULL DEFAULT 0,
    tipo_defensa VARCHAR(50),
    poder_defensa INT NOT NULL DEFAULT 0,
    descripcion VARCHAR(200)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table: batallas (Battles)
-- Stores battle metadata
-- =====================================================
CREATE TABLE IF NOT EXISTS batallas (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    ejercito_ganador_id INT,
    ejercito_perdedor_id INT,
    duracion_segundos INT,
    total_soldados_iniciales INT NOT NULL,
    soldados_sobrevivientes INT NOT NULL DEFAULT 0,
    fecha_batalla TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ejercito_ganador_id) REFERENCES ejercitos(id) ON DELETE SET NULL,
    FOREIGN KEY (ejercito_perdedor_id) REFERENCES ejercitos(id) ON DELETE SET NULL,
    INDEX idx_fecha (fecha_batalla),
    INDEX idx_ganador (ejercito_ganador_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table: informes (Battle Reports)
-- Stores detailed battle outcome reports
-- =====================================================
CREATE TABLE IF NOT EXISTS informes (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    batalla_id INT,
    resultado TEXT NOT NULL,
    detalles_combate TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (batalla_id) REFERENCES batallas(id) ON DELETE CASCADE,
    INDEX idx_batalla (batalla_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table: soldados_batalla (Battle Soldiers)
-- Tracks individual soldiers in each battle
-- =====================================================
CREATE TABLE IF NOT EXISTS soldados_batalla (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    batalla_id INT NOT NULL,
    ejercito_id INT NOT NULL,
    tipo_unidad_id INT NOT NULL,
    sobrevivio BOOLEAN DEFAULT FALSE,
    bajas_causadas INT DEFAULT 0,
    FOREIGN KEY (batalla_id) REFERENCES batallas(id) ON DELETE CASCADE,
    FOREIGN KEY (ejercito_id) REFERENCES ejercitos(id) ON DELETE CASCADE,
    FOREIGN KEY (tipo_unidad_id) REFERENCES tipos_unidad(id) ON DELETE CASCADE,
    INDEX idx_batalla_ejercito (batalla_id, ejercito_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Insert default armies
-- =====================================================
INSERT INTO ejercitos (nombre, bonificacion_ataque, bonificacion_defensa)
VALUES
    ('USSR', 0.95, 1.95),
    ('AXIS', 2.95, 1.00)
ON DUPLICATE KEY UPDATE nombre=nombre;

-- =====================================================
-- Insert default unit types
-- =====================================================
INSERT INTO tipos_unidad (nombre, tipo_arma, poder_ataque, tipo_defensa, poder_defensa, descripcion)
VALUES
    ('Infanteria_Fusil_Chaleco', 'Fusil', 15, 'Chaleco', 30, 'Soldado de infanteria basico con fusil y chaleco antibalas'),
    ('Infanteria_Fusil_Trinchera', 'Fusil', 15, 'Trinchera', 50, 'Soldado de infanteria atrincherado'),
    ('Operador_Tanque_Chaleco', 'Tanque', 40, 'Chaleco', 30, 'Operador de tanque con proteccion basica'),
    ('Piloto_Avion_Correr', 'AvionCombate', 50, 'Correr', 10, 'Piloto de avion de combate'),
    ('Artillero_Canon_Trinchera', 'Canon', 25, 'Trinchera', 50, 'Artillero de canon atrincherado'),
    ('Cobarde_Fusil_Correr', 'Fusil', 15, 'Correr', 10, 'Soldado cobarde que huye facilmente')
ON DUPLICATE KEY UPDATE nombre=nombre;

-- =====================================================
-- Views for reporting
-- =====================================================

-- View: Resumen de batallas
CREATE OR REPLACE VIEW vista_resumen_batallas AS
SELECT
    b.id,
    eg.nombre AS ganador,
    ep.nombre AS perdedor,
    b.duracion_segundos,
    b.total_soldados_iniciales,
    b.soldados_sobrevivientes,
    b.fecha_batalla,
    i.resultado
FROM batallas b
LEFT JOIN ejercitos eg ON b.ejercito_ganador_id = eg.id
LEFT JOIN ejercitos ep ON b.ejercito_perdedor_id = ep.id
LEFT JOIN informes i ON i.batalla_id = b.id
ORDER BY b.fecha_batalla DESC;

-- View: Estadisticas por ejercito
CREATE OR REPLACE VIEW vista_estadisticas_ejercitos AS
SELECT
    e.nombre AS ejercito,
    COUNT(DISTINCT CASE WHEN b.ejercito_ganador_id = e.id THEN b.id END) AS victorias,
    COUNT(DISTINCT CASE WHEN b.ejercito_perdedor_id = e.id THEN b.id END) AS derrotas,
    COUNT(DISTINCT b.id) AS total_batallas,
    ROUND(COUNT(DISTINCT CASE WHEN b.ejercito_ganador_id = e.id THEN b.id END) * 100.0 /
          NULLIF(COUNT(DISTINCT b.id), 0), 2) AS porcentaje_victoria
FROM ejercitos e
LEFT JOIN batallas b ON e.id = b.ejercito_ganador_id OR e.id = b.ejercito_perdedor_id
GROUP BY e.id, e.nombre;
