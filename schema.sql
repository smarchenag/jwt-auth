CREATE TABLE usuarios (id_usuario SERIAL PRIMARY KEY, name_usuario VARCHAR(100) NOT NULL, email VARCHAR(100) NOT NULL, password_usuario VARCHAR(255) NOT NULL);

CREATE TABLE rol (id_rol SERIAL PRIMARY KEY, name_rol VARCHAR(50) NOT NULL, descripcion VARCHAR(100));

CREATE TABLE usuarios_roles (id_usuario INT NOT NULL, id_rol INT NOT NULL, CONSTRAINT fk_id_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario),
CONSTRAINT fk_id_rol FOREIGN KEY (id_rol) REFERENCES rol (id_rol));