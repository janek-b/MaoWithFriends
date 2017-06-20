package com.janek.maowithfriends.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.janek.maowithfriends.R;
import com.janek.maowithfriends.adapter.PlayerHandAdapter;
import com.janek.maowithfriends.model.Game;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GameActivity extends AppCompatActivity {
    @BindView(R.id.currentTurnPlayer) TextView currentTurnPlayer;
    @BindView(R.id.playerHandRecycleView) RecyclerView playerHandRecyclerView;

    PlayerHandAdapter playerHandAdapter;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        Game newGame = Parcels.unwrap(getIntent().getParcelableExtra("game"));

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();

        playerHandAdapter = new PlayerHandAdapter(newGame.getPlayers().get(currentUser.getUid()).getHand());
        playerHandRecyclerView.setAdapter(playerHandAdapter);
        playerHandRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        playerHandRecyclerView.setHasFixedSize(true);
    }

    private void setGameState(Game game) {

    }
}
