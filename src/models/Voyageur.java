package models;

import java.sql.Date;
import java.time.LocalDate;

public class Voyageur {

    private long pkVoyageur;
    private String nom;
    private String prenom;
    private String rue;
    private String npa;
    private String ville;
    private Date dateNaissance;
    private int version;

    public Voyageur() {
        pkVoyageur = 0;
        nom = "";
        prenom = "";
        rue = "";
        npa = "";
        ville = "";
        dateNaissance = java.sql.Date.valueOf(LocalDate.of(1970, 1, 1));
        version = 0;
    }

    public long getPkVoyageur() {
        return pkVoyageur;
    }

    public void setPkVoyageur(long pkVoyageur) {
        this.pkVoyageur = pkVoyageur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public String getNpa() {
        return npa;
    }

    public void setNpa(String npa) {
        this.npa = npa;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
