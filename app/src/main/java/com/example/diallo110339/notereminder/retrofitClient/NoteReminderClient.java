package com.example.diallo110339.notereminder.retrofitClient;

import com.example.diallo110339.notereminder.entity.ConnResultat;
import com.example.diallo110339.notereminder.entity.ListResultat;
import com.example.diallo110339.notereminder.entity.Note;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NoteReminderClient {
    @GET("connect/{user}/{password}")
    Call<ConnResultat> connect(@Path("user") String user, @Path("password") String password);


    //@GET("connect/thierno/diallo")
    //Call<Resultat> conn();

    @GET("list")
    Call<ListResultat> getUserNotes();

    @POST("add")
    Call<ConnResultat> addNote(Note note);

    @POST("change/{id}")
    Call<ConnResultat> updateNote(@Path("id") String id, Note note);
}
