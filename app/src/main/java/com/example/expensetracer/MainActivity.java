package com.example.expensetracer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView emailField, passwordField, forgotTextLink;
    Button loginBtn, signBtn;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forgotTextLink = (TextView) findViewById(R.id.forgotTextLink);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        try {
            if (ni.isConnected()) {
                if (mAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(MainActivity.this, ExpenseActivity.class);
                    startActivity(intent);
                }
                loadAllViews();
                FirebaseApp.initializeApp(this);
            } else {
                Toast.makeText(MainActivity.this, R.string.toast_no_internet, Toast.LENGTH_LONG).show();
            }
        } catch (Exception err) {
            Log.i("Error in network", err.toString());
        }


        forgotTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText resetMail = new EditText(view.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Reset Password");
                passwordResetDialog.setMessage("Enter your Email to Received Reset Password Link");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String mail = resetMail.getText().toString();
                        mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Password Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error ! Reset Link Is Not Sent " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                passwordResetDialog.create().show();

            }
        });


    }

    public void loadAllViews() {
        emailField = (TextView) findViewById(R.id.emailField);
        passwordField = (TextView) findViewById(R.id.passwordField);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
        signBtn = (Button) findViewById(R.id.signUpBtn);
        signBtn.setOnClickListener(this);
        progressBar = findViewById(R.id.progress_circular);


    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.loginBtn) {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, R.string.toast_empty_values, Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMax(100);
                // Setting Title
                progressDialog.setTitle("ProgressDialog");
                // Setting Message
                progressDialog.setMessage("Loading...");
                // Progress Dialog Style Horizontal
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                // Progress Dialog Style Spinner
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("test", "signInWithEmail:onComplete:" + task.isSuccessful());
                                if (!task.isSuccessful()) {
                                    Log.w("test", "signInWithEmail:failed", task.getException());
                                    Toast.makeText(MainActivity.this, R.string.toast_loginFailed, Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    progressDialog.dismiss();
                                } else {
                                    Intent intent = new Intent(MainActivity.this, ExpenseActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
            }
        } else if (v.getId() == R.id.signUpBtn) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }

    }

}