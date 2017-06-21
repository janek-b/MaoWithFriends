package com.janek.maowithfriends.ui;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageView;
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
import com.janek.maowithfriends.adapter.PlayerTurnAdapter;
import com.janek.maowithfriends.model.Card;
import com.janek.maowithfriends.model.Game;
import com.janek.maowithfriends.util.StringUtils;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.BehaviorSubject;

public class GameActivity extends AppCompatActivity {
    @BindView(R.id.playerHandRecycleView) RecyclerView playerHandRecyclerView;
    @BindView(R.id.playersLayout) RecyclerView playersLayout;
    @BindView(R.id.nextTurnBtn) Button nextTurnBtn;
    @BindView(R.id.cardsLeft) TextView cardsLeft;
    @BindView(R.id.cardsDiscarded) TextView cardsDiscarded;
    @BindView(R.id.discardCardImageView) ImageView discardCardImageView;


    private FirebasePlayerHandAdapter firebasePlayerHandAdapter;
    private PlayerTurnAdapter playerTurnAdapter;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser currentUser;
    private String uid;
    private DatabaseReference rootRef;
    private Game currentGame;

    private BehaviorSubject<Game> gameSubject = BehaviorSubject.create();

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        currentGame = Parcels.unwrap(getIntent().getParcelableExtra("game"));

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = this::authListen;
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebasePlayerHandAdapter.cleanup();
        disposable.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
    }

    public void authListen(FirebaseAuth firebaseAuth) {
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
            getSupportActionBar().setTitle(StringUtils.toTitleCase(currentUser.getDisplayName()));
            setUpGameState();
        }
    }

    private void setUpGameState() {
        setUpAdapters();

        rootRef.child(Constants.FIREBASE_GAMES_REF).child(currentGame.getGameId()).addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                Game firebaseGame = dataSnapshot.getValue(Game.class);
                if (firebaseGame.isGameOver()) {
                    gameSubject.onComplete();
                } else {
                    gameSubject.onNext(dataSnapshot.getValue(Game.class));
                }
            }
            @Override public void onCancelled(DatabaseError databaseError) {}
        });

        disposable.add(gameSubject.subscribeWith(new DisposableObserver<Game>() {
            @Override public void onNext(@NonNull Game game) {
                currentGame = game;
                playerTurnAdapter.updateGame(game);
                setGameState();
            }
            @Override public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }
            @Override public void onComplete() {
                quitGame();
            }
        }));
    }

    private void setUpAdapters() {
        // Player Turn Adapter
        playerTurnAdapter = new PlayerTurnAdapter(currentGame, uid);
        playersLayout.setAdapter(playerTurnAdapter);
        playersLayout.setLayoutManager(new GridLayoutManager(this, currentGame.getPlayers().size()));
        playersLayout.setHasFixedSize(true);

        // Player Hand Adapter
        Query playerHandRef = rootRef.child(String.format(Constants.FIREBASE_PLAYER_HAND_REF, currentGame.getGameId(), uid));
        firebasePlayerHandAdapter = new FirebasePlayerHandAdapter(playerHandRef, this);
        playerHandRecyclerView.setAdapter(firebasePlayerHandAdapter);
        playerHandRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        playerHandRecyclerView.setHasFixedSize(true);
    }

    private void setGameState() {
        updateDiscardCard(currentGame.topDiscardCard());
        cardsLeft.setText(String.format("Cards Left: %d", currentGame.getDeck().size()));
        cardsDiscarded.setText(String.format("Cards discarded: %d", currentGame.getDiscard().size()));
        checkRoundOver();
    }

    private void updateDiscardCard(Card discardCard) {
        int cardImageResource = getResources().getIdentifier(discardCard.cardImage(), "drawable", getPackageName());
        Picasso.with(this).load(cardImageResource).into(discardCardImageView);
    }

    private void checkRoundOver() {
        if (currentGame.roundOver()) {
            FragmentManager fm = getSupportFragmentManager();
            GameOverDialogFragment gameOverDialogFragment = new GameOverDialogFragment();
            boolean outcome = (currentGame.getPlayers().get(uid).getHand().size() == 0);
            gameOverDialogFragment.setOutcome(outcome);
            gameOverDialogFragment.show(fm, "Game Over Fragment");
        }
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

    private boolean checkPlayerTurn() {
        if (currentGame.getCurrentPlayer().equals(uid)) {
            return true;
        } else {
            Toast.makeText(this, "It is not your turn", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void endGame() {
        currentGame.endGame();
    }

    public void quitGame() {
        Intent intent = new Intent(GameActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
