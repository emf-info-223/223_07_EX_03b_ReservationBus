package models;

public class TypeVehicule {

    private long pkTypeVehicule;
    private String nom;

    public TypeVehicule() {
        this.pkTypeVehicule = 0;
        this.nom = null;
    }

    public long getPkTypeVehicule() {
        return pkTypeVehicule;
    }

    public void setPkTypeVehicule(long pkTypeVehicule) {
        this.pkTypeVehicule = pkTypeVehicule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

}
