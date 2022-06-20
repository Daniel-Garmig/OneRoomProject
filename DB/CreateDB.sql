CREATE DATABASE IF NOT EXISTS OneRoom
        CHARACTER SET utf8;


USE OneRoom;


BEGIN;
CREATE TABLE IF NOT EXISTS usuarios (
        nickname varchar(20) NOT NULL,
        passwd varchar(40) DEFAULT NULL,
        PRIMARY KEY (nickname),
        UNIQUE KEY (nickname)
) COMMENT='Tabla para almacenar usuarios
            - Nombre del usuario CLAVE PRIMARIA
            - Contraseña
            Relaciones:
                                USUARIOS <--- 1:N ---> PARTIDAS';

CREATE TABLE IF NOT EXISTS partidas (
        id int NOT NULL AUTO_INCREMENT,
        nickname_usuario varchar(20) NOT NULL,
        puntuacion bigint NOT NULL,
        dinero bigint NOT NULL,
        PRIMARY KEY (id),
        UNIQUE KEY (id,nickname_usuario),
        FOREIGN KEY (nickname_usuario) REFERENCES usuarios (nickname) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT='Tabla para almacenar las partidas
            - id CLAVE PRIMARIA autoincremental
            - usuario CLAVE FORANEA (si se actualiza el nickname se actualiza la referencia) (si se borra el usuario se borra la partida)
            - dinero bigint para poder alcanzar altos valores
            - puntuacion bigint
            Relaciones:
                                PARTIDAS <--- 1:N ---> USUARIOS
                                PARTIDAS <--- 1:N ---> SALAS';


CREATE TABLE IF NOT EXISTS salas (
        nombre varchar(20) NOT NULL,
        id_partida int(11) NOT NULL,
        sala_data JSON,
        PRIMARY KEY (nombre,id_partida),
        FOREIGN KEY (id_partida) REFERENCES partidas (id) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT='Tabla para almacenar usuarios
                        - tipo CLAVE PRIMARIA
                        - partida CLAVE PRIMARIA Y FORANEA (si se actualiza el id de la partida se actualiza la referencia) (si se borra la partida se borra las salas asociadas)
            - datos de la sala JSON con la informacion de las maquinas colocadas y los recursos
            Relaciones:
                                SALAS <--- N:1 ---> PARTIDAS';



-- Funcion para devolver si un usuario esta validado para no tener acesso a las contraseñas
DELIMITER :
CREATE FUNCTION IF NOT EXISTS validar (nick varchar(20), pass varchar(40)) RETURNS BOOLEAN READS SQL DATA
BEGIN
        declare dbPass varchar(40) default "";
        set dbPass = (SELECT passwd from usuarios where nickname=nick);
        if STRCMP(pass, dbPass) = 0
        THEN
                return true;
        END IF;
        return false;
END:
DELIMITER ;

-- Funcion para ver si el nombre de usuario esta disponible
DELIMITER :
CREATE FUNCTION IF NOT EXISTS comprobarNickDisponible (nick varchar(20)) RETURNS BOOLEAN READS SQL DATA
BEGIN
        DECLARE existe varchar(20);
        SET existe = (SELECT nick FROM usuarios WHERE nickname=nick);
        if existe IS NULL
        THEN
                return true;
        END IF;
        return false;
END:
DELIMITER ;

/*
-- Usuario para administracion propia 
CREATE USER IF NOT EXISTS "{placeHolderUsuario}"@"%" IDENTIFIED BY "{placeHolderContraseña}";
GRANT ALL ON OneRoom.* TO '{placeHolderUsuario}'@'%' WITH GRANT OPTION;
*/

-- Usuario de la aplicacion
CREATE USER IF NOT EXISTS "UserApp"@"%" IDENTIFIED BY "LD0neR00m";
GRANT SELECT,INSERT,UPDATE ON OneRoom.partidas TO 'UserApp'@'%';
GRANT SELECT,INSERT,UPDATE ON OneRoom.salas TO 'UserApp'@'%';
GRANT INSERT ON OneRoom.usuarios TO 'UserApp'@'%';
GRANT EXECUTE ON FUNCTION OneRoom.validar TO 'UserApp'@'%';
GRANT EXECUTE ON FUNCTION OneRoom.comprobarNickDisponible TO 'UserApp'@'%';

COMMIT;
