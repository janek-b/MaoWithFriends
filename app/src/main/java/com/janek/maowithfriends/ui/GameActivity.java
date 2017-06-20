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
import butterknife.OnClick;

public class GameActivity extends AppCompatActivity {
    @BindView(R.id.currentTurnPlayer) TextView currentTurnPlayer;
    @BindView(R.id.playerHandRecycleView) RecyclerView playerHandRecyclerView;
    @BindView(R.id.nextTurnBtn) Button nextTurnBtn;
    @BindView(R.id.discardCardSuit) TextView discardCardSuit;
    @BindView(R.id.discardCardValue) TextView discardCardValue;
    @BindView(R.id.cardsLeft) TextView cardsLeft;
    @BindView(R.id.cardsDiscarded) TextView cardsDiscarded;

    private FirebasePlayerHandAdapter firebasePlayerHandAdapter;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String uid;
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
        uid = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        Query playerHandRef = rootRef.child(String.format(Constants.FIREBASE_PLAYER_HAND_REF, newGame.getGameId(), uid));

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
        if (checkPlayerTurn()) {
            if (currentGame.validCardInHand(uid)) {
                Card playedCard = currentGame.currentTurnPlayer().getHand().get(index);
                if (currentGame.validCard(playedCard)) {
                    currentGame.playCard(uid, index);
                } else {
                    Toast.makeText(this, "Card must be of the same suit or value", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "You have no valid cards in hand, draw a new card", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setGameState(Game game) {
        currentGame = game;
        updateTurnIndicator();
        updateDiscardCard(currentGame.topDiscardCard());
        cardsLeft.setText(String.format("Cards Left: %d", currentGame.getDeck().size()));
        cardsDiscarded.setText(String.format("Cards discarded: %d", currentGame.getDiscard().size()));
    }

    @OnClick(R.id.nextTurnBtn)
    public void nextTurn() {
        currentGame.nextTurn();
    }

    @OnClick(R.id.drawCardPile)
    public void drawCard() {
        if (checkPlayerTurn()) {
            if (currentGame.validCardInHand(uid)) {
                Toast.makeText(this, "You must play any valid card in hand before drawing", Toast.LENGTH_SHORT).show();
            } else {
                currentGame.playerDrawCard(uid);
            }
        }
    }

    private boolean checkPlayerTurn() {
        if (currentGame.getCurrentPlayer().equals(uid)) {
            return true;
        } else {
            Toast.makeText(this, "It is not your turn", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void updateTurnIndicator() {
        if (currentGame.getCurrentPlayer().equals(uid)) {
            currentTurnPlayer.setText("Your turn");
        } else {
            currentTurnPlayer.setText(currentGame.currentTurnPlayer().getName());
        }
    }

    private void updateDiscardCard(Card discardCard) {
        discardCardSuit.setText(discardCard.getSuit().toString());
        discardCardValue.setText(discardCard.getValue().toString());
    }
}
