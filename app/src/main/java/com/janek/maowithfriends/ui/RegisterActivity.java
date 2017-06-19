package com.janek.maowithfriends.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.janek.maowithfriends.R;

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

    CompositeDisposable disposable = new CompositeDisposable();
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        disposable.add(Observable.combineLatest(
                RxTextView.textChanges(nameEditText).skipInitialValue(),
                RxTextView.textChanges(emailRegisterEditText).skipInitialValue(),
                RxTextView.textChanges(passwordRegisterEditText).skipInitialValue(),
                RxTextView.textChanges(confirmRegisterEditText).skipInitialValue(),
                (CharSequence nameInput, CharSequence emailInput, CharSequence passwordInput, CharSequence confirmInput) -> {
                    boolean validName = validateName(nameInput);
                    boolean validEmail = validateEmail(emailInput);
                    boolean validPassword = validatePassword(passwordInput, confirmInput);
                    return validName && validEmail && validPassword;
                }).subscribe(valid -> registerBtn.setEnabled(valid)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    private boolean validateName(CharSequence name) {
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Please enter a name.");
            return false;
        }
        return true;
    }

    private boolean validateEmail(CharSequence email) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailRegisterEditText.setError("Please enter a valid email address.");
            return false;
        }
        return true;
    }

    private boolean validatePassword(CharSequence password, CharSequence confirm) {
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
        Log.d("RegisterActivity", "register new account");
    }
}
