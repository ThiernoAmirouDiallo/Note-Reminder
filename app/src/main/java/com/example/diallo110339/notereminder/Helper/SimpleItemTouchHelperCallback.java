package com.example.diallo110339.notereminder.Helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

//gestion des evenements du reclycler view contenant la liste des notes
//drag&drop, swap
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    int dragFrom = -1;
    int dragTo = -1;


    private final ItemTouchHelperAdapter mAdapter;
    private RecyclerView mRecyclerView;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        this.mRecyclerView = recyclerView;

        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        if (target.getItemViewType() != target.getItemViewType()) {
            return false;
        }

        // remember FIRST from position
        if (dragFrom == -1)
            dragFrom = viewHolder.getAdapterPosition();
        dragTo = target.getAdapterPosition();


        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //before undo confirmation
        //mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        //after
        mAdapter.onItemRemove(viewHolder, mRecyclerView,viewHolder.getAdapterPosition());
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        //System.out.println( "Moved from "+ dragFrom +" to "+dragTo);

        if(dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
            mAdapter.onDrop(dragFrom, dragTo);
        }

        dragFrom = dragTo = -1;
    }



}