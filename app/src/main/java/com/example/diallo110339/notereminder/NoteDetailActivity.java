package com.example.diallo110339.notereminder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.diallo110339.notereminder.entity.ListResultat;
import com.example.diallo110339.notereminder.entity.Note;
import com.example.diallo110339.notereminder.retrofitClient.NoteReminderClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteDetailActivity extends AppCompatActivity {

    String noteId;
    Note note;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        Intent intent =getIntent();
        noteId=intent.getStringExtra("noteId");
        note = (Note) intent.getSerializableExtra("noteObj");
        Toast.makeText(NoteDetailActivity.this, "Note : "+note,Toast.LENGTH_LONG).show();
// add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteButtonClicked(View view){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_menu_delete)
                .setTitle("Suppression")
                .setMessage("Êtes vous sur de bien vouloir supprimer cette note?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //delete the note
                        deleteNote();

                    }

                })
                .setNegativeButton("Non", null)
                .show();
    }

    public void editButtonClicked(View view){
        Intent intent = new Intent(getApplicationContext(),EditNote.class);
        intent.putExtra("noteId",noteId);
        intent.putExtra("noteObj",note);
        startActivity(intent);
    }

    public  void deleteNote(){
        NoteReminderClient client = MainActivity.getClient().create(NoteReminderClient.class);

        Call<ListResultat> call = client.removeNote(note.getId());

        call.enqueue(new Callback<ListResultat>() {
            @Override
            public void onResponse(Call<ListResultat> call, Response<ListResultat> response) {
                ListResultat resultat = response.body();
                Log.i("Notes",response.body().getCode());
                Toast.makeText(NoteDetailActivity.this, "Suppression de la note OK : "+resultat.toString(),Toast.LENGTH_LONG).show();
                Log.i("Notes","Suppression de la note OK : "+resultat.toString());

                // and show the note list
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("actionType","deletion");
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<ListResultat> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Impossible de supprimer la note ",Toast.LENGTH_LONG).show();
                Log.i("Notes","Impossible de supprimer la note : "+t.toString() + " " + t.getStackTrace());

            }
        });
    }
}
