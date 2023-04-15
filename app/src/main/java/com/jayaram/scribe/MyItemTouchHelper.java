package com.jayaram.scribe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MyItemTouchHelper extends ItemTouchHelper.Callback {

    private final ItemTouchAdapter adapter;


    public MyItemTouchHelper(ItemTouchAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        CardView  cardView= (CardView) viewHolder.itemView;
        cardView.setCardBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(),R.color.light_blue));
        adapter.OnMoveComplete();
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if(actionState==ItemTouchHelper.ACTION_STATE_DRAG)
        {
            assert viewHolder != null;
            CardView  cardView= (CardView) viewHolder.itemView;
            cardView.setCardBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(),R.color.light_grey));
        }
        if(actionState==ItemTouchHelper.ACTION_STATE_SWIPE)
        {
            adapter.isSwiped(true);
        }
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final int dragFlags= ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags=ItemTouchHelper.END;
        return makeMovementFlags(dragFlags,swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        adapter.OnMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
          adapter.OnSwiped(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        CardView cardView = (CardView) viewHolder.itemView;
        new RecyclerViewSwipeDecorator.Builder(cardView.getContext(), c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
                .addBackgroundColor(ContextCompat.getColor(cardView.getContext(),R.color.white))
                .addActionIcon(R.drawable.ic_baseline_delete_24)
                .create()
                .decorate();
    }



}
