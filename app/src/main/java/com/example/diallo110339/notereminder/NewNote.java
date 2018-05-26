package com.example.diallo110339.notereminder;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.diallo110339.notereminder.entity.AjoutModifResultat;
import com.example.diallo110339.notereminder.entity.NoteToPost;
import com.example.diallo110339.notereminder.retrofitClient.NoteReminderClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import android.widget.Toolbar;

public class NewNote extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        addNote();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public  void addNote(){
        NoteReminderClient client = MainActivity.getClient().create(NoteReminderClient.class);

        NoteToPost note =new NoteToPost();
        note.setC("#fCacaa");
        note.setE("12/05/2018");
        note.setTexte("ajout via retrofit");
        note.setO(12);


        Call<AjoutModifResultat> call = client.addNote(note.getTexte(),note.getO(),note.getC(),note.getE());

        call.enqueue(new Callback<AjoutModifResultat>() {
            @Override
            public void onResponse(Call<AjoutModifResultat> call, Response<AjoutModifResultat> response) {
                AjoutModifResultat resultat = response.body();
                Log.i("Notes",response.body().getCode());
                Toast.makeText(NewNote.this, "Ajout note OK : "+resultat.toString(),Toast.LENGTH_LONG).show();
                Log.i("Notes","Recuperation des notes OK : "+resultat.toString());
            }

            @Override
            public void onFailure(Call<AjoutModifResultat> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Impossible d'ajouter la note ",Toast.LENGTH_LONG).show();
                Log.i("Notes","Impossible d'ajouter la note : "+t.toString() + " " + t.getStackTrace());

            }
        });
    }
}
