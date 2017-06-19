package com.janek.maowithfriends.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.janek.maowithfriends.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.emailLoginEditText) EditText emailLoginEditText;
    @BindView(R.id.passwordLoginEditText) EditText passwordLoginEditText;
    @BindView(R.id.loginBtn) Button loginBtn;

    ProgressDialog loading;
    CompositeDisposable disposable = new CompositeDisposable();
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loading = new ProgressDialog(this);
        loading.setMessage("Authenticating. Please Wait...");
        loading.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = this::authListen;

        disposable.add(Observable.combineLatest(
                RxTextView.textChanges(emailLoginEditText).skipInitialValue(),
                RxTextView.textChanges(passwordLoginEditText).skipInitialValue(),
                (emailInput, passwordInput) -> validateEmail(emailInput.toString().trim()) && validatePassword(passwordInput.toString().trim()))
                .subscribe(valid -> loginBtn.setEnabled(valid)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    private boolean validateEmail(String email) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLoginEditText.setError("Please enter a valid email.");
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password) {
        if (!(password.length() > 6)) {
            passwordLoginEditText.setError("Password must be more than 6 characters in length.");
            return false;
        }
        return true;
    }

    public void authListen(FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.registerBtn)
    public void loadRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.loginBtn)
    public void login() {
        String email = emailLoginEditText.getText().toString().trim();
        String password = passwordLoginEditText.getText().toString().trim();

        loading.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            loading.dismiss();
            if (!task.isSuccessful()) {
                Toast.makeText(this, "Authentication failed, Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
