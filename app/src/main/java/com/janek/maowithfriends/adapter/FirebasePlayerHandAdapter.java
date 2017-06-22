package com.janek.maowithfriends.adapter;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.janek.maowithfriends.R;
import com.janek.maowithfriends.model.Card;
import com.janek.maowithfriends.ui.GameActivity;
import com.janek.maowithfriends.util.ItemTouchHelperAdapter;
import com.janek.maowithfriends.util.OnStartDragListener;

public class FirebasePlayerHandAdapter extends FirebaseRecyclerAdapter<Card, FirebaseCardViewHolder> implements ItemTouchHelperAdapter{
    private OnStartDragListener onStartDragListener;
    private Context context;
    private RecyclerView recyclerView;
    private int previousCont;

    public FirebasePlayerHandAdapter(Query ref, Context context, OnStartDragListener onStartDragListener) {
        super(Card.class, R.layout.player_hand_card_item, FirebaseCardViewHolder.class, ref);
        this.context = context;
        this.onStartDragListener = onStartDragListener;
        this.previousCont = getItemCount();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    protected void onDataChanged() {
        super.onDataChanged();
        if (getItemCount() > previousCont) {
            recyclerView.smoothScrollToPosition(getItemCount());
        }
        previousCont = getItemCount();
    }



    @Override
    protected void populateViewHolder(FirebaseCardViewHolder viewHolder, Card model, int position) {
        viewHolder.bindCard(model);

        viewHolder.cardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    onStartDragListener.onStartDrag(viewHolder);
                }
                return false;
            }
        });

        viewHolder.cardView.setOnClickListener(view -> {
            resetCards();
            if (view.getY() == 0f) {
                view.animate().translationY(-30f).setDuration(200);
            }
//            ((GameActivity)context).playCard(position);
        });

        viewHolder.cardView.setOnLongClickListener(view -> {
            ((GameActivity)context).playCard(position);

//            resetCards();
//            view.animate().translationY(-30f).setDuration(200);
            return false;
        });
    }

    private void resetCards() {
        for (int i=0; i < getItemCount(); i++) {
            resetCard(recyclerView.findViewHolderForAdapterPosition(i));
        }
    }

    private void resetCard(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.animate().translationY(0f);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

}
