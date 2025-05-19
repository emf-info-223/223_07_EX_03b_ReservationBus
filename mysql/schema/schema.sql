CREATE DATABASE IF NOT EXISTS `bus_company` DEFAULT CHARACTER SET = utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `bus_company`;

DROP TABLE IF EXISTS `t_type_vehicule`;

CREATE TABLE `t_type_vehicule` (
    `pk_type_vehicule` int NOT NULL AUTO_INCREMENT,
    `nom` varchar(64) NOT NULL,
    PRIMARY KEY (`pk_type_vehicule`),
    UNIQUE KEY `nom_UNIQUE` (`nom`),
    UNIQUE KEY `pk_type_UNIQUE` (`pk_type_vehicule`)
) ENGINE = InnoDB AUTO_INCREMENT = 7 DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_unicode_ci;

LOCK TABLES `t_type_vehicule` WRITE;

INSERT INTO
    `t_type_vehicule`
VALUES
    (4, 'Bus'),
    (5, 'Car'),
    (6, 'Limousine VIP'),
    (3, 'Mini-Bus'),
    (1, 'Van'),
    (2, 'Voiture');

UNLOCK TABLES;

DROP TABLE IF EXISTS `t_vehicule`;

CREATE TABLE `t_vehicule` (
    `pk_vehicule` int NOT NULL AUTO_INCREMENT,
    `fk_type_vehicule` int NOT NULL,
    `nom` varchar(64) NOT NULL,
    `description` varchar(128) DEFAULT '',
    `capacite` int DEFAULT 2,
    `version` int DEFAULT 0,
    PRIMARY KEY (`pk_vehicule`),
    UNIQUE KEY `pk_vehicule_UNIQUE` (`pk_vehicule`),
    KEY `pk_type_idx` (`fk_type_vehicule`),
    CONSTRAINT `pk_type_vehicule` FOREIGN KEY (`fk_type_vehicule`) REFERENCES `t_type_vehicule` (`pk_type_vehicule`)
) ENGINE = InnoDB AUTO_INCREMENT = 15 DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_unicode_ci;

LOCK TABLES `t_vehicule` WRITE;

INSERT INTO
    `t_vehicule` (
        `pk_vehicule`,
        `fk_type_vehicule`,
        `nom`,
        `description`,
        `capacite`
    )
VALUES
    (
        1,
        1,
        'Van Rouge A1',
        'Van Bedford / 5+1 places',
        5
    ),
    (
        2,
        1,
        'Van Rouge B',
        'Van Bedford / 5+1 places',
        5
    ),
    (
        3,
        3,
        'Mini-bus 01',
        'Minibus Volkswagen / 7+1 places',
        7
    ),
    (
        4,
        3,
        'Mini-bus 02',
        'Minibus Volkswagen / 7+1 places',
        7
    ),
    (
        5,
        6,
        'Limo VIP',
        'Limousine VIP extra-longue / 6+1 places',
        6
    ),
    (
        6,
        2,
        'Voiture V1',
        'Voiture BMW serie 7 / 3+1 places',
        3
    ),
    (
        7,
        2,
        'Voiture V2',
        'Voiture BMW serie 7 / 3+1 places',
        3
    ),
    (
        8,
        2,
        'Voiture V3',
        'Voiture BMW serie 7 / 3+1 places',
        3
    ),
    (
        9,
        2,
        'Voiture V4',
        'Voiture BMW serie 7 / 3+1 places',
        3
    ),
    (
        10,
        4,
        'Bus jaune',
        'Bus GFM modèle 2012 / 28+1 places',
        28
    ),
    (
        11,
        4,
        'Bus rouge',
        'Bus GFM modèle 2007 / 32+1 places',
        32
    ),
    (
        12,
        5,
        'Car Jupiter',
        'Car climatisé Renault X784 / 64+1 places',
        64
    ),
    (
        13,
        5,
        'Car Neptune',
        'Car climatisé Renault X784 / 64+1 places',
        64
    ),
    (
        14,
        5,
        'Car Venus',
        'Car climatisé Renault X784 / 64+1 places',
        64
    );

UNLOCK TABLES;

DROP TABLE IF EXISTS `t_voyageur`;

CREATE TABLE `t_voyageur` (
    `pk_voyageur` int NOT NULL AUTO_INCREMENT,
    `nom` varchar(128) DEFAULT '',
    `prenom` varchar(128) DEFAULT '',
    `rue` varchar(128) DEFAULT '',
    `npa` varchar(8) DEFAULT '',
    `ville` varchar(64) DEFAULT '',
    `date_naissance` date DEFAULT '1970-01-01',
    `version` int DEFAULT 0,
    PRIMARY KEY (`pk_voyageur`),
    UNIQUE KEY `pk_client4_UNIQUE` (`pk_voyageur`)
) ENGINE = InnoDB AUTO_INCREMENT = 38 DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_unicode_ci;

LOCK TABLES `t_voyageur` WRITE;

INSERT INTO
    `t_voyageur` (
        `pk_voyageur`,
        `nom`,
        `prenom`
    )
VALUES
    (1, 'VAUTHEY ', 'Vincent'),
    (2, 'GOLAY', 'Warunee'),
    (3, 'BIOLLEY', 'Pauline Julie'),
    (4, 'KUCI', 'Elvin'),
    (5, 'VIAL', 'Luca'),
    (6, 'DIEU', 'Ludovic'),
    (7, 'PILLER', 'Damian'),
    (8, 'MARALDI', 'Kenzo'),
    (9, 'DOUGOUD ', 'Guillaume'),
    (10, 'FRANZEN', 'Leander'),
    (11, 'GODEL', 'Erik Shady Marius'),
    (12, 'LEVRAT', 'Thibault'),
    (13, 'FANKHAUSER', 'Simon'),
    (14, 'MENOUD', 'Lucas Romaric'),
    (15, 'CUENDET DE CARVALHO', 'Rafael'),
    (16, 'PILLONEL', 'Adrien'),
    (17, 'GAMEZ', 'Jonathan'),
    (18, 'CIUCA', 'Christoph Alexander');

UNLOCK TABLES;

DROP TABLE IF EXISTS `tr_vehicule_voyageur`;

CREATE TABLE `tr_vehicule_voyageur` (
    `fk_vehicule` int NOT NULL,
    `fk_voyageur` int NOT NULL,
    KEY `pk_client_idx` (`fk_voyageur`),
    KEY `pk_vehicule_idx` (`fk_vehicule`),
    CONSTRAINT `pk_vehicule` FOREIGN KEY (`fk_vehicule`) REFERENCES `t_vehicule` (`pk_vehicule`),
    CONSTRAINT `pk_voyageur` FOREIGN KEY (`fk_voyageur`) REFERENCES `t_voyageur` (`pk_voyageur`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_unicode_ci;

LOCK TABLES `tr_vehicule_voyageur` WRITE;

UNLOCK TABLES;