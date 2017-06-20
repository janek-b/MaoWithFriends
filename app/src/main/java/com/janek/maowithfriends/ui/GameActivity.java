package com.janek.maowithfriends.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.janek.maowithfriends.Constants;
import com.janek.maowithfriends.R;
import com.janek.maowithfriends.adapter.PlayerHandAdapter;
import com.janek.maowithfriends.model.Game;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameActivity extends AppCompatActivity {
    @BindView(R.id.currentTurnPlayer) TextView currentTurnPlayer;
    @BindView(R.id.playerHandRecycleView) RecyclerView playerHandRecyclerView;
    @BindView(R.id.nextTurnBtn) Button nextTurnBtn;

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

        setGameState(newGame);

        rootRef.child(Constants.FIREBASE_GAME_REF).child(newGame.getGameId()).addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                Game game = dataSnapshot.getValue(Game.class);
                setGameState(game);
            }

            @Override public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void setGameState(Game game) {
        currentTurnPlayer.setText(game.getPlayers().get(game.getCurrentPlayer()).getName());

        nextTurnBtn.setOnClickListener(view -> {
            game.nextTurn();
            updateFirebase(game);
        });
    }

    private void updateFirebase(Game game) {
        rootRef.child(Constants.FIREBASE_GAME_REF).child(game.getGameId()).setValue(game).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Your turn is over", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
