-- Estados de mascota
INSERT INTO estado_mascota (id, descripcion)
SELECT 'DISPONIBLE', 'Mascota lista para adopción'
WHERE NOT EXISTS (SELECT 1 FROM estado_mascota WHERE id = 'DISPONIBLE');

INSERT INTO estado_mascota (id, descripcion)
SELECT 'NO_DISPONIBLE', 'Mascota no disponible temporalmente'
WHERE NOT EXISTS (SELECT 1 FROM estado_mascota WHERE id = 'NO_DISPONIBLE');

INSERT INTO estado_mascota (id, descripcion)
SELECT 'ADOPTADA', 'Mascota adoptada'
WHERE NOT EXISTS (SELECT 1 FROM estado_mascota WHERE id = 'ADOPTADA');

-- Especies
INSERT INTO especie (nombre, estado)
SELECT 'Perro', 1
WHERE NOT EXISTS (SELECT 1 FROM especie WHERE nombre = 'Perro' AND deleted_at IS NULL);

INSERT INTO especie (nombre, estado)
SELECT 'Gato', 1
WHERE NOT EXISTS (SELECT 1 FROM especie WHERE nombre = 'Gato' AND deleted_at IS NULL);

-- Categorías
INSERT INTO categoria (nombre, descripcion, estado)
SELECT 'Cachorro', 'Mascotas pequeñas, ideal para familias activas', 1
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nombre = 'Cachorro' AND deleted_at IS NULL);

INSERT INTO categoria (nombre, descripcion, estado)
SELECT 'Joven', 'Mascotas con energía moderada y fácil adaptación', 1
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nombre = 'Joven' AND deleted_at IS NULL);

INSERT INTO categoria (nombre, descripcion, estado)
SELECT 'Adulto', 'Mascotas calmadas, recomendadas para adopción responsable', 1
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nombre = 'Adulto' AND deleted_at IS NULL);

INSERT INTO categoria (nombre, descripcion, estado)
SELECT 'Especial', 'Mascotas con necesidades especiales o rescate prioritario', 1
WHERE NOT EXISTS (SELECT 1 FROM categoria WHERE nombre = 'Especial' AND deleted_at IS NULL);

-- Razas
INSERT INTO raza (especie_id, nombre, estado)
SELECT (SELECT id FROM especie WHERE nombre = 'Perro' AND deleted_at IS NULL LIMIT 1), 'Mestizo', 1
WHERE NOT EXISTS (SELECT 1 FROM raza WHERE nombre = 'Mestizo' AND deleted_at IS NULL);

INSERT INTO raza (especie_id, nombre, estado)
SELECT (SELECT id FROM especie WHERE nombre = 'Perro' AND deleted_at IS NULL LIMIT 1), 'Labrador', 1
WHERE NOT EXISTS (SELECT 1 FROM raza WHERE nombre = 'Labrador' AND deleted_at IS NULL);

INSERT INTO raza (especie_id, nombre, estado)
SELECT (SELECT id FROM especie WHERE nombre = 'Perro' AND deleted_at IS NULL LIMIT 1), 'Pastor Alemán', 1
WHERE NOT EXISTS (SELECT 1 FROM raza WHERE nombre = 'Pastor Alemán' AND deleted_at IS NULL);

INSERT INTO raza (especie_id, nombre, estado)
SELECT (SELECT id FROM especie WHERE nombre = 'Gato' AND deleted_at IS NULL LIMIT 1), 'Criollo', 1
WHERE NOT EXISTS (SELECT 1 FROM raza WHERE nombre = 'Criollo' AND deleted_at IS NULL);

INSERT INTO raza (especie_id, nombre, estado)
SELECT (SELECT id FROM especie WHERE nombre = 'Gato' AND deleted_at IS NULL LIMIT 1), 'Siamés', 1
WHERE NOT EXISTS (SELECT 1 FROM raza WHERE nombre = 'Siamés' AND deleted_at IS NULL);

