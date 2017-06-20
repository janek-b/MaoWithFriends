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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.janek.maowithfriends.Constants;
import com.janek.maowithfriends.R;
import com.janek.maowithfriends.adapter.FirebasePlayerHandAdapter;
import com.janek.maowithfriends.model.Card;
import com.janek.maowithfriends.model.Game;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GameActivity extends AppCompatActivity {
    @BindView(R.id.currentTurnPlayer) TextView currentTurnPlayer;
    @BindView(R.id.playerHandRecycleView) RecyclerView playerHandRecyclerView;
    @BindView(R.id.nextTurnBtn) Button nextTurnBtn;
    @BindView(R.id.discardCardSuit) TextView discardCardSuit;
    @BindView(R.id.discardCardValue) TextView discardCardValue;

    private FirebasePlayerHandAdapter firebasePlayerHandAdapter;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef;
    private Game currentGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        Game newGame = Parcels.unwrap(getIntent().getParcelableExtra("game"));

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        Query playerHandRef = rootRef.child(String.format(Constants.FIREBASE_PLAYER_HAND_REF, newGame.getGameId(), currentUser.getUid()));

        firebasePlayerHandAdapter = new FirebasePlayerHandAdapter(playerHandRef, this);
        playerHandRecyclerView.setAdapter(firebasePlayerHandAdapter);
        playerHandRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        playerHandRecyclerView.setHasFixedSize(true);

        setGameState(newGame);

        rootRef.child(Constants.FIREBASE_GAME_REF).child(newGame.getGameId()).addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                Game game = dataSnapshot.getValue(Game.class);
                setGameState(game);
            }
            @Override public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebasePlayerHandAdapter.cleanup();
    }

    public void playCard(int index) {
        if (currentGame.getCurrentPlayer().equals(currentUser.getUid())) {
            currentGame.playCard(currentUser.getUid(), index);
        } else {
            Toast.makeText(this, "It is not your turn", Toast.LENGTH_SHORT).show();
        }
    }

    private void setGameState(Game game) {
        currentGame = game;
        currentTurnPlayer.setText(currentGame.currentTurnPlayer().getName());
        updateDiscardCard(currentGame.topDiscardCard());

        nextTurnBtn.setOnClickListener(view -> {
            currentGame.nextTurn();
        });
    }

    private void updateDiscardCard(Card discardCard) {
        discardCardSuit.setText(discardCard.getSuit().toString());
        discardCardValue.setText(discardCard.getCardValue().toString());
    }
}
