package com.janek.maowithfriends.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.janek.maowithfriends.R;
import com.janek.maowithfriends.model.Card;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FirebaseCardViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.cardSuit) TextView cardSuit;
    @BindView(R.id.cardValue) TextView cardValue;

    public View cardView;

    private Context context;

    public FirebaseCardViewHolder(View itemView) {
        super(itemView);
        cardView = itemView;
        ButterKnife.bind(this, itemView);
        this.context = itemView.getContext();
    }

    public void bindCard(Card card) {
        cardSuit.setText(card.getSuit().toString());
        cardValue.setText(card.getValue().toString());
    }
}