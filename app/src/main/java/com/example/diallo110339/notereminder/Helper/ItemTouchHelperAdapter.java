package com.example.diallo110339.notereminder.Helper;

import android.support.v7.widget.RecyclerView;

import com.example.diallo110339.notereminder.entity.Note;

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

    void restoreItem(Note note, int position);

    public void onItemRemove(final RecyclerView.ViewHolder viewHolder, final RecyclerView mRecyclerView, final int position);

    }
