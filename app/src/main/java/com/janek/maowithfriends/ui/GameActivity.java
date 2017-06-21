package com.janek.maowithfriends.ui;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.janek.maowithfriends.adapter.FirebasePlayerTurnViewHolder;
import com.janek.maowithfriends.model.Card;
import com.janek.maowithfriends.model.Game;
import com.janek.maowithfriends.model.Player;

import org.parceler.Parcels;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.subjects.ReplaySubject;

public class GameActivity extends AppCompatActivity {
    @BindView(R.id.currentTurnPlayer) TextView currentTurnPlayer;
    @BindView(R.id.playerHandRecycleView) RecyclerView playerHandRecyclerView;
    @BindView(R.id.nextTurnBtn) Button nextTurnBtn;
    @BindView(R.id.discardCardSuit) TextView discardCardSuit;
    @BindView(R.id.discardCardValue) TextView discardCardValue;
    @BindView(R.id.cardsLeft) TextView cardsLeft;
    @BindView(R.id.cardsDiscarded) TextView cardsDiscarded;
    @BindView(R.id.playersLayout) RecyclerView playersLayout;

    private FirebasePlayerHandAdapter firebasePlayerHandAdapter;
    private FirebaseRecyclerAdapter firebaseListAdapter;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String uid;
    private DatabaseReference rootRef;
    private Game currentGame;

    private ReplaySubject gameSubject = ReplaySubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        currentGame = Parcels.unwrap(getIntent().getParcelableExtra("game"));

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        Query playerHandRef = rootRef.child(String.format(Constants.FIREBASE_PLAYER_HAND_REF, currentGame.getGameId(), uid));
        DatabaseReference playerRef = rootRef.child(Constants.FIREBASE_GAME_REF).child(currentGame.getGameId()).child("players");

        firebaseListAdapter = new FirebaseRecyclerAdapter<Player, FirebasePlayerTurnViewHolder>(Player.class, R.layout.player_turn_indicator,FirebasePlayerTurnViewHolder.class, playerRef) {
            @Override
            protected void populateViewHolder(FirebasePlayerTurnViewHolder viewHolder, Player model, int position) {
                gameSubject.subscribe(game -> viewHolder.bindPlayer(model, (Game) game));
            }
        };
        playersLayout.setAdapter(firebaseListAdapter);
        playersLayout.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        playersLayout.setHasFixedSize(true);

        firebasePlayerHandAdapter = new FirebasePlayerHandAdapter(playerHandRef, this);
        playerHandRecyclerView.setAdapter(firebasePlayerHandAdapter);
        playerHandRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        playerHandRecyclerView.setHasFixedSize(true);

        setGameState();

        rootRef.child(Constants.FIREBASE_GAME_REF).child(currentGame.getGameId()).addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                Game game = dataSnapshot.getValue(Game.class);
                currentGame = game;
                gameSubject.onNext(currentGame);
                setGameState();
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

    private void setGameState() {
        updateTurnIndicator();
        updateDiscardCard(currentGame.topDiscardCard());
        cardsLeft.setText(String.format("Cards Left: %d", currentGame.getDeck().size()));
        cardsDiscarded.setText(String.format("Cards discarded: %d", currentGame.getDiscard().size()));
        checkEndGame();
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

    private void checkEndGame() {
        if (currentGame.gameOver()) {
            FragmentManager fm = getSupportFragmentManager();
            GameOverDialogFragment gameOverDialogFragment = new GameOverDialogFragment();
            boolean outcome = (currentGame.getPlayers().get(uid).getHand().size() == 0);
            gameOverDialogFragment.setOutcome(outcome);
            gameOverDialogFragment.show(fm, "Game Over Fragment");
        }
    }
}
