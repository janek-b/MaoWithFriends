package com.janek.maowithfriends.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.janek.maowithfriends.R;
import com.janek.maowithfriends.model.Game;
import com.janek.maowithfriends.model.Player;
import com.janek.maowithfriends.ui.GameActivity;
import com.janek.maowithfriends.util.StringUtils;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FirebaseGameListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.playerInviteNameTextView) TextView playerInviteNameTextView;
    @BindView(R.id.playerInviteLabel) TextView playerInviteLabel;
    @BindView(R.id.currentPlayerTextView) TextView currentPlayerTextView;

    private Context context;
    private Game game;
    private String uid;

    public FirebaseGameListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindGame(Game game, String uid) {
        this.game = game;
        this.uid = uid;
        if (game.getGameCreator().equals(uid)) {
            playerInviteLabel.setText(R.string.gameListPlayerListLabel);
            String playerNames = "";
            String prefix = "";
            for (Player player: game.getPlayers().values()) {
                if (!player.getUserId().equals(uid)) {
                    playerNames += prefix + StringUtils.toTitleCase(player.getName());
                    prefix = ", ";
                }
            }
            playerInviteNameTextView.setText(playerNames);

        } else {
            playerInviteLabel.setText(R.string.gameListInvitation);
            playerInviteNameTextView.setText(StringUtils.toTitleCase(game.getPlayers().get(game.getGameCreator()).getName()));
        }
        currentPlayerTextView.setText(StringUtils.toTitleCase(game.currentTurnPlayer().getName()));
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra("game", Parcels.wrap(game));
        context.startActivity(intent);
    }
}