INSERT INTO raza (especie_id, nombre, estado)
SELECT (SELECT id FROM especie WHERE nombre = 'Gato' AND deleted_at IS NULL LIMIT 1), 'Persa', 1
WHERE NOT EXISTS (SELECT 1 FROM raza WHERE nombre = 'Persa' AND deleted_at IS NULL);

INSERT INTO raza (especie_id, nombre, estado)
SELECT (SELECT id FROM especie WHERE nombre = 'Conejo' AND deleted_at IS NULL LIMIT 1), 'Cabeza de león', 1
WHERE NOT EXISTS (SELECT 1 FROM raza WHERE nombre = 'Cabeza de león' AND deleted_at IS NULL);

INSERT INTO raza (especie_id, nombre, estado)
SELECT (SELECT id FROM especie WHERE nombre = 'Conejo' AND deleted_at IS NULL LIMIT 1), 'Mini Rex', 1
WHERE NOT EXISTS (SELECT 1 FROM raza WHERE nombre = 'Mini Rex' AND deleted_at IS NULL);

-- Mascotas (mayoría adoptadas para simular flujo de adopción)
INSERT INTO mascota (nombre, raza_id, categoria_id, edad, sexo, descripcion, foto_url, estado_id)
SELECT 'Luna',
       (SELECT id FROM raza WHERE nombre = 'Criollo' AND deleted_at IS NULL LIMIT 1),
       (SELECT id FROM categoria WHERE nombre = 'Joven' AND deleted_at IS NULL LIMIT 1),
       2, 'H', 'Cariñosa y sociable, ideal para departamento.', '/images/mascotas/luna.jpg', 'ADOPTADA'
WHERE NOT EXISTS (SELECT 1 FROM mascota WHERE nombre = 'Luna' AND deleted_at IS NULL);

INSERT INTO mascota (nombre, raza_id, categoria_id, edad, sexo, descripcion, foto_url, estado_id)
SELECT 'Rocky',
       (SELECT id FROM raza WHERE nombre = 'Labrador' AND deleted_at IS NULL LIMIT 1),
       (SELECT id FROM categoria WHERE nombre = 'Adulto' AND deleted_at IS NULL LIMIT 1),
       4, 'M', 'Muy noble y obediente, disfruta paseos largos.', '/images/mascotas/rocky.jpg', 'ADOPTADA'
WHERE NOT EXISTS (SELECT 1 FROM mascota WHERE nombre = 'Rocky' AND deleted_at IS NULL);

INSERT INTO mascota (nombre, raza_id, categoria_id, edad, sexo, descripcion, foto_url, estado_id)
SELECT 'Mía',
       (SELECT id FROM raza WHERE nombre = 'Persa' AND deleted_at IS NULL LIMIT 1),
       (SELECT id FROM categoria WHERE nombre = 'Adulto' AND deleted_at IS NULL LIMIT 1),
       3, 'H', 'Tranquila, perfecta para hogares silenciosos.', '/images/mascotas/mia.jpg', 'ADOPTADA'
WHERE NOT EXISTS (SELECT 1 FROM mascota WHERE nombre = 'Mía' AND deleted_at IS NULL);

INSERT INTO mascota (nombre, raza_id, categoria_id, edad, sexo, descripcion, foto_url, estado_id)
SELECT 'Bruno',
       (SELECT id FROM raza WHERE nombre = 'Mestizo' AND deleted_at IS NULL LIMIT 1),
       (SELECT id FROM categoria WHERE nombre = 'Joven' AND deleted_at IS NULL LIMIT 1),
       2, 'M', 'Juguetón y protector, convivió con niños.', '/images/mascotas/bruno.jpg', 'ADOPTADA'
WHERE NOT EXISTS (SELECT 1 FROM mascota WHERE nombre = 'Bruno' AND deleted_at IS NULL);

