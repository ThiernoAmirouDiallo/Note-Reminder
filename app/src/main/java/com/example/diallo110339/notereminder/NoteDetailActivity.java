package com.example.diallo110339.notereminder;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

    EditText edittext;
    TextView colorTextViewPreview;
    EditText tacheEditText;
    //ProgressBar loadingSpinner;
    Button editButton;
    Spinner spinner;
    TextView colorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        Intent intent =getIntent();
        noteId=intent.getStringExtra("noteId");
        note = (Note) intent.getSerializableExtra("noteObj");
        //Toast.makeText(NoteDetailActivity.this, "Note : "+note,Toast.LENGTH_LONG).show();
// add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        editButton= (Button) findViewById(R.id.echeanceButton);
        spinner = (Spinner)findViewById(R.id.couleurSpinner);
        colorTextView = (TextView) findViewById(R.id.colorTextView);

        edittext= (EditText) findViewById(R.id.echeanceEditText);
        colorTextViewPreview= (TextView) findViewById(R.id.colorPreviewTextView);
        tacheEditText= (EditText) findViewById(R.id.tacheEditText);
        //loadingSpinner= (ProgressBar) findViewById(R.id.progressBar);

        editButton.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);

        tacheEditText.setText(note.getTache());
        tacheEditText.setEnabled(false);
        edittext.setText(note.getEcheance());
        colorTextViewPreview.setBackgroundColor(Color.parseColor(note.getCouleur()));

        int colorPosition =NewNote.getColorIndex(note.getCouleur());

        colorTextView.setText(NewNote.couleursNames[colorPosition]);
        colorTextView.setVisibility(View.VISIBLE);
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

                        //suppression de la note sur click sur le bouton OUI
                        deleteNote();

                    }

                })
                .setNegativeButton("Non", null)
                .show();
    }

    //click sur le boutton modification on redirige sur la vue modification
    public void editButtonClicked(View view){
        Intent intent = new Intent(getApplicationContext(),NewNote.class);
        intent.putExtra("typeCrud","MODIFICATION");
        intent.putExtra("noteObj",note);
        startActivity(intent);
    }

    //suppression de la note
    public  void deleteNote(){
        NoteReminderClient client = MainActivity.getClient().create(NoteReminderClient.class);

        Call<ListResultat> call = client.removeNote(note.getId());

        call.enqueue(new Callback<ListResultat>() {
            //suppressio ok
            @Override
            public void onResponse(Call<ListResultat> call, Response<ListResultat> response) {
                ListResultat resultat = response.body();
                //Log.i("Notes",response.body().getCode());
                //Toast.makeText(NoteDetailActivity.this, "Suppression de la note OK : "+resultat.toString(),Toast.LENGTH_LONG).show();
                //Log.i("Notes","Suppression de la note OK : "+resultat.toString());

                //on redirige sur la liste des notes
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("actionType","deletion");
                startActivity(intent);
            }

            //erreur suppression, on reste sur la meme vue et on affiche un message
            @Override
            public void onFailure(Call<ListResultat> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Erreur pendant la suppression de la note",Toast.LENGTH_LONG).show();
                Log.i("Notes","Erreur pendant la suppression de la note "+t.toString() + " " + t.getStackTrace());

            }
        });
    }
}
