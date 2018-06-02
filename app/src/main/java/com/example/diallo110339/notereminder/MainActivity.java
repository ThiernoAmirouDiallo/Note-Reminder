package com.example.diallo110339.notereminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.diallo110339.notereminder.Helper.SimpleItemTouchHelperCallback;
import com.example.diallo110339.notereminder.adapter.NoteAdapter;
import com.example.diallo110339.notereminder.adapter.RecyclerItemListener;
import com.example.diallo110339.notereminder.entity.ConnResultat;
import com.example.diallo110339.notereminder.entity.ListResultat;
import com.example.diallo110339.notereminder.entity.Note;
import com.example.diallo110339.notereminder.retrofitClient.AddCookiesInterceptor;
import com.example.diallo110339.notereminder.retrofitClient.NoteReminderClient;
import com.example.diallo110339.notereminder.retrofitClient.ReceivedCookiesInterceptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    RecyclerView noteListView;
    SwipeRefreshLayout swipeLayout;


    private static final String BASE_URL = "http://daviddurand.info/D228/reminder/";
    private static Retrofit retrofit = null;

    NoteAdapter na;
    List<Note> notes = new ArrayList<>();
    RelativeLayout progressBarRelativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        progressBarRelativeLayout = (RelativeLayout) findViewById(R.id.progressBarRelativeLayout);
        noteListView = (RecyclerView) findViewById(R.id.noteListView);

        //on vérifie si on arrive ici après une suppression de note ou une suppression ou encore une modification
        Intent intent = getIntent();
        String lastActionType = intent.getStringExtra("actionType");
        if (lastActionType != null && lastActionType.equals("deletion"))
            Toast.makeText(this, "Note supprimée avec succès", Toast.LENGTH_LONG).show();
        else if (lastActionType != null && lastActionType.equals("add"))
            Toast.makeText(this, "Note ajoutée avec succès", Toast.LENGTH_LONG).show();
        else if (lastActionType != null && lastActionType.equals("modif"))
            Toast.makeText(this, "Note modifiée avec succès", Toast.LENGTH_LONG).show();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //         .setAction("Action", null).show();
                addNote();
            }
        });

        setListContent();
    }

    //connexion au serveur et recuperation de la liste puis appel de la fonction de remplissage de la vue
    private void setListContent() {
        //a l'actualisaion, on afficher le loading et masque la liste
        showLoading();

        NoteReminderClient client = getClient().create(NoteReminderClient.class);

        Call<ConnResultat> call = client.connect("thierno","diallo");
        //Call<Resultat> call = client.conn();

        call.enqueue(new Callback<ConnResultat>() {
            @Override
            public void onResponse(Call<ConnResultat> call, Response<ConnResultat> response) {
                ConnResultat resultat = response.body();
                //Log.i("res",response.body().getCode());
                //Toast.makeText(getApplicationContext(),"Connexion OK : "+resultat.toString(),Toast.LENGTH_LONG).show();
                //Log.i("Notes","Connexion OK : "+resultat.toString());


                NoteReminderClient client = getClient().create(NoteReminderClient.class);

                Call<ListResultat> call2 = client.getUserNotes();

                call2.enqueue(new Callback<ListResultat>() {
                    @Override
                    public void onResponse(Call<ListResultat> call, Response<ListResultat> response) {
                        ListResultat resultat = response.body();
                        //Log.i("Notes",response.body().getCode());
                        //Toast.makeText(getApplicationContext(),"Recuperation des notes OK : "+resultat.toString(),Toast.LENGTH_LONG).show();
                        //Log.i("Notes","Recuperation des notes OK : "+resultat.toString());

                        //affectation du resultat a la liste
                        notes=resultat.getData();
                        fillList();
                        //on affiche la liste des notes
                        showList();
                    }

                    @Override
                    public void onFailure(Call<ListResultat> call, Throwable t) {
                        //Toast.makeText(getApplicationContext(),"Impossible de recuperer la liste des notes ",Toast.LENGTH_LONG).show();
                        Log.i("Notes","Impossible de recuperer la liste des notes  : "+t.toString() + " " + t.getStackTrace());
                        //on affiche la liste des notes
                        showList();

                    }
                });
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Connexion impossible",Toast.LENGTH_LONG).show();
                showList();
            }
        });


    }
    //affichage des la liste des notes
    public void showList(){
        progressBarRelativeLayout.setVisibility(View.GONE);
        noteListView.setVisibility(View.VISIBLE);
    }

    //affichage du loading (message d'actualisation)
    public void showLoading(){
        noteListView.setVisibility(View.GONE);
        progressBarRelativeLayout.setVisibility(View.VISIBLE);
    }

    //remplissage du RecyclerView après recuperation de la liste
    public void fillList(){
        //trie de la liste
        Collections.sort(notes);

        //si c'est une actualisation?
        if (na!= null)
        {
            Log.i("info", "actualisation");
            na.notifyDataSetChanged();
            return;
        }
        else
            Log.i("info", "Creation de la liste");


        na = new NoteAdapter(notes);
        noteListView.setAdapter(na);



        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        noteListView.setLayoutManager(llm);

        //affichage de la vue de details de la note après click dans la liste
        noteListView.addOnItemTouchListener(new RecyclerItemListener(getApplicationContext(), noteListView,
                new RecyclerItemListener.RecyclerTouchListener() {
                    public void onClickItem(View v, int position) {
                        Intent intent = new Intent(getApplicationContext(),NoteDetailActivity.class);
                        intent.putExtra("note",notes.get(position).getTache());
                        intent.putExtra("noteObj",notes.get(position));
                        startActivity(intent);
                    }

                    public void onLongClickItem(View v, int position) {
                        //System.out.println("On Long Click Item interface");
                    }
                }));

        //noteListView.addItemDecoration(new VerticalSpacingDecoration(64));

        //noteListView.addItemDecoration(
        //        new DividerItemDecoration(ContextCompat.getDrawable(getApplicationContext(),
        //                R.drawable.item_decorator)));


        //swipe & drap & drop
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(na);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        na.setTouchHelper(touchHelper);
        touchHelper.attachToRecyclerView(noteListView);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            addNote();
            return true;
        } else if (id == R.id.action_refresh)
        {
            setListContent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //ajout
    //a l'ajout, on donne a la vue le plus petit ordre des notes ou 1000 si n'y a pas de notes (liste vide)
    //avant ajout, il decrementeta cet ordre pour le donner a la nouvelle note créee ce qui la placera en tete de liste
    public void addNote(){
        //on recupere le plus petit ordre ou 1000 (pour decrementer par la suit)
        Integer smallestOrder=notes.size()>0?notes.get(0).getOrdre():1000;

        Intent intent = new Intent(getApplicationContext(),NewNote.class);
        intent.putExtra("typeCrud","ADD");
        intent.putExtra("smallestOrder",smallestOrder.toString());
        //intent.putExtra("noteObj",notes.get(position));
        startActivity(intent);
    }

    //Singleton qui renvoi le client retrofit parametre
    public static Retrofit getClient() {
        if (retrofit==null) {
            //OkHttpClient.Builder client = new OkHttpClient.Builder();
            OkHttpClient.Builder okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(60 * 5, TimeUnit.SECONDS)
                    .readTimeout(60 * 5, TimeUnit.SECONDS)
                    .writeTimeout(60 * 5, TimeUnit.SECONDS);
            //Si une reponse contient des cookies, on les sauvegarde
            okHttpClient.interceptors().add(new AddCookiesInterceptor());
            //on injecte les cookies sauvegardés avant envoi de la requete au serveur
            okHttpClient.interceptors().add(new ReceivedCookiesInterceptor());

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient.build())
                    //parametrage de la serialisation et deserialisation de facon automatique
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
