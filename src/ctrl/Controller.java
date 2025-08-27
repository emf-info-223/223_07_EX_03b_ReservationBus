package ctrl;

import services.db.DBException;
import services.db.buscompany.IServiceDBBusCompany;
import services.db.buscompany.ServiceDBBusCompany;
import services.db.buscompany.ServiceDBBusCompanyUsingSP;
import views.IViewForController;

import java.util.List;

import models.Vehicule;
import models.Voyageur;

/**
 * Contrôleur principal de notre application.
 *
 * @author <a href="mailto:paul.friedli@edufr.ch">Paul Friedli</a>
 * @version 1.0.0
 */
public class Controller implements IControllerForView {

    private IServiceDBBusCompany refServiceDBBusCompany;
    private IViewForController refView;

    public Controller() {
        refServiceDBBusCompany = new ServiceDBBusCompany();
        refView = null;
    }

    public void start() {
        refView.start();
    }

    @Override
    public void actionLogin() {
        try {
            refServiceDBBusCompany.dbConnecter();
            refView.afficherMessageInformation("Vous vous êtes connecté à la BD avec succès !");
        } catch (DBException ex) {
            refView.afficherMessageErreur(ex.getMessage());
        }
        refView.montrerEtatActuel(refServiceDBBusCompany.dbEstConnectee());
    }

    @Override
    public void actionLogout() {
        try {
            refServiceDBBusCompany.dbDeconnecter();
            refView.definirListeVehicules(null);
            refView.definirListeVoyageursAPlacer(null);
            refView.reinitialiserFiltreSurNom();
            refView.montrerEtatActuel(refServiceDBBusCompany.dbEstConnectee());
            refView.afficherMessageInformation("Vous n'êtes plus connecté à la BD !");
        } catch (DBException ex) {
            refView.afficherMessageErreur(ex.getMessage());
        }
    }

    @Override
    public void actionListeRafraichir() {
        try {
            List<Vehicule> vehicules = refServiceDBBusCompany.dbLireVehicules();
            refView.definirListeVehicules(vehicules);
            String valeurFiltreSurNom = refView.obtenirValeurFiltreSurNom();
            List<Voyageur> voyageursAPlacer = refServiceDBBusCompany.dbLireVoyageursAPlacer(valeurFiltreSurNom);
            refView.definirListeVoyageursAPlacer(voyageursAPlacer);
            refView.montrerEtatActuel(refServiceDBBusCompany.dbEstConnectee());
            refView.afficherMessageInformation("La liste a été rafraîchie avec succès !");
        } catch (DBException ex) {
            refView.afficherMessageErreur(ex.getMessage());
        }
    }

    @Override
    public void actionVoyageurCreer() {
        Voyageur nouveauVoyageur = new Voyageur();
        try {
            refServiceDBBusCompany.dbCreerVoyageur(nouveauVoyageur);

            refView.reinitialiserFiltreSurNom();
            String valeurFiltreSurNom = refView.obtenirValeurFiltreSurNom();
            List<Voyageur> voyageursAPlacer = refServiceDBBusCompany.dbLireVoyageursAPlacer(valeurFiltreSurNom);

            refView.definirListeVoyageursAPlacer(voyageursAPlacer);
            refView.montrerEtatActuel(refServiceDBBusCompany.dbEstConnectee());
            refView.selectionnerVoyageur(nouveauVoyageur);
            refView.afficherMessageInformation(
                    "Le nouveau voyageur a été créée avec succès ! Vous pouvez maintenant le modifier à votre guise.");
        } catch (DBException ex) {
            refView.afficherMessageErreur(ex.getMessage());
        }
    }

    @Override
    public void actionVoyageurModifier(Voyageur voyageurAModifier) {
        if (voyageurAModifier != null) {
            try {
                refServiceDBBusCompany.dbModifierVoyageur(voyageurAModifier);

                refView.reinitialiserFiltreSurNom();
                String valeurFiltreSurNom = refView.obtenirValeurFiltreSurNom();
                List<Voyageur> voyageursAPlacer = refServiceDBBusCompany.dbLireVoyageursAPlacer(valeurFiltreSurNom);

                refView.definirListeVoyageursAPlacer(voyageursAPlacer);
                refView.montrerEtatActuel(refServiceDBBusCompany.dbEstConnectee());
                refView.selectionnerVoyageur(voyageurAModifier);
                refView.afficherMessageInformation("Le voyageur a été modifié avec succès !");
            } catch (DBException ex) {
                refView.afficherMessageErreur(ex.getMessage());
            }
        }
    }

    @Override
    public void actionVoyageurSupprimer(Voyageur voyageurASupprimer) {
        if (voyageurASupprimer != null) {
            if (refView.afficherQuestionOuiNon("Voulez-vous réellement supprimer ce voyageur ?")) {
                try {
                    refServiceDBBusCompany.dbSupprimerVoyageur(voyageurASupprimer);

                    refView.reinitialiserFiltreSurNom();
                    String valeurFiltreSurNom = refView.obtenirValeurFiltreSurNom();
                    List<Voyageur> voyageursAPlacer = refServiceDBBusCompany.dbLireVoyageursAPlacer(valeurFiltreSurNom);

                    refView.definirListeVoyageursAPlacer(voyageursAPlacer);
                    refView.montrerEtatActuel(refServiceDBBusCompany.dbEstConnectee());

                    refView.afficherMessageInformation("Le voyageur a été supprimé avec succès !");
                } catch (DBException ex) {
                    refView.afficherMessageErreur(ex.getMessage());
                }
            }
        }
    }

    @Override
    public void actionPlacerVoyageursDansVehicule(Vehicule vehicule, List<Voyageur> voyageursAPlacerDansVehicule) {
        try {
            refServiceDBBusCompany.dbPlacerVoyageursDansVehicule(vehicule, voyageursAPlacerDansVehicule);

            String valeurFiltreSurNom = refView.obtenirValeurFiltreSurNom();
            List<Voyageur> voyageursAPlacer = refServiceDBBusCompany.dbLireVoyageursAPlacer(valeurFiltreSurNom);
            refView.definirListeVoyageursAPlacer(voyageursAPlacer);
            refView.montrerEtatActuel(refServiceDBBusCompany.dbEstConnectee());
            refView.selectionnerVehicule(vehicule);
            refView.afficherMessageInformation("Les voyageurs ont été placés dans ce véhicule avec succès !");
        } catch (DBException ex) {
            refView.afficherMessageErreur(ex.getMessage());
        }
    }

    @Override
    public void actionLibererVoyageursDuVehicule(Vehicule vehicule, List<Voyageur> voyageursALibererDuVehicule) {
        try {
            refServiceDBBusCompany.dbLibererVoyageursDuVehicule(vehicule, voyageursALibererDuVehicule);

            String valeurFiltreSurNom = refView.obtenirValeurFiltreSurNom();
            List<Voyageur> voyageursAPlacer = refServiceDBBusCompany.dbLireVoyageursAPlacer(valeurFiltreSurNom);
            refView.definirListeVoyageursAPlacer(voyageursAPlacer);
            refView.montrerEtatActuel(refServiceDBBusCompany.dbEstConnectee());
            refView.selectionnerVehicule(vehicule);

            refView.afficherMessageInformation("Ces voyageurs ont été libérés de ce véhicule avec succès !");
        } catch (DBException ex) {
            refView.afficherMessageErreur(ex.getMessage());
        }
    }

    @Override
    public void actionQuitter() {
    }

    public IViewForController getRefView() {
        return refView;
    }

    public void setRefView(IViewForController refView) {
        this.refView = refView;
    }

}
