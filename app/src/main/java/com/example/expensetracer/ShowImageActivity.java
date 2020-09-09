package com.example.expensetracer;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ShowImageActivity extends AppCompatActivity {
    ImageView show_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        try {
            Bundle bundle = getIntent().getExtras();
            String imageUrl = bundle.get("imageUri").toString();
            show_image = findViewById(R.id.show_image);
            Picasso.get().load(imageUrl).into(show_image);
        } catch (Exception imageError) {
            Log.i("imageError", imageError.toString());
        }


    }
}