package com.janek.maowithfriends.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.janek.maowithfriends.Constants;
import com.janek.maowithfriends.R;
import com.janek.maowithfriends.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.nameEditText) EditText nameEditText;
    @BindView(R.id.emailRegisterEditText) EditText emailRegisterEditText;
    @BindView(R.id.passwordRegisterEditText) EditText passwordRegisterEditText;
    @BindView(R.id.confirmRegisterEditText) EditText confirmRegisterEditText;
    @BindView(R.id.registerBtn) Button registerBtn;

    ProgressDialog loading;
    CompositeDisposable disposable = new CompositeDisposable();
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        loading = new ProgressDialog(this);
        loading.setMessage("Creating Account. Please Wait...");
        loading.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();

        disposable.add(Observable.combineLatest(
                RxTextView.textChanges(nameEditText).skipInitialValue(),
                RxTextView.textChanges(emailRegisterEditText).skipInitialValue(),
                RxTextView.textChanges(passwordRegisterEditText).skipInitialValue(),
                RxTextView.textChanges(confirmRegisterEditText).skipInitialValue(),
                (nameInput, emailInput, passwordInput, confirmInput) -> {
                    boolean validName = validateName(nameInput.toString().trim());
                    boolean validEmail = validateEmail(emailInput.toString().trim());
                    boolean validPassword = validatePassword(passwordInput.toString().trim(), confirmInput.toString().trim());
                    return validName && validEmail && validPassword;
                }).subscribe(valid -> registerBtn.setEnabled(valid)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    private boolean validateName(String name) {
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Please enter a name.");
            return false;
        }
        return true;
    }

    private boolean validateEmail(String email) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailRegisterEditText.setError("Please enter a valid email address.");
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password, String confirm) {
        if (!(password.length() > 6)) {
            passwordRegisterEditText.setError("Password must be more than 6 characters in length.");
            return false;
        } else if (!password.equals(confirm)) {
            passwordRegisterEditText.setError("Passwords do not match.");
            return false;
        } else {
            passwordRegisterEditText.setError(null);
        }
        return true;
    }

    @OnClick(R.id.loginBtn)
    public void loadLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.registerBtn)
    public void register() {
        String name = nameEditText.getText().toString().trim();
        String email = emailRegisterEditText.getText().toString().trim();
        String password = passwordRegisterEditText.getText().toString().trim();

        loading.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateProfile(task.getResult().getUser(), name);
            } else {
                Toast.makeText(this, "Registration failed, please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateProfile(FirebaseUser user, String name) {
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name).build();

        user.updateProfile(profileUpdate).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User newUser = new User(user.getUid(), user.getDisplayName(), Constants.DEFAULT_USER_IMG);
                DatabaseReference newUserRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USER_REF).child(user.getUid());

                newUserRef.setValue(newUser).addOnCompleteListener(saveTask -> {
                    if (saveTask.isSuccessful()) {
                        loading.dismiss();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }
}
