package com.example.diallo110339.notereminder.entity;

//note a posté
//pour ce projet en particulier il n'y avait pas forcement besoin de cetle classe
// puisque ce n'est pas un objet qui est posté mais plutot des parametre url encodé
//mais pour bien organisé, j'ai preféré la garde
public class NoteToPost {
    Integer id;
    String texte;
    String e;
    Integer o;
    String c;

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public Integer getO() {
        return o;
    }

    public void setO(Integer o) {
        this.o = o;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "NoteToPost{" +
                "id=" + id +
                ", texte='" + texte + '\'' +
                ", e='" + e + '\'' +
                ", o=" + o +
                ", c='" + c + '\'' +
                '}';
    }
}