INSERT INTO mascota (nombre, raza_id, categoria_id, edad, sexo, descripcion, foto_url, estado_id)
SELECT 'Leo',
       (SELECT id FROM raza WHERE nombre = 'Pastor Alemán' AND deleted_at IS NULL LIMIT 1),
       (SELECT id FROM categoria WHERE nombre = 'Adulto' AND deleted_at IS NULL LIMIT 1),
       5, 'M', 'Entrenado básico, requiere espacio amplio.', '/images/mascotas/leo.jpg', 'ADOPTADA'
WHERE NOT EXISTS (SELECT 1 FROM mascota WHERE nombre = 'Leo' AND deleted_at IS NULL);

INSERT INTO mascota (nombre, raza_id, categoria_id, edad, sexo, descripcion, foto_url, estado_id)
SELECT 'Daisy',
       (SELECT id FROM raza WHERE nombre = 'Siamés' AND deleted_at IS NULL LIMIT 1),
       (SELECT id FROM categoria WHERE nombre = 'Joven' AND deleted_at IS NULL LIMIT 1),
       1, 'H', 'Activa y curiosa, lista para acompañarte.', '/images/mascotas/daisy.jpg', 'ADOPTADA'
WHERE NOT EXISTS (SELECT 1 FROM mascota WHERE nombre = 'Daisy' AND deleted_at IS NULL);

INSERT INTO mascota (nombre, raza_id, categoria_id, edad, sexo, descripcion, foto_url, estado_id)
SELECT 'Kira',
       (SELECT id FROM raza WHERE nombre = 'Mestizo' AND deleted_at IS NULL LIMIT 1),
       (SELECT id FROM categoria WHERE nombre = 'Cachorro' AND deleted_at IS NULL LIMIT 1),
       1, 'H', 'Rescatada recientemente, muy afectuosa.', '/images/mascotas/kira.jpg', 'DISPONIBLE'
WHERE NOT EXISTS (SELECT 1 FROM mascota WHERE nombre = 'Kira' AND deleted_at IS NULL);

INSERT INTO mascota (nombre, raza_id, categoria_id, edad, sexo, descripcion, foto_url, estado_id)
SELECT 'Toby',
       (SELECT id FROM raza WHERE nombre = 'Labrador' AND deleted_at IS NULL LIMIT 1),
       (SELECT id FROM categoria WHERE nombre = 'Joven' AND deleted_at IS NULL LIMIT 1),
       2, 'M', 'Sano y vacunado, busca familia definitiva.', '/images/mascotas/toby.jpg', 'DISPONIBLE'
WHERE NOT EXISTS (SELECT 1 FROM mascota WHERE nombre = 'Toby' AND deleted_at IS NULL);

INSERT INTO mascota (nombre, raza_id, categoria_id, edad, sexo, descripcion, foto_url, estado_id)
SELECT 'Nube',
       (SELECT id FROM raza WHERE nombre = 'Mini Rex' AND deleted_at IS NULL LIMIT 1),
       (SELECT id FROM categoria WHERE nombre = 'Especial' AND deleted_at IS NULL LIMIT 1),
       2, 'H', 'Conejita dócil, requiere ambiente tranquilo.', '/images/mascotas/mascota-default.jpg', 'NO_DISPONIBLE'
WHERE NOT EXISTS (SELECT 1 FROM mascota WHERE nombre = 'Nube' AND deleted_at IS NULL);

INSERT INTO mascota (nombre, raza_id, categoria_id, edad, sexo, descripcion, foto_url, estado_id)
SELECT 'Copito',
       (SELECT id FROM raza WHERE nombre = 'Cabeza de león' AND deleted_at IS NULL LIMIT 1),
       (SELECT id FROM categoria WHERE nombre = 'Especial' AND deleted_at IS NULL LIMIT 1),
       1, 'M', 'En observación veterinaria, pronto disponible.', '/images/mascotas/mascota-default.jpg', 'NO_DISPONIBLE'
WHERE NOT EXISTS (SELECT 1 FROM mascota WHERE nombre = 'Copito' AND deleted_at IS NULL);
