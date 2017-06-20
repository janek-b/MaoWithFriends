package com.janek.maowithfriends.adapter;

import android.content.Context;
import android.util.Log;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.janek.maowithfriends.R;
import com.janek.maowithfriends.model.Card;
import com.janek.maowithfriends.ui.GameActivity;

public class FirebasePlayerHandAdapter extends FirebaseRecyclerAdapter<Card, FirebaseCardViewHolder> {

    private Context context;

    public FirebasePlayerHandAdapter(Query ref, Context context) {
        super(Card.class, R.layout.player_hand_card_item, FirebaseCardViewHolder.class, ref);
        this.context = context;
    }

    @Override
    protected void populateViewHolder(FirebaseCardViewHolder viewHolder, Card model, int position) {
        viewHolder.bindCard(model);
        viewHolder.cardView.setOnClickListener(view -> {
            ((GameActivity)context).playCard(position);
        });
    }

}
