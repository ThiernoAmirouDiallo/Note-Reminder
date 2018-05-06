package com.example.diallo110339.notereminder.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.diallo110339.notereminder.R;
import com.example.diallo110339.notereminder.entity.Note;

import java.util.List;

public class NoteAdapter extends
        RecyclerView.Adapter<NoteAdapter.MyViewHolder> {

    private List<Note> noteList;

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
}
