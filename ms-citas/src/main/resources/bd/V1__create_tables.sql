CREATE TABLE IF NOT EXISTS tipo_cita (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         nombre VARCHAR(100) NOT NULL,
    duracion_default INT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
    );

CREATE TABLE IF NOT EXISTS cita (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    paciente_id BIGINT NOT NULL,
                                    doctor_id BIGINT NOT NULL,
                                    sede_id BIGINT NOT NULL,
                                    fecha_cita DATE NOT NULL,
                                    hora_cita TIME NOT NULL,
                                    duracion_minutos INT NOT NULL,
                                    tipo_cita_id BIGINT NOT NULL,
                                    estado ENUM('PROGRAMADA', 'CONFIRMADA', 'COMPLETADA', 'CANCELADA') NOT NULL,
    motivo VARCHAR(255),
    observaciones TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tipo_cita_id) REFERENCES tipo_cita(id)
    );

CREATE TABLE IF NOT EXISTS excepcion_horario (
                                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                 doctor_id BIGINT NOT NULL,
                                                 franquicia_id BIGINT NOT NULL,
                                                 fecha DATE NOT NULL,
                                                 motivo VARCHAR(255) NOT NULL,
    todo_el_dia BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );