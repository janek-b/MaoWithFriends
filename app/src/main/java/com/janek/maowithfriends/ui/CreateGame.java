package com.janek.maowithfriends.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.janek.maowithfriends.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class CreateGame extends AppCompatActivity {
    @BindView(R.id.playerSearchAutoText) AutoCompleteTextView playerSearchAutoText;
    @BindView(R.id.startGameBtn) Button startGameBtn;
    @BindView(R.id.playerListRecyclerView) RecyclerView playerListRecyclerView;

    DatabaseReference rootRef;
    FirebaseAuth mAuth;

    CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        ButterKnife.bind(this);

        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        ArrayAdapter<User> playerSearchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        DatabaseReference users = rootRef.child(Constants.FIREBASE_USER_REF);
        users.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user.getUserId() != currentUser.getUid()) {
                        playerSearchAdapter.add(user);
                        // TODO: don't add if already in recycler view
                    }
                }
            }

            @Override public void onCancelled(DatabaseError databaseError) {
            }
        });

        playerSearchAutoText.setAdapter(playerSearchAdapter);

        disposable.add(RxAutoCompleteTextView.itemClickEvents(playerSearchAutoText).subscribe(view -> {
            Log.d("test", playerSearchAdapter.getItem(view.position()).getUserId());
        }));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
