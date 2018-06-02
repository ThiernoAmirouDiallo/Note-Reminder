package com.example.diallo110339.notereminder;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diallo110339.notereminder.entity.AjoutModifResultat;
import com.example.diallo110339.notereminder.entity.Note;
import com.example.diallo110339.notereminder.entity.NoteToPost;
import com.example.diallo110339.notereminder.retrofitClient.NoteReminderClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import android.widget.Toolbar;

public class NewNote extends AppCompatActivity {

    Spinner spinner;
    //nom des couleurs
    public static final String[] couleursNames = {"ROUGE","ORANGE","JAUNE","VIOLET","MAGENTA","BLUE VIOLET","VERT","BLEU","OR","BLANC","BEIGE","ARGENT","GRIS","NOIR"};
    //codes des couleurs
    public static final String[] couleursCodes = {"#FF0000","#FFA500","#FFFF00","#EE82EE","#FF00FF","#8A2BE2","#008000","#0000FF","#DAA520","#FFFFFF","#F5F5DC","#C0C0C0","#808080","#000000"};

    //calendrier
    Calendar myCalendar = Calendar.getInstance();

    //element de la vue
    EditText edittext;
    Button editButton;
    TextView colorTextViewPreview;
    EditText tacheEditText;
    DatePickerDialog.OnDateSetListener date;
    int smallestOrder;
    Button submitButton;
    ProgressBar loadingSpinner;
    String typeCrud;
    Note note = new Note();


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

        editButton= (Button) findViewById(R.id.echeanceButton);
        edittext= (EditText) findViewById(R.id.echeanceEditText);
        colorTextViewPreview= (TextView) findViewById(R.id.colorPreviewTextView);
        tacheEditText= (EditText) findViewById(R.id.tacheEditText);
        submitButton =(Button) findViewById(R.id.submitButton);
        loadingSpinner= (ProgressBar) findViewById(R.id.progressBar);
        spinner = (Spinner)findViewById(R.id.couleurSpinner);

        //preparatoin de la selection des couleurs
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewNote.this,
                android.R.layout.simple_spinner_item,couleursNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                //après select d'une couleur, on met a jour le pre visualisation
                colorTextViewPreview.setBackgroundColor(Color.parseColor(couleursCodes[spinner.getSelectedItemPosition()]));
                //Toast.makeText(getApplicationContext(),couleursNames[spinner.getSelectedItemPosition()],Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        //preparation de l'intent de redirection après succès
        Intent intent =getIntent();
        typeCrud=intent.getStringExtra("typeCrud");
        if (typeCrud.equals("ADD"))
        {
            smallestOrder =Integer.parseInt(intent.getStringExtra("smallestOrder"));
            submitButton.setText("Valider");

            //mettre des couleurs par defaut de façon aleatoire de [0 - nombredecoulerur -1 ]
            int randomPosition =new Random().nextInt(this.couleursCodes.length);
            spinner.setSelection(randomPosition);
            //colorTextViewPreview.setBackgroundColor(Color.parseColor(couleursCodes[randomPosition]));
        }
        else {
            //recuperation dans l'intent de la note a modifier
            note = (Note) intent.getSerializableExtra("noteObj");
            //mise a jour des champs
            edittext.setText(note.getEcheance());
            tacheEditText.setText(note.getTache());
            submitButton.setText("Modifier");
            //colorTextViewPreview.setBackgroundColor(Color.parseColor(note.getCouleur()));
            spinner.setSelection(getColorIndex(note.getCouleur()));

        }

        //on positionne le curseur dans le champ tache
        tacheEditText.setSelection(tacheEditText.length());

        //Toast.makeText(getApplicationContext(),smallestOrder+note.toString(),Toast.LENGTH_LONG).show();

