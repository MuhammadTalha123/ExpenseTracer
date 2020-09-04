package com.example.expensetracer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.UUID;

public class PaintingActivity extends AppCompatActivity {


    DrawingView drawingView;
    Button saveImageBtn;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    DatabaseReference ref;
    DatabaseReference expenseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painting);


        ref = FirebaseDatabase.getInstance().getReference();
        saveImageBtn = findViewById(R.id.saveimage_btn);
        drawingView = findViewById(R.id.myDrawing);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final String uid = mAuth.getCurrentUser().getUid();
        final String expenseId = getIntent().getStringExtra("EXPENSE_ID");
        final String imageId = UUID.randomUUID().toString();;
        expenseRef = ref.child("users").child(uid).child("expenses").child(expenseId);
        storageReference = FirebaseStorage.getInstance().getReference();



        saveImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder saveDialog = new AlertDialog.Builder(PaintingActivity.this);
                saveDialog.setTitle("Save drawing");
                saveDialog.setMessage("Save drawing to device Gallery?And Also Upload On FireStorage?");
                saveDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        drawingView.setDrawingCacheEnabled(true);
                        String imgSaved = MediaStore.Images.Media.insertImage(
                                getContentResolver(), drawingView.getDrawingCache(),
                                imageId + ".png", "drawing");

                        if (imgSaved != null) {
                            Toast savedToast = Toast.makeText(getApplicationContext(),
                                    "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                            savedToast.show();
                        } else {
                            Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                    "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                            unsavedToast.show();
                        }
                        // Destroy the current cache.
                        drawingView.destroyDrawingCache();

                        uploadImageToFirebase(imgSaved);

                    }

                    private void uploadImageToFirebase(String imgSaved) {


                        // upload image to firebase storage
                        final StorageReference fileRef = storageReference.child(uid).child(expenseId).child(imageId);
                        fileRef.putFile(Uri.parse(imgSaved)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(PaintingActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();

                                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Toast.makeText(PaintingActivity.this, uri.toString(), Toast.LENGTH_LONG).show();
                                        Log.i("imageUri", uri.toString());
                                        expenseRef.child("images").child(imageId).setValue(uri.toString());
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PaintingActivity.this, "Image Not Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });

                saveDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                saveDialog.show();

            }
        });


    }


    public void clearCanvas(View v) {
        drawingView.clearCanvas();
    }




}