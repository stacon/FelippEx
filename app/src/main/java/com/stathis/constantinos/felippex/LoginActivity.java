package com.stathis.constantinos.felippex;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private final String APP_TAG = "FelippEx";
    private EditText mEmailView;
    private EditText mPasswordView;
    private Button mLoginButton;
    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        Log.d(APP_TAG,"LoginActivity started");
    }

    // Init view elements and hook with vars
    private void init() {
        mEmailView = findViewById(R.id.login_email_input);
        mPasswordView = findViewById(R.id.login_password_input);
        mLoginButton = findViewById(R.id.login_button);

        mProgressBar = findViewById(R.id.loginProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    // Login button onClick action
    public void signIn(View v) {
        Log.d(APP_TAG, "Attempting to login");
        attemptLogin();
    }

    // Parses entered info and attempts login throught FirebaseAuth
    private void attemptLogin() {

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (!CodeHelper.emailAndPasswordValid(email, password, LoginActivity.this)) { return; }

        Toast.makeText(this, R.string.login_in_progress_toast, Toast.LENGTH_SHORT).show();
        disableUI();
        mProgressBar.setVisibility(View.VISIBLE);

        // Use FirebaseAuth to sign in with email & password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                Log.d(APP_TAG, "signInWithEmail() onComplete: " + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.e(APP_TAG, "Problem signing in: " + task.getException());
                    try {
                        CodeHelper.showErrorDialog(LoginActivity.this, task.getException().getMessage());
                    } catch (NullPointerException e) {
                        CodeHelper.showErrorDialog(LoginActivity.this, getResources().getString(R.string.problem_sign_in_dialog));
                    }
                    mProgressBar.setVisibility(View.INVISIBLE);
                    enableUI();
                } else {
                    Log.d(APP_TAG, "Signing was successful!");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    Log.d(APP_TAG, "Leaving Login activity");
                    mProgressBar.setVisibility(View.INVISIBLE);
                    leaveActivity();
                    startActivity(intent);
                }
            }
        });
    }

    private void disableUI(){
        mEmailView.setVisibility(View.INVISIBLE);
        mPasswordView.setVisibility(View.INVISIBLE);
        mLoginButton.setVisibility(View.INVISIBLE);
    }

    private void enableUI(){
        mEmailView.setVisibility(View.VISIBLE);
        mPasswordView.setVisibility(View.VISIBLE);
        mLoginButton.setVisibility(View.VISIBLE);
    }

    private void leaveActivity() {
        Log.d(APP_TAG, "Leaving loginActivity");
        finish();
    }
}
