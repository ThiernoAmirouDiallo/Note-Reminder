package com.example.diallo110339.notereminder.entity;

import java.util.List;

//resultat de l'api donnant la liste des notes
public class ListResultat {
    String code;
    String message;
    List<Note> data;

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

    public List<Note> getData() {
        return data;
    }

    public void setData(List<Note> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ListResultat{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
