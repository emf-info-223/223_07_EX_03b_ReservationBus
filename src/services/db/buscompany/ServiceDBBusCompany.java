package services.db.buscompany;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import models.Vehicule;
import models.Voyageur;
import services.db.DBException;

/**
 * Service spécifique "Base de données - BusCompany" avec JDBC.
 *
 * @author <a href="mailto:paul.friedli@edufr.ch">Paul Friedli</a>
 * @version 1.0.0
 */
public class ServiceDBBusCompany implements IServiceDBBusCompany {

    private final static String DB_BUSCOMPANY_DBNAME = "bus_company";
    private final static String DB_BUSCOMPANY_USERNAME = "emf";
    private final static String DB_BUSCOMPANY_PASSWORD = "emf123";

    private Connection dbConnexion;

    public ServiceDBBusCompany() {
        dbConnexion = null;
    }

    @Override
    public void dbConnecter() throws DBException {
        try {
            dbConnexion = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/" + DB_BUSCOMPANY_DBNAME + "?serverTimezone=CET",
                    DB_BUSCOMPANY_USERNAME,
                    DB_BUSCOMPANY_PASSWORD);
        } catch (SQLException ex) {
            throw new DBException(ex.getMessage());
        }
    }

    @Override
    public void dbDeconnecter() throws DBException {
        try {
            if (dbConnexion != null) {
                dbConnexion.close();
                dbConnexion = null;
            }
        } catch (SQLException ex) {
            throw new DBException(ex.getMessage());
        }
    }

    @Override
    public boolean dbEstConnectee() {
        boolean estConnecte = false;
        try {
            if ((dbConnexion != null) && dbConnexion.isValid(1)) { // vérifie vraiment l'état de la connexion avec la BD
                estConnecte = true;
            }
        } catch (SQLException ex) {
        }

        return estConnecte;
    }

    @Override
    public void dbCreerVoyageur(Voyageur voyageur) throws DBException {

        final String requeteSQL = "INSERT INTO t_voyageur (nom, prenom, rue, npa, ville, date_naissance, version) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            // Créer la requête en demandant de récupérer la PK qui sera générée
            PreparedStatement ps = dbConnexion.prepareStatement(requeteSQL, Statement.RETURN_GENERATED_KEYS);

            // Définir les paramètres de cette manière afin d'éviter l'injection SQL
            ps.setString(1, voyageur.getNom());
            ps.setString(2, voyageur.getPrenom());
            ps.setString(3, voyageur.getRue());
            ps.setString(4, voyageur.getNpa());
            ps.setString(5, voyageur.getVille());
            ps.setDate(6, voyageur.getDateNaissance());
            ps.setInt(7, voyageur.getVersion());

            // exécution de la requête
            int nb = ps.executeUpdate();
            if (nb != 1) {
                throw new DBException("Le voyageur n'a pas pu être créé : " + nb);
            }

            // récuperation de la clé générée et stockage dans l'objet personne
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            voyageur.setPkVoyageur(rs.getLong(1));
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            throw new DBException(ex.getMessage());
        }
    }

    @Override
    public void dbModifierVoyageur(Voyageur voyageur) throws DBException {

        try {
            // Mise à jour de ce voyageur
            String requeteSQL = "UPDATE t_voyageur SET nom=?, prenom=?, rue=?, npa=?, ville=?, date_naissance=? WHERE (pk_voyageur=?)";
            PreparedStatement ps = dbConnexion.prepareStatement(requeteSQL);
            ps.setString(1, voyageur.getNom());
            ps.setString(2, voyageur.getPrenom());
            ps.setString(3, voyageur.getRue());
            ps.setString(4, voyageur.getNpa());
            ps.setString(5, voyageur.getVille());
            ps.setDate(6, voyageur.getDateNaissance());
            ps.setLong(7, voyageur.getPkVoyageur());
            int nb = ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new DBException(ex.getMessage());
        }
    }

    @Override
    public void dbSupprimerVoyageur(Voyageur voyageur) throws DBException {

        try {
            // Supprimer ce voyageur
            String requeteSQL = "DELETE FROM t_voyageur WHERE pk_voyageur=" + voyageur.getPkVoyageur();
            Statement stm = dbConnexion.createStatement();
            int nb = stm.executeUpdate(requeteSQL);
            stm.close();

            // Vérifier si la mise à jour a trouvé sa cible...
            if (nb != 1) {
                throw new DBException(
                        "La suppression a échoué car ce voyageur a probablement déjà été supprimé par quelqu'un d'autre. Veuillez rafraîchir votre liste !");
            }
        } catch (SQLException ex) {
            throw new DBException(ex.getMessage());
        }
    }

    @Override
    public List<Vehicule> dbLireVehicules() throws DBException {

        final String requeteSQL = "SELECT pk_vehicule, nom, description, capacite, version FROM t_vehicule ORDER BY pk_vehicule";
        List<Vehicule> listeVehicules = new ArrayList<>();
        try {
            Statement stm = dbConnexion.createStatement();
            ResultSet rs = stm.executeQuery(requeteSQL);
            while (rs.next()) {
                listeVehicules.add(extraireVehiculeDuResultSet(rs));
            }
            rs.close();
            stm.close();

            // Avant de retourner la liste des véhicules, mettre à jour leur liste de
            // passagers
            for (Vehicule vehicule : listeVehicules) {
                vehicule.setPassagers(lireListePassagersDuVehicule(vehicule));
            }
        } catch (SQLException | NullPointerException ex) {
            listeVehicules = null;
            throw new DBException(ex.getMessage());
        }
        return listeVehicules;
    }

    @Override
    public List<Voyageur> dbLireVoyageursAPlacer(String valeurFiltreSurNom) throws DBException {

        String filtreSurNomOuPrenom = "";
        if ((valeurFiltreSurNom != null) && !valeurFiltreSurNom.isEmpty()) {
            filtreSurNomOuPrenom = " AND ( LOWER(nom) LIKE LOWER(CONCAT('%','" + valeurFiltreSurNom
                    + "','%')) OR LOWER(prenom) LIKE LOWER(CONCAT('%','" + valeurFiltreSurNom + "','%'))) ";
        }

        final String requeteSQL = "SELECT pk_voyageur, nom, prenom, rue, npa, ville, date_naissance, version " +
                "FROM t_voyageur " +
                // Noter cette partie intéressante !
                "WHERE pk_voyageur NOT IN (SELECT fk_voyageur FROM tr_vehicule_voyageur) " +
                filtreSurNomOuPrenom +
                "ORDER BY pk_voyageur";

        return lireListeVoyageurs(requeteSQL);
    }

    @Override
    public void dbPlacerVoyageursDansVehicule(Vehicule vehicule, List<Voyageur> voyageursAPlacer) throws DBException {

        if (vehicule == null) {
            throw new DBException("Aucun véhicule fourni : on ne peut pas mettre ces voyageurs dedans !");
        }

        if ((voyageursAPlacer == null) || voyageursAPlacer.isEmpty()) {
            throw new DBException("Aucun voyageur fourni : rien à mettre dans ce véhicule !");
        }

        try {
            // Démarrer une nouvelle transaction
            dbConnexion.setAutoCommit(false);

            // Y a-t-il encore suffisamment de place dans ce véhicule ?
            if ((voyageursAPlacer.size() + vehicule.getPassagers().size()) > vehicule.getCapacite()) {
                dbConnexion.rollback();
                throw new DBException("La capacité maximale de ce véhicule serait dépassée !");
            }

            // Ajouter maintenant ces passagers à ce véhicule dans la BD
            String insertListPairs = "";
            for (Voyageur voyageur : voyageursAPlacer) {
                if (!insertListPairs.isEmpty()) {
                    insertListPairs += ",";
                }
                insertListPairs += "(" + vehicule.getPkVehicule() + "," + voyageur.getPkVoyageur() + ")";
            }
            String sqlInsererVoyageurs = "INSERT INTO tr_vehicule_voyageur (fk_vehicule,fk_voyageur) VALUES "
                    + insertListPairs;
            Statement stm = dbConnexion.createStatement();
            int nb = stm.executeUpdate(sqlInsererVoyageurs);
            if (nb != voyageursAPlacer.size()) {
                dbConnexion.rollback();
                throw new DBException("Erreur : tous les voyageurs n'ont pas pu être placés dans ce véhicule !");
            }
            stm.close();

            // Si on est là, c'est que tout s'est bien passé ! On peut commit :-)
            dbConnexion.commit();

            // Relire ce véhicule de la BD car il a été modifié
            Vehicule refreshed = dbLireVehicule(vehicule.getPkVehicule());
            vehicule.idem(refreshed);

        } catch (SQLException ex) {
            try {
                dbConnexion.rollback();
            } catch (SQLException e) {
            }
            throw new DBException(ex.getMessage());
        } finally {
            //
            // Quoiqu'il arrive, s'assurer de "fermer" cette transaction
            //
            try {
                dbConnexion.setAutoCommit(true);
            } catch (SQLException ex) {
                throw new DBException(ex.getMessage());
            }
        }
    }

    @Override
    public void dbLibererVoyageursDuVehicule(Vehicule vehicule, List<Voyageur> voyageursALiberer) throws DBException {

        if (vehicule == null) {
            throw new DBException("Aucun véhicule fourni : on ne peut pas sortir ces voyageurs !");
        }

        if ((voyageursALiberer == null) || voyageursALiberer.isEmpty()) {
            throw new DBException("Aucun voyageur fourni : rien à supprimer de ce véhicule !");
        }

        try {
            // Démarrer une nouvelle transaction
            dbConnexion.setAutoCommit(false);

            // Constituer la liste des passagers a supprimer
            List<Long> listeIDVoyageurs = new ArrayList<>();
            for (Voyageur voyageur : voyageursALiberer) {
                listeIDVoyageurs.add(voyageur.getPkVoyageur());
            }
            String strListeIDVoyageursAvecVirgules = listeIDVoyageurs.stream().map(String::valueOf)
                    .collect(Collectors.joining(","));

            // Sortir ces passagers de la liste associée à ce véhicule
            String sqlSortirPassagers = "DELETE FROM tr_vehicule_voyageur WHERE (fk_vehicule="
                    + vehicule.getPkVehicule() + ") AND (fk_voyageur IN ("
                    + strListeIDVoyageursAvecVirgules + "))";

            Statement stm = dbConnexion.createStatement();
            int nb = stm.executeUpdate(sqlSortirPassagers);
            if (nb != voyageursALiberer.size()) {
                dbConnexion.rollback();
                throw new DBException(
                        "Erreur : tous les voyageurs mentionnés n'ont pas pu être retirés de ce véhicule !");
            }
            stm.close();

            // Si on est là, c'est que tout s'est bien passé ! On peut commit :-)
            dbConnexion.commit();

            // Relire ce véhicule de la BD car il a été modifié
            Vehicule refreshed = dbLireVehicule(vehicule.getPkVehicule());
            vehicule.idem(refreshed);
        } catch (SQLException ex) {
            try {
                dbConnexion.rollback();
            } catch (SQLException e) {
            }
            throw new DBException(ex.getMessage());
        } finally {
            //
            // Quoiqu'il arrive, s'assurer de "fermer" cette transaction
            //
            try {
                dbConnexion.setAutoCommit(true);
            } catch (SQLException ex) {
                throw new DBException(ex.getMessage());
            }
        }

    }

    protected Vehicule dbLireVehicule(long pk_vehicule) throws DBException {

        final String requeteSQL = "SELECT pk_vehicule, nom, description, capacite, version FROM t_vehicule WHERE pk_vehicule = "
                + pk_vehicule;
        Vehicule vehicule = null;
        try {
            Statement stm = dbConnexion.createStatement();
            ResultSet rs = stm.executeQuery(requeteSQL);
            if (rs.next()) {
                vehicule = extraireVehiculeDuResultSet(rs);
            }
            rs.close();
            stm.close();
            vehicule.setPassagers(lireListePassagersDuVehicule(vehicule));
        } catch (SQLException | NullPointerException ex) {
            throw new DBException(ex.getMessage());
        }
        return vehicule;
    }

    private Vehicule extraireVehiculeDuResultSet(ResultSet rs) throws DBException {
        Vehicule vehicule = new Vehicule();
        try {
            vehicule.setPkVehicule(rs.getInt("pk_vehicule"));
            vehicule.setNom(rs.getString("nom"));
            vehicule.setDescription(rs.getString("description"));
            vehicule.setCapacite(rs.getInt("capacite"));
            vehicule.setVersion(rs.getInt("version"));
        } catch (SQLException ex) {
            throw new DBException(ex.getMessage());
        }
        return vehicule;
    }

    private Voyageur extraireVoyageurDuResultSet(ResultSet rs) throws DBException {
        Voyageur voyageur = new Voyageur();
        try {
            voyageur.setPkVoyageur(rs.getInt("pk_voyageur"));
            voyageur.setNom(rs.getString("nom"));
            voyageur.setPrenom(rs.getString("prenom"));
            voyageur.setRue(rs.getString("rue"));
            voyageur.setNpa(rs.getString("npa"));
            voyageur.setVille(rs.getString("ville"));
            voyageur.setDateNaissance(rs.getDate("date_naissance"));
            voyageur.setVersion(rs.getInt("version"));
        } catch (SQLException ex) {
            throw new DBException(ex.getMessage());
        }
        return voyageur;
    }

    private List<Voyageur> lireListePassagersDuVehicule(Vehicule vehicule) throws DBException {

        final String requeteSQL = "SELECT pk_voyageur, nom, prenom, rue, npa, ville,date_naissance, version " +
                "FROM t_voyageur " +
                "WHERE pk_voyageur IN (SELECT fk_voyageur FROM tr_vehicule_voyageur WHERE fk_vehicule="
                + vehicule.getPkVehicule() + ") " +
                "ORDER BY pk_voyageur";

        return lireListeVoyageurs(requeteSQL);
    }

    private List<Voyageur> lireListeVoyageurs(String requeteSQL) throws DBException {

        List<Voyageur> listeVoyageurs = new ArrayList<>();
        try {
            Statement stm = dbConnexion.createStatement();
            ResultSet rs = stm.executeQuery(requeteSQL);
            while (rs.next()) {
                listeVoyageurs.add(extraireVoyageurDuResultSet(rs));
            }
            rs.close();
            stm.close();
        } catch (SQLException | NullPointerException ex) {
            listeVoyageurs = null;
            throw new DBException(ex.getMessage());
        }
        return listeVoyageurs;
    }

    public Connection getDbConnexion() {
        return dbConnexion;
    }

}
