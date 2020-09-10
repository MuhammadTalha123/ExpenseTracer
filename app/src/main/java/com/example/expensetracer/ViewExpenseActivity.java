package com.example.expensetracer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewExpenseActivity extends AppCompatActivity {

    Expense expense;
    TextView det_name;
    TextView det_cat;
    TextView det_amount;
    TextView det_date;
    TextView expense_type;
    Button closeBtn;
    Button drawBtn;
    ListView imgList;
    ArrayList<String> imagesList = new ArrayList<String>();
    DatabaseReference imagesRef;
    DecimalFormat df = new DecimalFormat("0.#");
    FirebaseAuth mAuth;
    Storage myStore;
    TextView noImageTextView;
    HashMap<String, String> imagesHashMap;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expense);
        progressBar = findViewById(R.id.progress_bar_for_images);
        getSupportActionBar().setTitle(R.string.app_name_view);
        mAuth = FirebaseAuth.getInstance();
        myStore = Storage.getInstance();
        loadAllViews();
        imagesHashMap = new HashMap<String, String>();
        String uid = myStore.getUserId();
        expense = myStore.getCurrentExpense();
        String expenseId = myStore.getExpenseId();
        try {
            imagesRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("expenses").child(expenseId).child("images");
            getImages(imagesRef);
        } catch (Exception err) {
            Log.i("delImage", err.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logOut) {
            mAuth.signOut();
            Intent intent = new Intent(ViewExpenseActivity.this, MainActivity.class);
            startActivity(intent);
        }
        return true;
    }

    public void loadAllViews() {
        expense = myStore.getCurrentExpense();
        closeBtn = (Button) findViewById(R.id.closeBtn);
        imgList = (ListView) findViewById(R.id.imgList);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ExpenseActivity.class));
            }
        });

        drawBtn = findViewById(R.id.draw_btn);

        drawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String expenseId = expense.getId();
                Intent intent = new Intent(getApplicationContext(), PaintingActivity.class);
                intent.putExtra("EXPENSE_ID", expenseId);
                startActivity(intent);
            }
        });
        if (expense != null) {
            det_name = (TextView) findViewById(R.id.dt_name);
            det_cat = (TextView) findViewById(R.id.dt_category);
            det_amount = (TextView) findViewById(R.id.dt_amount);
            det_date = (TextView) findViewById(R.id.dt_date);
            expense_type = (TextView) findViewById(R.id.expense_type);
            det_name.setText(expense.getName());
            expense_type.setText(expense.getExpenseType());
            det_cat.setText(ExpenseActivity.categories[expense.getCategory()]);
            det_amount.setText(df.format(expense.getAmount()));
            det_date.setText(expense.getcDate());

        }
    }

    public void getImages(DatabaseReference reference) {
        // Attach a listener to read the data at our posts reference
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                noImageTextView = findViewById(R.id.noImage);
                imagesList.clear();
                boolean hasImages = dataSnapshot.exists();
                if (hasImages) {
                    try {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String data = ds.getValue(String.class);
                            progressBar.setVisibility(View.VISIBLE);
                            imagesHashMap.put(data, ds.getKey());
                            imagesList.add(data);

                        }

                        myStore.setImagesHashMap(imagesHashMap);
                        CustomAdapter customAdapter = new CustomAdapter(ViewExpenseActivity.this, imagesList);
                        imgList.setAdapter(customAdapter);
                        noImageTextView.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);

                    } catch (Exception err) {
                        Log.i("imgTest0", err.toString());
                    }
                } else {
                    noImageTextView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("labelError", databaseError.toString());
            }
        });
    }
}