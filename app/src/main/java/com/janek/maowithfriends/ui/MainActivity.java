package com.janek.maowithfriends.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.janek.maowithfriends.Constants;
import com.janek.maowithfriends.R;
import com.janek.maowithfriends.adapter.FirebaseGameListViewHolder;
import com.janek.maowithfriends.model.Game;
import com.janek.maowithfriends.util.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.gameListRecyclerView) RecyclerView gameListRecyclerView;

    private FirebaseIndexRecyclerAdapter mFirebaseAdapter;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef;
    private Query playerGameListRef;
    private Query gameRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = this::authListen;
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirebaseAdapter.cleanup();
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
            getSupportActionBar().setTitle("Welcome " + StringUtils.toTitleCase(currentUser.getDisplayName()));
            playerGameListRef = rootRef.child(String.format(Constants.FIREBASE_USER_ALL_GAMES_REF, currentUser.getUid())).orderByValue().equalTo(true);
            gameRef = rootRef.child(Constants.FIREBASE_GAMES_REF);
            setUpGameList();
        }
    }

    private void setUpGameList() {
        mFirebaseAdapter = new FirebaseIndexRecyclerAdapter<Game, FirebaseGameListViewHolder>(Game.class, R.layout.game_list_item, FirebaseGameListViewHolder.class, playerGameListRef, gameRef) {
            @Override
            protected void populateViewHolder(FirebaseGameListViewHolder viewHolder, Game model, int position) {
                viewHolder.bindGame(model);
            }
        };
        gameListRecyclerView.setHasFixedSize(true);
        gameListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        gameListRecyclerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.newGameBtn)
    public void newGame() {
        Intent intent = new Intent(MainActivity.this, CreateGameActivity.class);
        startActivity(intent);
    }
}
