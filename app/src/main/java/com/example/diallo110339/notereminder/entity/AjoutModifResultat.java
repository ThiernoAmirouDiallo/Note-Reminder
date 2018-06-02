package com.example.diallo110339.notereminder.entity;

//result de l'aip après ajout ou modification d'une note
public class AjoutModifResultat {
    String code;
    String message;
    Note data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Note getData() {
        return data;
    }

    public void setData(Note data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AjoutModifResultat{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
