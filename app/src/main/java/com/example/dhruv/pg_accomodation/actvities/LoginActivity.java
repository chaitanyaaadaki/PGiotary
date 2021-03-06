package com.example.dhruv.pg_accomodation.actvities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.dhruv.pg_accomodation.Home;
import com.example.dhruv.pg_accomodation.helper_classes.PrefManager;
import com.example.dhruv.pg_accomodation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static android.view.View.*;
import static com.example.dhruv.pg_accomodation.helper_classes.ValidationUtility.isValidEmail;
import static com.example.dhruv.pg_accomodation.helper_classes.ValidationUtility.isValidPassword;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEdittext;
    private TextInputEditText passwordEdittext;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(LoginActivity.this);
        firebaseAuth = FirebaseAuth.getInstance();
        emailEdittext = findViewById(R.id.email_edittext);
        passwordEdittext = findViewById(R.id.password_edittext);

        prefManager = new PrefManager(getApplicationContext());

        MaterialTextView forgotPasswordTextView = findViewById(R.id.forgot_password_tv);
        MaterialButton btnLogin = findViewById(R.id.btn_login);
        ImageButton btnBack = findViewById(R.id.btn_back);


        //go back btn
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
            }
        });


        forgotPasswordTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                processLogin();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        emailEdittext.setError(null);
        passwordEdittext.setError(null);
    }


    private void processLogin() {

        String email = emailEdittext.getText().toString();
        String password = passwordEdittext.getText().toString();
        if (isValidEmail(email)) {
            if (isValidPassword(password)) {
                // FirebaseAuth.getInstance().signOut();
                progressDialog.setMessage("Processing...");
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {

                                prefManager.setIsLoggedIn(true);
                                prefManager.setCallerID(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                startActivity(new Intent(LoginActivity.this, Home.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Please verify your email.", Toast.LENGTH_SHORT).show();
                            }

                            //Toast.makeText(LoginActivity.this, "Login successful !", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                passwordEdittext.setError("Invalid password!");
            }
        } else {
            emailEdittext.setError("Invalid email address!");
        }

    }
}
