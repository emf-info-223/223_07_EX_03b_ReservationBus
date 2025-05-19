package ctrl;

import java.util.List;

import models.Vehicule;
import models.Voyageur;

public interface IControllerForView {

    void actionLogin();

    void actionLogout();

    void actionQuitter();

    void actionListeRafraichir();

    void actionVoyageurCreer();

    void actionVoyageurModifier(Voyageur voyageurAModifier);

    void actionVoyageurSupprimer(Voyageur voyageurASupprimer);

    void actionPlacerVoyageursDansVehicule(Vehicule vehicule, List<Voyageur> voyageursAPlacerDansVehicule);

    void actionLibererVoyageursDuVehicule(Vehicule vehicule, List<Voyageur> voyageursALibererDuVehicule);

}
