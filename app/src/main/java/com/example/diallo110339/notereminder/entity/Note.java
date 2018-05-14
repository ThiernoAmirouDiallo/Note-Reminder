package com.example.diallo110339.notereminder.entity;

public class Note {
    Long id;
    String user;
    String tache;
    String echeance;
    int ordre;
    String couleur;

    public Note(){}

    public Note(Long id,
            String user,
            String tache,
            String echeance,
            int ordre,
            String couleur
    ){
        this.id=id;
        this.user=user;
        this.tache=tache;
        this.echeance=echeance;
        this.ordre=ordre;
        this.couleur=couleur;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTache() {
        return tache;
    }

    public void setTache(String tache) {
        this.tache = tache;
    }

    public String getEcheance() {
        return echeance;
    }

    public void setEcheance(String echeance) {
        this.echeance = echeance;
    }

    public int getOrdre() {
        return ordre;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", user='" + user + '\'' +
                ", tache='" + tache + '\'' +
                ", echeance='" + echeance + '\'' +
                ", ordre=" + ordre +
                ", couleur='" + couleur + '\'' +
                '}';
    }
}
