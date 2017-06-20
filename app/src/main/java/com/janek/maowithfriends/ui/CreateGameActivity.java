package com.janek.maowithfriends.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.rxbinding2.widget.RxAutoCompleteTextView;
import com.janek.maowithfriends.Constants;
import com.janek.maowithfriends.R;
import com.janek.maowithfriends.adapter.NewGamePlayerListAdapter;
import com.janek.maowithfriends.adapter.PlayerSearchAdapter;
import com.janek.maowithfriends.model.Game;
import com.janek.maowithfriends.model.Player;
import com.janek.maowithfriends.model.User;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.ReplaySubject;

public class CreateGameActivity extends AppCompatActivity {
    @BindView(R.id.playerSearchAutoText) AutoCompleteTextView playerSearchAutoText;
    @BindView(R.id.startGameBtn) Button startGameBtn;
    @BindView(R.id.playerListRecyclerView) RecyclerView playerListRecyclerView;

    private NewGamePlayerListAdapter playerListAdapter;
    private ReplaySubject<User> players = ReplaySubject.create();

    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private User userObject;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        ButterKnife.bind(this);

        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        playerListAdapter = new NewGamePlayerListAdapter();
        playerListRecyclerView.setAdapter(playerListAdapter);
        playerListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playerListRecyclerView.setHasFixedSize(true);

        PlayerSearchAdapter playerSearchAdapter = new PlayerSearchAdapter(this, android.R.layout.simple_list_item_1);
        DatabaseReference users = rootRef.child(Constants.FIREBASE_USER_REF);
        users.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user.getUserId().equals(currentUser.getUid())) {
                        userObject = user;
                    } else {
                        playerSearchAdapter.add(user);
                    }
                }
            }
            @Override public void onCancelled(DatabaseError databaseError) {
            }
        });

        playerSearchAutoText.setAdapter(playerSearchAdapter);
        disposable.add(RxAutoCompleteTextView.itemClickEvents(playerSearchAutoText).subscribe(view -> {
            User selectedUser = playerSearchAdapter.getItem(view.position());
            players.onNext(selectedUser);
            playerSearchAdapter.removeSelectedPlayer(selectedUser);
            playerListAdapter.updatePlayers(players.getValues(new User[]{}));
            playerSearchAutoText.setText("");
        }));

        disposable.add(players.scan(new ArrayList<User>(), (list, user) -> {
            list.add(user);
            return list;
        }).subscribe(playerList -> startGameBtn.setEnabled(playerList.size() > 1)));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    @OnClick(R.id.startGameBtn)
    public void startGame() {
        List<User> playersToInvite = new ArrayList<>();
        playersToInvite.addAll(Arrays.asList(players.getValues(new User[]{})));
        playersToInvite.add(userObject);

        String gameKey = rootRef.child(Constants.FIREBASE_GAME_REF).push().getKey();
        Game newGame = new Game(gameKey, currentUser.getUid());

        Map updates = new HashMap();
        for (User player : playersToInvite) {
            updates.put(String.format(Constants.FIREBASE_USER_GAME_REF, player.getUserId(), gameKey), true);
            newGame.addPlayer(new Player(player.getUserId(), player.getName(), player.getImageUrl()));
        }
        newGame.startGame();
        updates.put(String.format("%s/%s", Constants.FIREBASE_GAME_REF, gameKey), newGame);


        rootRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(CreateGameActivity.this, GameActivity.class);
                intent.putExtra("game", Parcels.wrap(newGame));
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
