package com.janek.maowithfriends.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

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
import com.janek.maowithfriends.model.User;

import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.ReplaySubject;

public class CreateGame extends AppCompatActivity {
    @BindView(R.id.playerSearchAutoText) AutoCompleteTextView playerSearchAutoText;
    @BindView(R.id.startGameBtn) Button startGameBtn;
    @BindView(R.id.playerListRecyclerView) RecyclerView playerListRecyclerView;

    private NewGamePlayerListAdapter playerListAdapter;
    private ReplaySubject<User> players = ReplaySubject.create();

    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        ButterKnife.bind(this);

        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        playerListAdapter = new NewGamePlayerListAdapter();
        playerListRecyclerView.setAdapter(playerListAdapter);
        playerListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playerListRecyclerView.setHasFixedSize(true);

        PlayerSearchAdapter playerSearchAdapter = new PlayerSearchAdapter(this, android.R.layout.simple_list_item_1);
        DatabaseReference users = rootRef.child(Constants.FIREBASE_USER_REF);
        users.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//                    User user = userSnapshot.getValue(User.class);
                    User user = User.create(userSnapshot);
                    if (!user.userId().equals(currentUser.getUid())) {
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
        User[] playerToInvite = players.getValues(new User[]{});
        
    }
}