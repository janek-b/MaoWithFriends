package com.janek.maowithfriends.adapter;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.janek.maowithfriends.R;
import com.janek.maowithfriends.model.Card;
import com.janek.maowithfriends.ui.GameActivity;

public class FirebasePlayerHandAdapter extends FirebaseRecyclerAdapter<Card, FirebaseCardViewHolder> {

    private Context context;
    private RecyclerView recyclerView;
    private int previousCont;

    public FirebasePlayerHandAdapter(Query ref, Context context) {
        super(Card.class, R.layout.player_hand_card_item, FirebaseCardViewHolder.class, ref);
        this.context = context;
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
        viewHolder.itemView.setOnLongClickListener(v -> {
            v.setVisibility(View.GONE);
            ClipData dragData = ClipData.newPlainText("position", Integer.toString(viewHolder.getAdapterPosition()));
            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                v.startDragAndDrop(dragData, myShadow, v, 0);
            } else {
                v.startDrag(dragData, myShadow, v, 0);
            }
            return false;
        });
    }

    public void resetVisibility() {
        int total = getItemCount();
        for (int i = 0; i < total; i++) {
            recyclerView.findViewHolderForAdapterPosition(i).itemView.setVisibility(View.VISIBLE);
        }
    }
}
