CREATE TABLE IF NOT EXISTS direccion (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         departamento VARCHAR(100) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    provincia VARCHAR(100) NOT NULL
    );

CREATE TABLE IF NOT EXISTS seguro_medico (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             nombre VARCHAR(100) NOT NULL,
    tipo_seguro VARCHAR(50) NOT NULL,
    descripcion TEXT,
    cobertura VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS paciente (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    edad INT,
    email VARCHAR(100) UNIQUE NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    sexo ENUM('FEMENINO', 'MASCULINO') NOT NULL,
    estado_civil ENUM('SOLTERO', 'CASADO', 'DIVORCIADO', 'VIUDO', 'CONCUBINATO') NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    nacionalidad VARCHAR(50) NOT NULL,
    id_direccion BIGINT NOT NULL,
    tipo_documento VARCHAR(50) NOT NULL,
    dni VARCHAR(20) UNIQUE NOT NULL,
    contacto_emergencia VARCHAR(100) NOT NULL,
    id_seguro_medico BIGINT NOT NULL,
    FOREIGN KEY (id_direccion) REFERENCES direccion(id),
    FOREIGN KEY (id_seguro_medico) REFERENCES seguro_medico(id)
    );