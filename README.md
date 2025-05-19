# Exercice JDBC BusCompany

## Objectifs

- Finir de s'approprier de cette technologie JDBC avec un cas réel plus complexe.
- Appliquer les connaissances reçues afin de rendre ce logiciel **"multi-utilisateurs safe"**.

## Vue de l'application concernée

![Ecran principal de l'application](res/AppViewExample.png "Ecran principal de l'application")

## Préalable

Démarrez votre instance `MySQL` et `phpmyadmin` dans Docker à l'aide du fichier `docker-compose.yml` fourni.

Pour faire cela, commencez par lancer `Docker Desktop` sur votre poste de travail puis, dans VSC, sélectionnez le fichier [docker-compose.yml](docker-compose.yml) de votre projet VSC. Faites ensuite un clic-droit sur celui-ci et sélectionnez l'option `compose up`.

Une fois les instances `MySQL` et `phpmyadmin` démarrées, vous pourrez accéder à `phpmyadmin` depuis Docker Desktop en cliquant sur le lien [http://localhost:8080](http://localhost:8080).

Avec son aide, importez ensuite le script [/mysql/schema/crud_personnes.sql](/mysql/schema/schema.sql) afin que la base de données de `bus_company` ci-dessous y soit créée. Elle contiendra les table suivantes :

**Schéma de la BD**

![Schéma BD](mysql/schema/schema.png "Schéma BD").

## Vue d'ensemble UML

### Diagramme UML des classes de l'application

Il s'agit d'une application MVC classique.

```mermaid
classDiagram
direction LR
namespace app {
    class Application {
        main(String[] args) void$
    }
}
namespace models {
    class Vehicule
    class Voyageur
    class TypeVehicule
}
namespace services {
    class ServiceDBBusCompany
    class IServiceDBBusCompany{ <<interface>> }
}
namespace ctrl {
    class Controller {
        -ServiceDBBusCompany refServiceDBBusCompany
        -IViewForController refView
    }
    class IControllerForView{ <<interface>> }
}
namespace views {
    class View {
        -Controller refController
    }
    class IViewForController{ <<interface>> }
}

Controller o--> "1" IViewForController : refView
Controller o--> "1" ServiceDBBusCompany : refServiceDBBusCompany
View o--> "1" IControllerForView : refController
View ..|> IViewForController : "implements"
Controller ..|> IControllerForView : "implements"
ServiceDBBusCompany ..|> IServiceDBBusCompany : "implements"
```

### Diagramme de la classe `ServiceDBBusCompany`

```mermaid
classDiagram
DIRECTION DT
class ServiceDBBusCompany {
    -String DB_BUSCOMPANY_DBNAME = "bus_company"$
    -String DB_BUSCOMPANY_USERNAME = "emf"$
    -String DB_BUSCOMPANY_PASSWORD = "emf123"$
    -Connection dbConnexion

    +ServiceDBBusCompany()

    -long verrouillerVoyageurEtGetVersion(long pk) throws DBException
    -long verrouillerVehiculeEtGetVersion(long pk) throws DBException
    -Vehicule dbLireVehicule(long pk_vehicule) throws DBException
    -Vehicule extraireVehiculeDuResultSet(ResultSet rs) throws DBException
    -Voyageur extraireVoyageurDuResultSet(ResultSet rs) throws DBException
    -List< Voyageur > lireListePassagersDuVehicule(Vehicule vehicule) throws DBException
    -List< Voyageur > lireListeVoyageurs(String requeteSQL) throws DBException
    -void dbIncrementerVersionVehicule(Vehicule vehicule) throws DBException, SQLException
}
note for ServiceDBBusCompany "Le travail consistera à corriger le code dans cette classe qui est fournie et qui est fonctionnelle."

ServiceDBBusCompany ..|> IServiceDBBusCompany : "implements"

class IServiceDBBusCompany {
    <<interface>>
    void dbConnecter() throws DBException
    void dbDeconnecter() throws DBException
    boolean dbEstConnectee()
    List< Vehicule > dbLireVehicules() throws DBException
    List< Voyageur > dbLireVoyageursAPlacer(String valeurFiltreSurNom) throws DBException
    void dbPlacerVoyageursDansVehicule(Vehicule vehicule, List< Voyageur > voyageursAPlacer) throws DBException
    void dbLibererVoyageursDuVehicule(Vehicule vehicule, List< Voyageur > voyageursALiberer) throws DBException
    void dbModifierVoyageur(Voyageur voyageur) throws DBException
    void dbCreerVoyageur(Voyageur voyageur) throws DBException
    void dbSupprimerVoyageur(Voyageur voyageur) throws DBException
}


```

## Travail à réaliser

- Lisez avec attention cette consigne
- Suivez avec attention la démonstration que votre professeur va vous faire, de manière à vous imprégner du fonctionnement de cette application et bien le comprendre.
- Prenez connaissance des classes déjà fournies et leur code
- Appropriez-vous du code fourni et faites l'effort de le comprendre, en particulier celui du service `ServiceDBBusCompany`.
- Corrigez et complétez le code des méthodes de ce service `ServiceDBBusCompany` afin que les use-cases multi-utilisateurs ci-dessous soient corrigés et fonctionnels :
  - Deux instances de l'application essaient de mettre à jour un même voyageur.
  - Deux instances de l'application essaient de (trop) remplir un même véhicule.
  - Une instance de l'application essaie de remplir ou vider un véhicule qu'une autre instance vient de supprimer.
- **Quelles ressources faudrait-il verrouiller** pour éviter ces problèmes ?
- **Quelles vérifications faudrait-il faire** pour garantir ces situations ?
- Faites du pas-par-pas avec le débogueur et vérifiez bien que tous les cas de figure soient fonctionnels, sans générer d'erreurs ni d'exceptions

---

<img src="res/EMF_logo_RVB_Info_long.png" width="25%" style="margin-left:-20px;">
