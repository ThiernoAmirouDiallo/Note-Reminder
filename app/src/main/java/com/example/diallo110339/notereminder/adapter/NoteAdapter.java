package com.example.diallo110339.notereminder.adapter;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diallo110339.notereminder.Helper.ItemTouchHelperAdapter;
import com.example.diallo110339.notereminder.MainActivity;
import com.example.diallo110339.notereminder.R;
import com.example.diallo110339.notereminder.entity.AjoutModifResultat;
import com.example.diallo110339.notereminder.entity.ListResultat;
import com.example.diallo110339.notereminder.entity.Note;
import com.example.diallo110339.notereminder.entity.NoteToPost;
import com.example.diallo110339.notereminder.retrofitClient.MyApplication;
import com.example.diallo110339.notereminder.retrofitClient.NoteReminderClient;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//adaptateur de la liste des notes
public class NoteAdapter extends
        RecyclerView.Adapter<NoteAdapter.MyViewHolder>
        implements ItemTouchHelperAdapter{

    private List<Note> noteList;
    private Note note;
    private ItemTouchHelper touchHelper;
    public boolean delete;

    /**
     * View holder class
     * */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tacheText;
        public TextView echeanceText;
        ImageView reorderView;

        public MyViewHolder(View view) {
            super(view);
            tacheText = (TextView) view.findViewById(R.id.tacheEditText);
            echeanceText = (TextView) view.findViewById(R.id.echeanceTextView);
            reorderView = (ImageView )view.findViewById(R.id.imageViewReorder);
        }
    }

    public NoteAdapter(List<Note> notes) {
        this.noteList = notes;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Note n = noteList.get(position);
        holder.tacheText.setText(n.getTache());
        holder.echeanceText.setText(n.getEcheance());

        holder.itemView.setBackgroundColor(Color.parseColor(n.getCouleur()));

        if (n.getCouleur().equals("#000000"))
        {
            holder.tacheText.setTextColor(Color.parseColor("#FFFFFF"));
            holder.echeanceText.setTextColor(Color.parseColor("#FFFFFF"));
        }

        ((MyViewHolder) holder).reorderView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    touchHelper.startDrag(holder);
                }
                return false;
            }
        });
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
        //on switch au fur et a mesure qu'il y a deplacement.
        //si le web service implementait le switch du coté backend,
        // on aurait envoyé une seule requte de switch après le drop dans la methode onDrop ci-dessous
        switchNotes(fromPosition,toPosition);
        notifyItemMoved(fromPosition, toPosition);

        return true;
    }

    //aprsès un swap, on affiche un snackbar pour permettre au user d'annuler la suppression
    public void onItemRemove(final RecyclerView.ViewHolder viewHolder, final RecyclerView mRecyclerView, final int position){
        note = noteList.get(position);
        noteList.remove(position);
        notifyItemRemoved(position);
        //notifyDataSetChanged();
        Log.i("swap", "pos : "+position + ", notes : "+ noteList);

        //après un swap, on affiche
        delete=true;
        Snackbar snackbar = Snackbar
                .make( viewHolder.itemView , R.string.item_removed, Snackbar.LENGTH_LONG)
                //on afficher l'option annulé et s'il click, on met delete a false
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //on demande de ne pas supprimer
                        delete=false;
                    }
                })
                //si le snackbar disparait sans qu'il n'ait annulé, on envoi la requete de suppresion au serveur
                .setCallback(new Snackbar.Callback() {


                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        //send delete request to the API
                        if (delete)
                            deleteNote(position);
                        else
                            restoreItem(note,position);
                    }
                });
        snackbar.show();

    }

    //fonction qui permet de supprimer un note après un swap
    public  void deleteNote(final int position){
        NoteReminderClient client = MainActivity.getClient().create(NoteReminderClient.class);

        Call<ListResultat> call = client.removeNote(note.getId());

        call.enqueue(new Callback<ListResultat>() {
            @Override
            public void onResponse(Call<ListResultat> call, Response<ListResultat> response) {
                //si la suppression marche, on affiche un message
                ListResultat resultat = response.body();
                Toast.makeText(MyApplication.getAppContext(),"La note a été supprimée avec succès.",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ListResultat> call, Throwable t) {
                //si la suppression echoue, on restore et on affiche un message
                restoreItem(note,position);
                Log.i("Notes","Erreur pendant la suppression de la note "+t.toString() + " " + t.getStackTrace());
                Toast.makeText(MyApplication.getAppContext(),"Erreur de suppression de la note.",Toast.LENGTH_LONG).show();
            }
        });
    }

    //après un changment de position on demande de reordonner les notes
    public void switchNotes(int fromPosition, int toPosition){
        Note note1 = noteList.get(fromPosition);
        Note note2 = noteList.get(toPosition);

        int note1Odre = note1.getOrdre();
        note1.setOrdre(note2.getOrdre());
        note2.setOrdre(note1Odre);


        final NoteReminderClient client = MainActivity.getClient().create(NoteReminderClient.class);
        NoteToPost noteToPost1 =new NoteToPost();
        noteToPost1.setId(note1.getId());
        noteToPost1.setC(note1.getCouleur());
        noteToPost1.setE(note1.getEcheance());
        noteToPost1.setTexte(note1.getTache());
        noteToPost1.setO(note1.getOrdre());

        final NoteToPost noteToPost2 =new NoteToPost();
        noteToPost2.setId(note2.getId());
        noteToPost2.setC(note2.getCouleur());
        noteToPost2.setE(note2.getEcheance());
        noteToPost2.setTexte(note2.getTache());
        noteToPost2.setO(note2.getOrdre());

        Call<AjoutModifResultat> call = client.updateNote(noteToPost1.getId(),noteToPost1.getId(),noteToPost1.getTexte(),noteToPost1.getO(),noteToPost1.getC(),noteToPost1.getE());

        call.enqueue(new Callback<AjoutModifResultat>() {
            @Override
            public void onResponse(Call<AjoutModifResultat> call, Response<AjoutModifResultat> response) {
                AjoutModifResultat resultat = response.body();

                //mise a jour de la deuxième note
                Call<AjoutModifResultat> call2 = client.updateNote(noteToPost2.getId(),noteToPost2.getId(),noteToPost2.getTexte(),noteToPost2.getO(),noteToPost2.getC(),noteToPost2.getE());

                call2.enqueue(new Callback<AjoutModifResultat>() {
                    @Override
                    public void onResponse(Call<AjoutModifResultat> call, Response<AjoutModifResultat> response) {
                        AjoutModifResultat resultat = response.body();
                    }

                    @Override
                    public void onFailure(Call<AjoutModifResultat> call, Throwable t) {
                        Toast.makeText(MyApplication.getAppContext(),"Une erreur est survenue pendant la mise à jour",Toast.LENGTH_LONG).show();
                        //Log.i("Notes","Impossible de modifier la note : "+t.toString() + " " + t.getStackTrace());

                    }
                });

            }

            @Override
            public void onFailure(Call<AjoutModifResultat> call, Throwable t) {
                Toast.makeText(MyApplication.getAppContext(),"Une erreur est survenue pendant la mise à jour",Toast.LENGTH_LONG).show();
                //Log.i("Notes","Impossible de modifier la note : "+t.toString() + " " + t.getStackTrace());

            }
        });

    }


    public void onDrop(int fromPosition, int toPosition) {
        //on switch une fois à la fin
        //si le web service implementait le switch du coté backend,
        // on aurait envoyé une seule requte de switch après le drop ici
        //switchNotes(fromPosition,toPosition);
        //Toast.makeText(MyApplication.getAppContext(),"Moved from "+ fromPosition +" to "+toPosition,Toast.LENGTH_LONG).show();
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {

        this.touchHelper = touchHelper;
    }

}
