package com.example.diallo110339.notereminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.diallo110339.notereminder.Helper.SimpleItemTouchHelperCallback;
import com.example.diallo110339.notereminder.adapter.DividerItemDecoration;
import com.example.diallo110339.notereminder.adapter.NoteAdapter;
import com.example.diallo110339.notereminder.adapter.RecyclerItemListener;
import com.example.diallo110339.notereminder.adapter.VerticalSpacingDecoration;
import com.example.diallo110339.notereminder.entity.Note;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView noteListView;

    ArrayList<Note> notes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //on vérifie si on arrive ici après une suppression de note
        Intent intent =getIntent();
        String lastActionType=intent.getStringExtra("actionType");
        if (lastActionType !=null && lastActionType.equals("deletion"))
            Toast.makeText(this,"Note supprimée avec succès",Toast.LENGTH_LONG).show();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
               //         .setAction("Action", null).show();
                addNote();
            }
        });

        noteListView = (RecyclerView) findViewById(R.id.noteListView);

        notes.add(new Note("Note 1","04/05/2018"));
        notes.add(new Note("Note 2","12/05/2018"));
        notes.add(new Note("Note 3","17/05/2018"));
        notes.add(new Note("Note 4","23/05/2018"));
        notes.add(new Note("Note 5","27/05/2018"));
        notes.add(new Note("Note 5","27/05/2018"));
        notes.add(new Note("Note 5","27/05/2018"));
        notes.add(new Note("Note 5","27/05/2018"));
        notes.add(new Note("Note 5","27/05/2018"));
        notes.add(new Note("Note 5","27/05/2018"));
        notes.add(new Note("Note 5","27/05/2018"));
        notes.add(new Note("Note 5","27/05/2018"));
        notes.add(new Note("Note 5","27/05/2018"));
        notes.add(new Note("Note 5","27/05/2018"));

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
                        startActivity(intent);
                    }

                    public void onLongClickItem(View v, int position) {
                        //System.out.println("On Long Click Item interface");
                    }
                }));

        noteListView.addItemDecoration(new VerticalSpacingDecoration(64));

        noteListView.addItemDecoration(
                new DividerItemDecoration(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.item_decorator)));


        //swipe & drap & drop
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(na);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void addNote(){
        Intent intent = new Intent(getApplicationContext(),NewNote.class);
        startActivity(intent);
    }
}
