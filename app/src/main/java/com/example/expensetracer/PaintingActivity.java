package com.example.expensetracer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class PaintingActivity extends AppCompatActivity {


    DrawingView drawingView;
    Button saveImageBtn, image_from_gallery, camera, cancelBtn;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    DatabaseReference ref;
    DatabaseReference expenseRef;
    Storage myStore;
    String uid;
    String expenseId;
    String imageId;
    Utils myUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painting);
        camera = findViewById(R.id.camera);
        cancelBtn = findViewById(R.id.cancelBtn);
        ref = FirebaseDatabase.getInstance().getReference();
        saveImageBtn = findViewById(R.id.saveimage_btn);
        image_from_gallery = findViewById(R.id.image_from_gallery);
        drawingView = findViewById(R.id.myDrawing);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        expenseId = getIntent().getStringExtra("EXPENSE_ID");
        imageId = UUID.randomUUID().toString();
        myUtils = Utils.getInstance();
        expenseRef = ref.child("users").child(uid).child("expenses").child(expenseId);
        storageReference = FirebaseStorage.getInstance().getReference();
        myStore = Storage.getInstance();

        //cancel button
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myExpenseIntent = new Intent(getApplicationContext(), ViewExpenseActivity.class);
                Expense currentExpense = myStore.getCurrentExpense();
                myExpenseIntent.putExtra("expense", currentExpense);
                startActivity(myExpenseIntent);
            }
        });


        // open camera mobile phone

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        image_from_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);


            }
        });


        saveImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder saveDialog = new AlertDialog.Builder(PaintingActivity.this);
                saveDialog.setTitle("Save drawing");
                saveDialog.setMessage("Drawing save to device Gallery?And Also Upload On FireStorage?");
                saveDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myUtils.showLoading(PaintingActivity.this);

                        drawingView.setDrawingCacheEnabled(true);
                        String imgSaved = MediaStore.Images.Media.insertImage(
                                getContentResolver(), drawingView.getDrawingCache(),
                                imageId, "drawing");
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

                    public void uploadImageToFirebase(String imgSaved) {
                        // upload image to firebase storage
                        final StorageReference fileRef = storageReference.child(uid).child(expenseId).child(imageId);
                        fileRef.putFile(Uri.parse(imgSaved)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        expenseRef.child("images").child(imageId).setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                myUtils.hideLoading();
                                                openViewExpenseActivity();
                                                Toast.makeText(PaintingActivity.this, "Drawing Upload Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                myUtils.showLoading(PaintingActivity.this);
                Uri imageURi = data.getData();
                uploadToFirebase(imageURi);
            }
        } else if (requestCode == 0) {

            try {
                if (resultCode == Activity.RESULT_OK) {
                    myUtils.showLoading(PaintingActivity.this);
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Title", null);
                    Uri capturedImageUri = Uri.parse(path);
                    uploadToFirebaseCameraImage(capturedImageUri);
                }
            } catch (Exception err) {
                Toast.makeText(this, "Your Error" + err.toString(), Toast.LENGTH_LONG).show();
                Log.i("error", err.toString());
            }
        }


    }

    private void openViewExpenseActivity(){
        Intent myExpenseIntent = new Intent(getApplicationContext(), ViewExpenseActivity.class);
        Expense currentExpense = myStore.getCurrentExpense();
        myExpenseIntent.putExtra("expense", currentExpense);
        startActivity(myExpenseIntent);
    }

    private void uploadToFirebaseCameraImage(Uri capturedImageUri) {
        final StorageReference fileRef = storageReference.child(uid).child(expenseId).child(imageId);
        fileRef.putFile(capturedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        expenseRef.child("images").child(imageId).setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                myUtils.hideLoading();
                                openViewExpenseActivity();
                                Toast.makeText(PaintingActivity.this, "Camera Image Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PaintingActivity.this, "Image Not Uploaded", Toast.LENGTH_SHORT).show();
                myUtils.hideLoading();
            }
        });
    }


    private void uploadToFirebase(Uri imageURi) {
        final StorageReference fileRef = storageReference.child(uid).child(expenseId).child(imageId);
        fileRef.putFile(imageURi).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        expenseRef.child("images").child(imageId).setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                myUtils.hideLoading();
                                openViewExpenseActivity();
                                Toast.makeText(PaintingActivity.this, "Gallery Image Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PaintingActivity.this, "Image Not Uploaded", Toast.LENGTH_SHORT).show();
                myUtils.hideLoading();
            }
        });


    }


}