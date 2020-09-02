package com.example.expensetracer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddExpenseActivity extends AppCompatActivity implements View.OnClickListener{

    TextView expNameField,expAmountField;
    Spinner expCategoryField;
    Spinner expenseTypeSelector;

    Button addBtn, cancelBtn;
    int catValue = -1;
    DatabaseReference ref;
    DatabaseReference expensesRef;
    private FirebaseAuth mAuth;
    String[] expenseImages = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        expenseTypeSelector = findViewById(R.id.expCategory);
        String[] items = new String[]{"Deposit","Credit"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        expenseTypeSelector.setAdapter(adapter);
        ref = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setTitle(R.string.app_name_Add);
        loadAllViews();
        String uid = mAuth.getCurrentUser().getUid();
        expensesRef = ref.child("users").child(uid).child("expenses");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logOut) {
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddExpenseActivity.this, android.R.layout.simple_spinner_item,ExpenseActivity.categories);
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


    public void onClick(View v) {
        if(v.getId() == R.id.addBtn) {
            try{
                String expenseName = expNameField.getText().toString();
                Float expenseAmt = Float.parseFloat(expAmountField.getText().toString());
                if(expenseName == "") {
                    Toast.makeText(AddExpenseActivity.this,"Enter Expense Name",Toast.LENGTH_LONG).show();
                } else if(expenseAmt <= 0) {
                } else if(expenseAmt <= 0) {
                    Toast.makeText(AddExpenseActivity.this,"Enter Expense Amount",Toast.LENGTH_LONG).show();
                } else if (expenseName != "" && expenseAmt > 0 && catValue != -1) {
                    String dtVal = String.valueOf(android.text.format.DateFormat.format("MM/dd/yyyy", new java.util.Date()));
                    String key = expensesRef.push().getKey();
                    String expenseType = expenseTypeSelector.getSelectedItem().toString();
                    List<String> myImagesCurrent = new ArrayList<>();
                    Expense thisExpense = new Expense(expenseName, catValue, expenseAmt,dtVal, key, myImagesCurrent, expenseType);
                    expensesRef.child(key).setValue(thisExpense);
                    finish();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(AddExpenseActivity.this,"Enter Valid Amount",Toast.LENGTH_LONG).show();
                Log.i("valuesOne",e.toString());
            } catch (Exception e) {
                Toast.makeText(AddExpenseActivity.this,"Enter Valid Values",Toast.LENGTH_LONG).show();
                Log.i("values",e.toString());
            }
        } else if (v.getId() == R.id.cancelBtn) {
            finish();
        }
    }
}