package com.example.expensetracer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddExpenseActivity extends AppCompatActivity implements View.OnClickListener {

    TextView expNameField, expAmountField;
    Spinner expCategoryField;
    Spinner expenseTypeSelector;
    Button addBtn, cancelBtn;
    int catValue = -1;
    DatabaseReference ref;
    DatabaseReference expensesRef;
    DatabaseReference userRef;
    private FirebaseAuth mAuth;
    Utils myUtils;
    Integer expenseAmt;
    String expenseType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        expenseTypeSelector = findViewById(R.id.expCategory);
        String[] items = new String[]{"Deposit", "Credit"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        expenseTypeSelector.setAdapter(adapter);
        ref = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        myUtils = Utils.getInstance();
        expenseAmt = 0;
        expenseType = "";
        getSupportActionBar().setTitle(R.string.app_name_Add);
        loadAllViews();
        String uid = mAuth.getCurrentUser().getUid();
        expensesRef = ref.child("users").child(uid).child("expenses");
        userRef = ref.child("users").child(uid);
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
            Intent intent = new Intent(AddExpenseActivity.this, MainActivity.class);
            startActivity(intent);
        }
        return true;
    }

    public void loadAllViews() {
        expNameField = (TextView) findViewById(R.id.editName);
        expAmountField = (TextView) findViewById(R.id.editAmount);
        expCategoryField = (Spinner) findViewById(R.id.editCategory);
        addBtn = (Button) findViewById(R.id.addBtn);
        addBtn.setOnClickListener(this);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddExpenseActivity.this, android.R.layout.simple_spinner_item, ExpenseActivity.categories);
        expCategoryField.setAdapter(adapter);
        expCategoryField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                catValue = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void onClick(View view) {
        boolean isAddButton = view.getId() == R.id.addBtn;
        boolean isCancelButton = view.getId() == R.id.cancelBtn;
        if (isAddButton) {
            String expenseName = expNameField.getText().toString();
            expenseAmt = Integer.parseInt(expAmountField.getText().toString());
            if (expenseName == "") {
                Toast.makeText(AddExpenseActivity.this, "Enter Expense Name", Toast.LENGTH_LONG).show();
            } else if (expenseAmt <= 0) {
            } else if (expenseAmt <= 0) {
                Toast.makeText(AddExpenseActivity.this, "Enter Expense Amount", Toast.LENGTH_LONG).show();
            } else if (expenseName != "" && expenseAmt > 0 && catValue != -1) {
                myUtils.showLoading(this);
                String dtVal = String.valueOf(android.text.format.DateFormat.format("MM/dd/yyyy", new java.util.Date()));
                String key = expensesRef.push().getKey();
                expenseType = expenseTypeSelector.getSelectedItem().toString();
                List<String> myImagesCurrent = new ArrayList<>();
                Expense thisExpense = new Expense(expenseName, catValue, expenseAmt, dtVal, key, myImagesCurrent, expenseType);
                expensesRef.child(key).setValue(thisExpense).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        myUtils.hideLoading();
                        updateUserBalance(expenseAmt, expenseType);
                        finish();
                    }
                });
            }
        } else if (isCancelButton) {
            finish();
        }
    }

    private void updateUserBalance(final Integer expenseAmt, final String expenseType) {

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean hasAmount = dataSnapshot.exists();
                if (hasAmount) {
                    try {
                        String currentBalance = dataSnapshot.child("currentBalance").getValue().toString();
                        Integer currentBalanceInt = Integer.parseInt(currentBalance);
                        Integer myAmount = expenseAmt;
                        if (expenseType == "Deposit" && myAmount != 0) {
                            currentBalanceInt = currentBalanceInt + myAmount;
                            userRef.child("currentBalance").setValue(currentBalanceInt);
                            expAmountField.setText(0);
                            myAmount = 0;
                        } else if (expenseType == "Credit" && myAmount != 0) {
                            currentBalanceInt = currentBalanceInt - myAmount;
                            userRef.child("currentBalance").setValue(currentBalanceInt);
                            expAmountField.setText(0);
                            myAmount = 0;
                        }
                    } catch (Exception err) {
                        Log.i("imgTest0", err.toString());
                    }
                } else {
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}