package models;

import java.util.ArrayList;
import java.util.List;

public class Vehicule {

    private long pkVehicule;
    private String nom;
    private String description;
    private int capacite;
    private int version;
    private List<Voyageur> passagers;

    public Vehicule() {
        pkVehicule = 0;
        nom = "";
        description = "";
        capacite = 0;
        version = 0;
        passagers = new ArrayList<>();
    }

    public long getPkVehicule() {
        return pkVehicule;
    }

    public void setPkVehicule(long pkVehicule) {
        this.pkVehicule = pkVehicule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void addPassager(Voyageur passager) {
        passagers.add(passager);
    }

    public List<Voyageur> getPassagers() {
        return passagers;
    }

    public void setPassagers(List<Voyageur> passagers) {
        this.passagers = passagers;
    }

    public void clearPassagers() {
        passagers.clear();
    }

    public void idem(Vehicule source) {
        pkVehicule = source.pkVehicule;
        nom = source.nom;
        description = source.description;
        capacite = source.capacite;
        version = source.version;
        passagers = new ArrayList<>(source.getPassagers());
    }

    @Override
    public String toString() {
        return nom + " (max " + capacite + " passagers)";
    }

}
