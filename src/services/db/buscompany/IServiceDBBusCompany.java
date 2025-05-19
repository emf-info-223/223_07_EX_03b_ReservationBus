package services.db.buscompany;

import models.Vehicule;
import models.Voyageur;
import services.db.DBException;

import java.util.List;

/**
 * @author <a href="mailto:paul.friedli@edufr.ch">Paul Friedli</a>
 * @since 6 sept. 2022
 * @version 1.0.0
 */
public interface IServiceDBBusCompany {

    void dbConnecter() throws DBException;

    void dbDeconnecter() throws DBException;

    boolean dbEstConnectee();

    List<Vehicule> dbLireVehicules() throws DBException;

    List<Voyageur> dbLireVoyageursAPlacer(String valeurFiltreSurNom) throws DBException;

    void dbPlacerVoyageursDansVehicule(Vehicule vehicule, List<Voyageur> voyageursAPlacer) throws DBException;

    void dbLibererVoyageursDuVehicule(Vehicule vehicule, List<Voyageur> voyageursALiberer) throws DBException;

    void dbModifierVoyageur(Voyageur voyageur) throws DBException;

    void dbCreerVoyageur(Voyageur voyageur) throws DBException;

    void dbSupprimerVoyageur(Voyageur voyageur) throws DBException;
}
