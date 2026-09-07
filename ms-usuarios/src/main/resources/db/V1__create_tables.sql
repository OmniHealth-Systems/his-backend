CREATE TABLE IF NOT EXISTS especialidad (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    codigo VARCHAR(50) UNIQUE NOT NULL
    );

CREATE TABLE IF NOT EXISTS turno (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     fecha DATE NOT NULL,
                                     lugar VARCHAR(100) NOT NULL,
    estado ENUM('DISPONIBLE', 'OCUPADO', 'CANCELADO') NOT NULL
    );

CREATE TABLE IF NOT EXISTS doctor (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      nombre_completo VARCHAR(150) NOT NULL,
    cmp VARCHAR(20) UNIQUE NOT NULL,
    id_especialidad BIGINT NOT NULL,
    id_turno BIGINT NOT NULL,
    FOREIGN KEY (id_especialidad) REFERENCES especialidad(id),
    FOREIGN KEY (id_turno) REFERENCES turno(id)
    );