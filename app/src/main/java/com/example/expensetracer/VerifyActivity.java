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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyActivity extends AppCompatActivity {

    Button resendCode;
    TextView login;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        mAuth = FirebaseAuth.getInstance();
        login = (TextView) findViewById(R.id.againLogIn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

//        resendCode = (Button) findViewById(R.id.resendCode);

        final FirebaseUser user = mAuth.getCurrentUser();

//
//        try {
//
//            resendCode.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(VerifyActivity.this, "Verification Email Has Been Sent.", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("tag", "onFailure: Email not sent " + e.getMessage());
//                            Toast.makeText(VerifyActivity.this, "Email Not Sent"
//                                    , Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            });
//
//        } catch (Exception err) {
//            Toast.makeText(this, "Email Already Send", Toast.LENGTH_SHORT).show();
//        }


    }
}