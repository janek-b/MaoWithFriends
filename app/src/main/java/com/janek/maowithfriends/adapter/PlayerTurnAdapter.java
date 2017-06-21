package com.janek.maowithfriends.adapter;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.janek.maowithfriends.R;
import com.janek.maowithfriends.model.Game;
import com.janek.maowithfriends.model.Player;
import com.janek.maowithfriends.util.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by janek on 6/20/17.
 */

public class PlayerTurnAdapter extends RecyclerView.Adapter<PlayerTurnAdapter.PlayerTurnViewHolder> {
    private Game game;
    private String[] playerIds;
    private String uid;

    public PlayerTurnAdapter(Game game, String uid) {
        this.game = game;
        this.uid = uid;
        this.playerIds = game.getPlayers().keySet().toArray(new String[]{});
    }

    public void updateGame(Game game) {
        this.game = game;
        notifyDataSetChanged();
    }

    @Override
    public PlayerTurnViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_turn_indicator, parent, false);
        PlayerTurnViewHolder viewHolder = new PlayerTurnViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PlayerTurnViewHolder holder, int position) {
        holder.bindPlayer(game, game.getPlayers().get(playerIds[position]), uid);
    }

    @Override
    public int getItemCount() {
        return game.getPlayers().values().size();
    }

    public class PlayerTurnViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.playerNameFirstLetter) TextView playerNameFirstLetter;
        @BindView(R.id.playerNameTextView) TextView playerNameTextView;
        @BindView(R.id.playerTurnIcon) CardView playerTurnIcon;

        private Context context;
        private int colorPrimaryDark;
        private int colorAccent;

        public PlayerTurnViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
            colorPrimaryDark = ContextCompat.getColor(context, R.color.colorPrimaryDark);
            colorAccent = ContextCompat.getColor(context, R.color.colorAccent);
        }

        public void bindPlayer(Game game, Player player, String uid) {
            playerNameFirstLetter.setText(Character.toString(player.getName().toUpperCase().charAt(0)));
            if (player.getUserId().equals(uid)) {
                playerNameTextView.setText("You");
            } else {
                playerNameTextView.setText(StringUtils.toTitleCase(player.getName()));
            }
            if (player.getUserId().equals(game.getCurrentPlayer())) {
                ObjectAnimator animator = ObjectAnimator.ofInt(playerTurnIcon, "cardBackgroundColor", colorPrimaryDark, colorAccent).setDuration(500);
                animator.setEvaluator(new ArgbEvaluator());
                animator.start();
                playerTurnIcon.animate().scaleX(1.3f).scaleY(1.3f).alpha(0.7f).setDuration(500);
            } else {
                ObjectAnimator animator = ObjectAnimator.ofInt(playerTurnIcon, "cardBackgroundColor", colorAccent, colorPrimaryDark).setDuration(500);
                animator.setEvaluator(new ArgbEvaluator());
                animator.start();
                playerTurnIcon.animate().scaleX(1f).scaleY(1f).alpha(1f);
            }
        }
    }

}
