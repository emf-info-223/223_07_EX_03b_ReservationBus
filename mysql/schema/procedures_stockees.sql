USE `bus_company`;

DROP PROCEDURE IF EXISTS AjouterVoyageurs;

DELIMITER $$

CREATE PROCEDURE AjouterVoyageurs(
    IN id_vehicule INT,
    IN liste_voyageurs TEXT,
    OUT reussi BOOLEAN,
    OUT msg TEXT
)
BEGIN
    DECLARE capacite_totale INT;
    DECLARE nb_voyageurs_actuels INT;
    DECLARE nb_voyageurs_a_ajouter INT;
    DECLARE nb_voyageurs_en_trop INT;
    DECLARE nb_voyageurs_deja_places INT;
    DECLARE msg_erreur TEXT;

    SET autocommit=0;
    START TRANSACTION;

    -- Verrouillage du vehicule concerné
    SELECT pk_vehicule FROM t_vehicule WHERE pk_vehicule = id_vehicule FOR UPDATE;

    -- Verrouillage de tous les voyageurs concernés (pour éviter qu'on en mette, en même temps, dans 2 véhicules différents)
    SELECT pk_voyageur FROM t_voyageur WHERE FIND_IN_SET(pk_voyageur, liste_voyageurs) > 0 FOR UPDATE;

    -- Récupérer la capacité totale du véhicule
    SELECT capacite INTO capacite_totale FROM t_vehicule WHERE pk_vehicule = id_vehicule;

    -- Récupérer le nombre actuel de voyageurs dans le véhicule
    SELECT COUNT(*) INTO nb_voyageurs_actuels FROM tr_vehicule_voyageur WHERE fk_vehicule = id_vehicule;

    -- Compter combien de voyageurs sont à ajouter
    SET nb_voyageurs_a_ajouter = (LENGTH(liste_voyageurs) - LENGTH(REPLACE(liste_voyageurs, ',', '')) + 1);

    -- Compter combien de ces voyageurs sont déjà dans des véhicules (ça devrait être zéro !)
    SELECT COUNT(*) INTO nb_voyageurs_deja_places FROM tr_vehicule_voyageur WHERE FIND_IN_SET(fk_voyageur, liste_voyageurs) > 0;

    -- Vérification que tous les voyageurs sont "libres"
    IF ( nb_voyageurs_deja_places = 0 ) THEN

        -- Vérification de la capacité
        IF (nb_voyageurs_actuels + nb_voyageurs_a_ajouter) <= capacite_totale THEN

            -- Insertion depuis la liste des voyageurs
            INSERT INTO tr_vehicule_voyageur (fk_vehicule, fk_voyageur)
            SELECT id_vehicule, pk_voyageur
            FROM t_voyageur
            WHERE FIND_IN_SET(pk_voyageur, liste_voyageurs) > 0;

            -- Définir les informations retournées
            SELECT TRUE, 'Ajout des voyageurs reussi !' INTO reussi, msg;

            -- Commit cette transaction qui s'est bien passée...
            COMMIT;
        ELSE
            -- Annulation de la transaction car la capacité est dépassée
            ROLLBACK;

            -- Définir les informations retournées
            SET nb_voyageurs_en_trop = nb_voyageurs_a_ajouter + nb_voyageurs_actuels - capacite_totale;
            SET msg_erreur =  CONCAT('Capacite insuffisante : il manque ', nb_voyageurs_en_trop, ' places');
            SELECT FALSE, msg_erreur INTO reussi, msg;

        END IF;
    ELSE

        -- Annulation de la transaction car certains voyageurs sont déjà placés !
        ROLLBACK;

        -- Définir les informations retournées
        SELECT FALSE, 'Erreur : certains de ces voyageurs sont déjà placés !' INTO reussi, msg;

    END IF;

    SET autocommit=1;    
END$$

DELIMITER ;
