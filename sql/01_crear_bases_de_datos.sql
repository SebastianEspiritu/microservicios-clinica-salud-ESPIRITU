-- =========================================================
-- Laboratorio 11 - Creación de Microservicios
-- Ejecutar en MySQL (phpMyAdmin / Workbench) antes de levantar los servicios.
-- (Las URLs JDBC también tienen createDatabaseIfNotExist=true)
-- Las TABLAS las crea JPA automáticamente (ddl-auto: update).
-- =========================================================

-- Parte guiada
CREATE DATABASE IF NOT EXISTS bd_categoria;
CREATE DATABASE IF NOT EXISTS bd_producto;
CREATE DATABASE IF NOT EXISTS bd_cliente;
CREATE DATABASE IF NOT EXISTS bd_pedido;

-- Perfiles dev
CREATE DATABASE IF NOT EXISTS bd_categoria_dev;
CREATE DATABASE IF NOT EXISTS bd_producto_dev;
CREATE DATABASE IF NOT EXISTS bd_cliente_dev;
CREATE DATABASE IF NOT EXISTS bd_pedido_dev;

-- Actividad: Clínica "Salud Integral" (una BD por microservicio)
CREATE DATABASE IF NOT EXISTS bd_paciente;   -- paciente
CREATE DATABASE IF NOT EXISTS bd_medico;     -- especialidad, medico, horario_medico
CREATE DATABASE IF NOT EXISTS bd_cita;       -- cita, atencion
