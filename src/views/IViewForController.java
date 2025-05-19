package views;

import java.util.List;

import models.Vehicule;
import models.Voyageur;

public interface IViewForController {

    void start();

    void afficherMessageInformation(String message);

    void afficherMessageErreur(String message);

    boolean afficherQuestionOuiNon(String message);

    void montrerEtatActuel(boolean onEstConnecte);

    void definirListeVehicules(List<Vehicule> vehicules);

    void definirListeVoyageursAPlacer(List<Voyageur> voyageursAPlacer);

    String obtenirValeurFiltreSurNom();

    void reinitialiserFiltreSurNom();

    void selectionnerVehicule(Vehicule vehicule);

    void selectionnerVoyageur(Voyageur voyageur);
}
