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
