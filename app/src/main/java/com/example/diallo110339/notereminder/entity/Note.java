package com.example.diallo110339.notereminder.entity;

import android.support.annotation.NonNull;

import java.io.Serializable;

//l'entie note se trouvant dans la liste des notes
//cette note implement l'interface Comparable qui permet de trier la liste des notes (ce qui est fait après
//la recuperation depuis le serveur des notes (malgré que l'api fourni une liste triée ce qui pourrait remplacer cela
//par exemple avec ca, on peut trier comme on veut par odre croissant decroissant, par echeance, ... en ajoutant un champ type trie statique dans la liste
//qu'on test dans la methode compareTo
public class Note implements Serializable, Comparable<Note>{
    Integer id;
    String user;
    String tache;
    String echeance;
    Integer ordre;
    String couleur;

    public Note(){}

    public Note(Integer id,
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


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    @Override
    public int compareTo(@NonNull Note o) {
        return ordre<o.ordre?-1:ordre==o.ordre?0:1;
    }
}
