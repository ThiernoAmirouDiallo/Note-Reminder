package com.example.diallo110339.notereminder;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class NoteDetailActivity extends AppCompatActivity {

    String noteId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        Intent intent =getIntent();
        noteId=intent.getStringExtra("noteId");

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

                        // and show the note list
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.putExtra("actionType","deletion");
                        startActivity(intent);
                    }

                })
                .setNegativeButton("Non", null)
                .show();
    }

    public void editButtonClicked(View view){
        Intent intent = new Intent(getApplicationContext(),EditNote.class);
        intent.putExtra("noteId",noteId);
        startActivity(intent);
    }
}
