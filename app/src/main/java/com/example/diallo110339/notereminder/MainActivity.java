package com.example.diallo110339.notereminder;

import android.content.Intent;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    List<Note> notes = new ArrayList<>();
    RelativeLayout progressBarRelativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //enabling auto cookies management
        //CookieManager cookieManager = new CookieManager();
        //CookieHandler.setDefault(cookieManager);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        progressBarRelativeLayout = (RelativeLayout) findViewById(R.id.progressBarRelativeLayout);
        noteListView = (RecyclerView) findViewById(R.id.noteListView);

        //on vérifie si on arrive ici après une suppression de note
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

        /* / Getting SwipeContainerLayout
        swipeLayout = findViewById(R.id.swipeContainer);
        // Adding Listener
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code here
                //Toast.makeText(getApplicationContext(), "Works!", Toast.LENGTH_LONG).show();
                setListContent();

                // To keep animation for 4 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animation (This will be after 3 seconds)
                        swipeLayout.setRefreshing(false);
                    }
                }, 4000); // Delay in millis
            }
        });

        // Scheme colors for animation
        swipeLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.darker_gray),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)

        );
*/


    }

    private void setListContent() {

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
                        showList();
                    }

                    @Override
                    public void onFailure(Call<ListResultat> call, Throwable t) {
                        //Toast.makeText(getApplicationContext(),"Impossible de recuperer la liste des notes ",Toast.LENGTH_LONG).show();
                        Log.i("Notes","Impossible de recuperer la liste des notes  : "+t.toString() + " " + t.getStackTrace());
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
    public void showList(){
        progressBarRelativeLayout.setVisibility(View.GONE);
        noteListView.setVisibility(View.VISIBLE);
    }

    public void showLoading(){
        noteListView.setVisibility(View.GONE);
        progressBarRelativeLayout.setVisibility(View.VISIBLE);
    }

    public void fillList(){
        Collections.sort(notes);
        NoteAdapter na = new NoteAdapter(notes);
        noteListView.setAdapter(na);



        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        noteListView.setLayoutManager(llm);

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

    public void addNote(){
        //on recupere le plus petit ordre ou 1000 (pour decrementer par la suit)
        Integer smallestOrder=notes.size()>0?notes.get(0).getOrdre():1000;

        Intent intent = new Intent(getApplicationContext(),NewNote.class);
        intent.putExtra("typeCrud","ADD");
        intent.putExtra("smallestOrder",smallestOrder.toString());
        //intent.putExtra("noteObj",notes.get(position));
        startActivity(intent);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;

            } catch (Exception e) {

                Toast.makeText(getApplicationContext(), "Erreur de connexion", Toast.LENGTH_LONG);

            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("Result", result);

            super.onPostExecute(result);

            try {

                String message = "";

                JSONObject jsonObject = new JSONObject(result);

                String data = jsonObject.getString("data");

                Log.i("API content", data);

                JSONArray arr = new JSONArray(data);

                //building the notes list

                for (int i = 0; i < arr.length(); i++) {

                    JSONObject jsonPart = arr.getJSONObject(i);

                    Integer id = Integer.parseInt(jsonPart.getString("id"));
                    String user=jsonPart.getString("user");
                    String tache=jsonPart.getString("tache");
                    String echeance=jsonPart.getString("echeance");
                    int ordre=Integer.parseInt(jsonPart.getString("ordre"));
                    String couleur=jsonPart.getString("couleur");

                    if (id != null || tache != "") {
                        notes.add(new Note(id,user,tache,echeance,ordre,couleur));
                    }

                }

                if (notes.size()<1) {

                    Toast.makeText(getApplicationContext(), "Aucune tache trouvée", Toast.LENGTH_LONG).show();

                }
                else {
                    fillList();
                }


            } catch (JSONException e) {

                Toast.makeText(getApplicationContext(), "Erreur de lecture des taches", Toast.LENGTH_LONG).show();

            }



        }
    }

    public class ConnTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;

            } catch (Exception e) {

                Toast.makeText(getApplicationContext(), "Erreur de connexion ->", Toast.LENGTH_LONG);

            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                String message = "";

                JSONObject jsonObject = new JSONObject(result);

                String data = jsonObject.getString("message");


                Log.i("API Res connection", data);

                //si connexion ok
                if (data.equals("ok"))
                {
                    Log.i("API Conn OK->", data);

                    DownloadTask task = new DownloadTask();
                    task.execute("http://daviddurand.info/D228/reminder/list");
                }
                else{
                    Log.i("API Erreur", "Connexion impossible "+data);
                        Toast.makeText(getApplicationContext(), "Connexion impossible "+ data, Toast.LENGTH_LONG).show();
                }



            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Erreur pendant la connexion", Toast.LENGTH_LONG).show();
            }



        }
    }

    public static Retrofit getClient() {
        if (retrofit==null) {
            //OkHttpClient.Builder client = new OkHttpClient.Builder();
            OkHttpClient.Builder okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(60 * 5, TimeUnit.SECONDS)
                    .readTimeout(60 * 5, TimeUnit.SECONDS)
                    .writeTimeout(60 * 5, TimeUnit.SECONDS);
            okHttpClient.interceptors().add(new AddCookiesInterceptor());
            okHttpClient.interceptors().add(new ReceivedCookiesInterceptor());

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
