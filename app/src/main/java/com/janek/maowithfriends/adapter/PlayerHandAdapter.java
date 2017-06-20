package com.janek.maowithfriends.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.janek.maowithfriends.R;
import com.janek.maowithfriends.model.Card;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PlayerHandAdapter extends RecyclerView.Adapter<PlayerHandAdapter.CardViewHolder> {
    private List<Card> playerCards;

    public PlayerHandAdapter(List<Card> playerCards) {
        this.playerCards = playerCards;
    }


    @Override
    public PlayerHandAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_hand_card_item, parent, false);
        CardViewHolder viewHolder = new CardViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PlayerHandAdapter.CardViewHolder holder, int position) {
        holder.bindCard(playerCards.get(position));
    }

    @Override
    public int getItemCount() {
        return playerCards.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cardSuit) TextView cardSuit;
        @BindView(R.id.cardValue) TextView cardValue;

        private Context context;

        public CardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.context = itemView.getContext();
        }

        public void bindCard(Card card) {
            cardSuit.setText(card.getSuit().toString());
            cardValue.setText(card.getCardValue().toString());
        }
    }
}
