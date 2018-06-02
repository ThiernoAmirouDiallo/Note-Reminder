package com.example.diallo110339.notereminder.retrofitClient;

import com.example.diallo110339.notereminder.entity.AjoutModifResultat;
import com.example.diallo110339.notereminder.entity.ConnResultat;
import com.example.diallo110339.notereminder.entity.ListResultat;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

//configuration du retrofit client
//il permet de delarer les urls a appeler en donnant les parametres et les types de retours.
//il se charge en suite de l'implemention de meme que la serialisation
// et la deserialisation des objets en parametre en en retour des appels
public interface NoteReminderClient {
    @GET("connect/{user}/{password}")
    Call<ConnResultat> connect(@Path("user") String user, @Path("password") String password);


    //@GET("connect/thierno/diallo")
    //Call<Resultat> conn();

    @GET("list")
    Call<ListResultat> getUserNotes();

    @GET("remove/{id}")
    Call<ListResultat> removeNote(@Path("id") Integer id);

    //@POST("add")
    //Call<AjoutModifResultat> addNote(@Body NoteToPost note);

    @FormUrlEncoded
    @POST("add")
    Call<AjoutModifResultat> addNote(@Field("texte") String text,
                                     @Field("o") Integer o,
                                     @Field("c") String c,
                                     @Field("e") String e);

    @FormUrlEncoded
    @POST("change/{id}")
    Call<AjoutModifResultat> updateNote(@Path("id") Integer id,
                                        @Field("id") Integer idd,
                                        @Field("texte") String text,
                                        @Field("o") Integer o,
                                        @Field("c") String c,
                                        @Field("e") String e);


}
