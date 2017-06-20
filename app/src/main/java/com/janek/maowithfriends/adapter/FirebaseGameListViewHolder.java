package com.janek.maowithfriends.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.janek.maowithfriends.R;
import com.janek.maowithfriends.model.Game;
import com.janek.maowithfriends.ui.GameActivity;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FirebaseGameListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.gameIdTextView) TextView gameIdTextView;
    @BindView(R.id.currentPlayerTextView) TextView currentPlayerTextView;

    private Context context;
    private Game game;

    public FirebaseGameListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindGame(Game game) {
        this.game = game;
        gameIdTextView.setText(game.getGameId());
        currentPlayerTextView.setText(game.getPlayers().get(game.getCurrentPlayer()).getName());
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra("game", Parcels.wrap(game));
        context.startActivity(intent);
    }
}
