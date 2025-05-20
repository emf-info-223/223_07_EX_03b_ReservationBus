package services.db.buscompany;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import models.Vehicule;
import models.Voyageur;
import services.db.DBException;

public class ServiceDBBusCompanyUsingSP extends ServiceDBBusCompany {

    public ServiceDBBusCompanyUsingSP() {
        super();
    }

    @Override
    public void dbPlacerVoyageursDansVehicule(Vehicule vehicule, List<Voyageur> voyageursAPlacer) throws DBException {

        if (vehicule == null) {
            throw new DBException("Aucun véhicule fourni : on ne peut pas mettre ces voyageurs dedans !");
        } else if ((voyageursAPlacer == null) || voyageursAPlacer.isEmpty()) {
            throw new DBException("Aucun voyageur fourni : rien à mettre dans ce véhicule !");
        }

        // Préparer liste des pk_voyageur séparée par des virgules pour le 2ème paramètre d'appel de la procédure stockée
        String paramListeDesVoyageursAAjouter = "";
        for (Voyageur voyageur : voyageursAPlacer) {
            if (!paramListeDesVoyageursAAjouter.isEmpty())
                paramListeDesVoyageursAAjouter += ",";
            paramListeDesVoyageursAAjouter += voyageur.getPkVoyageur();
        }

        // Appeler la procédure stockée
        try {
            // Préparer l'appel avec les paramètres
            CallableStatement cStmt = getDbConnexion().prepareCall("{call AjouterVoyageurs(?, ?, ?, ?)}");
            cStmt.setLong(1, vehicule.getPkVehicule());
            cStmt.setString(2, paramListeDesVoyageursAAjouter);

            // Enregistrer les informations retournées
            cStmt.registerOutParameter("reussi", Types.BOOLEAN);
            cStmt.registerOutParameter("msg", Types.VARCHAR);

            // Executer la procedure stockée (celle-ci ne produit pas de "result-set", donc pas besoin de tenter ensuite de le lire)
            /* boolean hadResults = */ cStmt.execute();

            // Récupérer les output parameters
            boolean reussi = cStmt.getBoolean("reussi");
            String msg = cStmt.getString("msg");

            // Vérifier si ça s'est bien passé
            if (!reussi) {
                throw new DBException(msg);
            }

            // Relire ce véhicule de la BD car il a été modifié
            Vehicule refreshed = dbLireVehicule(vehicule.getPkVehicule());
            vehicule.idem(refreshed);

        } catch (SQLException ex) {
            throw new DBException(ex.getMessage());
        }
    }

}