        //parametrage du date picker
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //affichage de la date selectionée
                updateLabel();
            }

        };

        //après click sur le bouton de selection des dates, on affiche le calendrier
        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(NewNote.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    //affichage de la date selectionnée
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);

        edittext.setText(sdf.format(myCalendar.getTime()));
    }


    //click sur le boutton de retour
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    //ajout de la note et si ok, on retourne sur la liste
    public  void addNote(){
        NoteReminderClient client = MainActivity.getClient().create(NoteReminderClient.class);

        NoteToPost note =new NoteToPost();
        note.setC(couleursCodes[spinner.getSelectedItemPosition()]);
        note.setE(edittext.getText().toString());
        note.setTexte(tacheEditText.getText().toString());
        note.setO(smallestOrder-1);


        Call<AjoutModifResultat> call = client.addNote(note.getTexte(),note.getO(),note.getC(),note.getE());

        call.enqueue(new Callback<AjoutModifResultat>() {
            //si succès
            @Override
            public void onResponse(Call<AjoutModifResultat> call, Response<AjoutModifResultat> response) {
                AjoutModifResultat resultat = response.body();
                //Log.i("Notes",response.body().getCode());
                //Toast.makeText(NewNote.this, "Ajout note OK : "+resultat.toString(),Toast.LENGTH_LONG).show();
                //Log.i("Notes","Recuperation des notes OK : "+resultat.toString());

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("actionType","add");
                startActivity(intent);
            }

            //si erreur avec l'api
            @Override
            public void onFailure(Call<AjoutModifResultat> call, Throwable t) {
                //affiche le boutton pour ressoumission et un message
                showButton();
                Toast.makeText(getApplicationContext(),"Impossible d'ajouter la note ",Toast.LENGTH_LONG).show();
                //Log.i("Notes","Impossible d'ajouter la note : "+t.toString() + " " + t.getStackTrace());

            }
        });
    }

    //modification
    public  void editNote(){
        NoteReminderClient client = MainActivity.getClient().create(NoteReminderClient.class);
        NoteToPost note =new NoteToPost();
        note.setId(this.note.getId());
        note.setC(couleursCodes[spinner.getSelectedItemPosition()]);
        note.setE(edittext.getText().toString());
        note.setTexte(tacheEditText.getText().toString());
        note.setO(this.note.getOrdre());

        Call<AjoutModifResultat> call = client.updateNote(note.getId(),note.getId(),note.getTexte(),note.getO(),note.getC(),note.getE());

        call.enqueue(new Callback<AjoutModifResultat>() {
            //si succès
            @Override
            public void onResponse(Call<AjoutModifResultat> call, Response<AjoutModifResultat> response) {
                AjoutModifResultat resultat = response.body();
                //Log.i("Notes",response.body().getCode());
                //Toast.makeText(NewNote.this, "Modification de la note OK : "+resultat.toString(),Toast.LENGTH_LONG).show();
                //Log.i("Notes","Modification de la note OK : "+resultat.toString());

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("actionType","modif");
                startActivity(intent);
            }

            //si erreur
            @Override
            public void onFailure(Call<AjoutModifResultat> call, Throwable t) {
                //affiche le boutton pour ressoumission et un message
                showButton();
                Toast.makeText(getApplicationContext(),"Impossible de modifier la note ",Toast.LENGTH_LONG).show();
                //Log.i("Notes","Impossible de modifier la note : "+t.toString() + " " + t.getStackTrace());

            }
        });
    }

    //click sur le boutton de validation
    public void submitForm(View v){
        //on masque le boutton et on affiche le spinner (loading)
        showSpiner();
        //validation du formulaire
        if(tacheEditText.getText().length()<2)
        {
            Toast.makeText(getApplicationContext(),"Saisissez une description pour la tache d'au moins 2 caractères",Toast.LENGTH_LONG).show();
            tacheEditText.setSelection(tacheEditText.length());
            showButton();
            return;
        }

        if(edittext.getText().length()==0)
        {
            Toast.makeText(getApplicationContext(),"Selectionnez l'echeance",Toast.LENGTH_LONG).show();
            showButton();
            return;
        }

        //si ajout on appelle le fonction ajout
        if (typeCrud.equals("ADD"))
            addNote();
        else
            //sinon on appelle la fonction de modification
            editNote();
    }
    //affichage du boutton et on masque le loading
    private void showButton() {
        loadingSpinner.setVisibility(View.GONE);
        submitButton.setVisibility(View.VISIBLE);
    }

    //on masque le boutton et on afficher le loading
    private void showSpiner() {
        loadingSpinner.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.GONE);
    }

    //recupeation de la position a partir du code de la couleur
    //utilisé pour affiché le nom de la couleur correspondante dans la vue de details des note
    public static int getColorIndex(String couleur) {
        int colorPosition =-1;

        for (String color : NewNote.couleursCodes){
            colorPosition++;
            if (color.equals(couleur)){
                break;
            }
        }

        return colorPosition;
    }
}
