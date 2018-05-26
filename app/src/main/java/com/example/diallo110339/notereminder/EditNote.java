package com.example.diallo110339.notereminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.diallo110339.notereminder.entity.AjoutModifResultat;
import com.example.diallo110339.notereminder.entity.Note;
import com.example.diallo110339.notereminder.retrofitClient.NoteReminderClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditNote extends AppCompatActivity {

    String noteId;
    Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Intent intent =getIntent();
        noteId=intent.getStringExtra("noteId");
        note = (Note) intent.getSerializableExtra("noteObj");

        Toast.makeText(this,"Note : "+note,Toast.LENGTH_LONG).show();
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        editNote();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public  void editNote(){
        NoteReminderClient client = MainActivity.getClient().create(NoteReminderClient.class);

        Call<AjoutModifResultat> call = client.updateNote(note.getId(),note.getId(),note.getTache()+ " edited",note.getOrdre(),note.getCouleur(),note.getEcheance());

        call.enqueue(new Callback<AjoutModifResultat>() {
            @Override
            public void onResponse(Call<AjoutModifResultat> call, Response<AjoutModifResultat> response) {
                AjoutModifResultat resultat = response.body();
                Log.i("Notes",response.body().getCode());
                Toast.makeText(EditNote.this, "Modification de la note OK : "+resultat.toString(),Toast.LENGTH_LONG).show();
                Log.i("Notes","Modification de la note OK : "+resultat.toString());
            }

            @Override
            public void onFailure(Call<AjoutModifResultat> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Impossible de modifier la note ",Toast.LENGTH_LONG).show();
                Log.i("Notes","Impossible de modifier la note : "+t.toString() + " " + t.getStackTrace());

            }
        });
    }
}
