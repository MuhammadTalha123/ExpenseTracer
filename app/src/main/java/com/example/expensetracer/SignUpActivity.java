package com.example.expensetracer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    TextView fullNameField, emailField, passwordField;
    Button signUpBtn, cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setTitle(R.string.app_name_signUp);
        loadAllViews();
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

    }

    public void loadAllViews() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        fullNameField = (TextView) findViewById(R.id.fullNameField);
        emailField = (TextView) findViewById(R.id.emailField);
        passwordField = (TextView) findViewById(R.id.passwordField);
        signUpBtn = (Button) findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(this);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.signUpBtn) {
            final String fullName = fullNameField.getText().toString();
            final String email = emailField.getText().toString();
            final String password = passwordField.getText().toString();
            if(email.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                Toast.makeText(SignUpActivity.this, R.string.toast_signup_empty_values, Toast.LENGTH_LONG).show();
            } else if (password.length() < 6) {
                Toast.makeText(SignUpActivity.this, R.string.toast_signup_empty_values, Toast.LENGTH_LONG).show();
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("test", "createUserWithEmail:onComplete:" + task.isSuccessful());



                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, R.string.toast_signup_error, Toast.LENGTH_LONG).show();
                                } else {
                                    FirebaseUser fUser = mAuth.getCurrentUser();
                                    fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(SignUpActivity.this, "Verification Email Has Been Sent.", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("TAG", "onFailure: Email not Sent " + e.getMessage());
                                        }
                                    });





                                    if (fUser != null) {
                                        User user = new User();
                                        String useruid = fUser.getUid();
                                        user.setId(useruid);
                                        user.setFullName(fullName);
                                        user.setEmail(email);
                                        user.setPassword(password);
                                        mDatabase.getReference().child("users").child(useruid).setValue(user);
                                        mAuth.signOut();
                                        Toast.makeText(SignUpActivity.this, R.string.toast_signup_success, Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
            }
        } else if (v.getId() == R.id.cancelBtn) {
            finish();
        }
    }
}