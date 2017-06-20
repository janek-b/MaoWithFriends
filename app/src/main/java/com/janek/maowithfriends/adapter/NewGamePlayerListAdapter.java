package com.janek.maowithfriends.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.janek.maowithfriends.R;
import com.janek.maowithfriends.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewGamePlayerListAdapter extends RecyclerView.Adapter<NewGamePlayerListAdapter.PlayerViewHolder> {
    private List<User> playerList;

    public NewGamePlayerListAdapter() {
        this.playerList = new ArrayList<>();
    }

    public void updatePlayers(User[] players) {
        this.playerList.clear();
        this.playerList.addAll(Arrays.asList(players));
        notifyDataSetChanged();
    }

    @Override
    public NewGamePlayerListAdapter.PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_game_player_list_item, parent, false);
        PlayerViewHolder viewHolder = new PlayerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NewGamePlayerListAdapter.PlayerViewHolder holder, int position) {
        holder.bindPlayer(playerList.get(position));
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public class PlayerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.playerProfileImg) ImageView playerProfileImg;
        @BindView(R.id.playerNameTextView) TextView playerNameTextView;

        private Context mContext;

        public PlayerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindPlayer(User player) {
            playerNameTextView.setText(player.getName());
            Picasso.with(mContext).load(player.getImageUrl()).into(playerProfileImg);
        }
    }
}
