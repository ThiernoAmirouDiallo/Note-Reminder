package com.example.diallo110339.notereminder.adapter;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diallo110339.notereminder.Helper.ItemTouchHelperAdapter;
import com.example.diallo110339.notereminder.MainActivity;
import com.example.diallo110339.notereminder.R;
import com.example.diallo110339.notereminder.entity.ListResultat;
import com.example.diallo110339.notereminder.entity.Note;
import com.example.diallo110339.notereminder.retrofitClient.MyApplication;
import com.example.diallo110339.notereminder.retrofitClient.NoteReminderClient;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteAdapter extends
        RecyclerView.Adapter<NoteAdapter.MyViewHolder>
        implements ItemTouchHelperAdapter{

    private List<Note> noteList;
    private Note note;

    /**
     * View holder class
     * */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tacheText;
        public TextView echeanceText;

        public MyViewHolder(View view) {
            super(view);
            tacheText = (TextView) view.findViewById(R.id.tacheTextView);
            echeanceText = (TextView) view.findViewById(R.id.echeanceTextView);
        }
    }

    public NoteAdapter(List<Note> notes) {
        this.noteList = notes;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Note n = noteList.get(position);
        holder.tacheText.setText(n.getTache());
        holder.echeanceText.setText(n.getEcheance());

        holder.itemView.setBackgroundColor(Color.parseColor(n.getCouleur()));
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.noterow,parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onItemDismiss(int position) {
        //remove the element
        note = noteList.get(position);
        noteList.remove(position);
        notifyItemRemoved(position);
        deleteNote(position);
    }

    //pour le undo
    public void restoreItem(Note note, int position) {
        this.noteList.add(position, note);
        notifyItemInserted(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //swap the elements
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(noteList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(noteList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public void onItemRemove(final RecyclerView.ViewHolder viewHolder, final RecyclerView mRecyclerView, final int position){
        note = noteList.get(position);
        noteList.remove(position);
        notifyItemRemoved(position);

        Snackbar snackbar = Snackbar
                .make(mRecyclerView , R.string.item_removed, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        restoreItem(note,position);
                    }
                })
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        //send delete request to the API
                        deleteNote(position);
                    }
                });
        snackbar.show();

    }
    public  void deleteNote(final int position){
        NoteReminderClient client = MainActivity.getClient().create(NoteReminderClient.class);

        Call<ListResultat> call = client.removeNote(note.getId());

        call.enqueue(new Callback<ListResultat>() {
            @Override
            public void onResponse(Call<ListResultat> call, Response<ListResultat> response) {
                ListResultat resultat = response.body();
            }

            @Override
            public void onFailure(Call<ListResultat> call, Throwable t) {
                restoreItem(note,position);
                Toast.makeText(MyApplication.getAppContext(),"Erreur de suppression de la note.",Toast.LENGTH_LONG).show();
            }
        });
    }
}
