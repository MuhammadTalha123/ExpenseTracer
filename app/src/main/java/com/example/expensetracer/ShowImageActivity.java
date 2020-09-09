package com.example.expensetracer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class ShowImageActivity extends AppCompatActivity {
    ImageView show_image;
    private Button cancelBtn, deleteBtn;
    Storage myStore;
    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        show_image = findViewById(R.id.show_image);
        cancelBtn = findViewById(R.id.cancelBtn);
        myStore = Storage.getInstance();
        try {
            Bundle bundle = getIntent().getExtras();
            imageUrl = bundle.get("imageUri").toString();
            Picasso.get().load(imageUrl).into(show_image);
        } catch (Exception imageError) {
            Log.i("imageError", imageError.toString());
        }

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myExpenseIntent = new Intent(getApplicationContext(), ViewExpenseActivity.class);
                Expense currentExpense = myStore.getCurrentExpense();
                myExpenseIntent.putExtra("expense", currentExpense);
                startActivity(myExpenseIntent);
            }
        });





    }
}