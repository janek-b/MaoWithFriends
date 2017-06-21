package com.janek.maowithfriends.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.janek.maowithfriends.R;
import com.janek.maowithfriends.model.Game;
import com.janek.maowithfriends.model.Player;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by janek on 6/20/17.
 */

public class FirebasePlayerTurnViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.playerNameFirstLetter) TextView playerNameFirstLetter;
    @BindView(R.id.playerTurnProgressBar) ProgressBar playerTurnProgressBar;

    private Context context;
    private Game game;

    public FirebasePlayerTurnViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = itemView.getContext();
    }

    public void bindPlayer(Player player, Game game) {
        playerNameFirstLetter.setText(Character.toString(player.getName().toUpperCase().charAt(0)));
        if (player.getUserId().equals(game.getCurrentPlayer())) {
            playerTurnProgressBar.setVisibility(View.VISIBLE);
        } else {
            playerTurnProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
