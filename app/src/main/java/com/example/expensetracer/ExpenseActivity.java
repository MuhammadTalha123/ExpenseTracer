package com.example.expensetracer;

import androidx.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.HashMap;

public class ExpenseActivity extends AppCompatActivity {

    ImageView addExp;
    ArrayList<Expense> expenses = new ArrayList<Expense>();
    ListView expList;
    LinearLayout expHint;
    String uid;
    String balanceAmount;
    DatabaseReference ref;
    DatabaseReference expensesRef;
    DatabaseReference expenseTypeRef;
    FirebaseAuth mAuth;
    TextView currentBalance;
    Integer userCurrentAmount;
    HashMap<String, String> imagesHashMap;
    DatabaseReference imagesRef;
    final static String[] categories = {"Groceries", "Invoice", "Transportation", "Shopping", "Rent", "Trips", "Utilities", "Other"};
    Storage myStore;
    Utils myUtils;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        currentBalance = findViewById(R.id.currentBalance);
        getSupportActionBar().setTitle(R.string.app_name_Expense);
        ref = FirebaseDatabase.getInstance().getReference();
        loadAllViews();
        myStore = Storage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myUtils = Utils.getInstance();
        imagesHashMap = new HashMap<String, String>();
        uid = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        storageReference = storageReference.child(uid);
        userCurrentAmount = 0;
        FirebaseUser user = mAuth.getCurrentUser();
        expenseTypeRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        expenseTypeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                balanceAmount = snapshot.child("currentBalance").getValue().toString();
                currentBalance.setText(balanceAmount);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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
            Intent intent = new Intent(ExpenseActivity.this, MainActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        expensesRef = ref.child("users").child(uid).child("expenses");
        if (expensesRef == null) {
            expHint.setVisibility(View.GONE);
            expList.setVisibility(View.VISIBLE);
        } else {
            expensesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    expenses.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Expense exp = ds.getValue(Expense.class);
                        exp.setId(ds.getKey());
                        expenses.add(exp);
                    }
                    showExpenseTable();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    public void loadAllViews() {
        expList = findViewById(R.id.expensesListView);
        addExp = findViewById(R.id.addExpenseButton);
        expHint = findViewById(R.id.addExpenseBody);

        addExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExpenseActivity.this, AddExpenseActivity.class);
                startActivity(intent);
            }
        });
        expList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder delExp = new AlertDialog.Builder(ExpenseActivity.this);
                delExp.setTitle("Expense Deleting");
                delExp.setMessage("You Will Loss Your Expense Detail...");
                delExp.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            myUtils.showLoading(ExpenseActivity.this);
                            userCurrentAmount = Integer.parseInt(balanceAmount);
                            Expense exp = expenses.get(position);
                            String expenseType = exp.getExpenseType();
                            Integer expenseAmount = exp.getAmount();
                            final String expenseId = exp.getExpenseId();
                            imagesRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("expenses").child(expenseId).child("images");
                            deleteAllExpenseImages(imagesRef, expenseId);
                            if (expenseType == "Credit") {
                                userCurrentAmount = userCurrentAmount + expenseAmount;
                            } else if (expenseType == "Deposit") {
                                userCurrentAmount = userCurrentAmount - expenseAmount;
                            }
                            expenseTypeRef.child("currentBalance").setValue(userCurrentAmount).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    currentBalance.setText(userCurrentAmount.toString());
                                    expensesRef.child(expenseId).removeValue();
                                    myUtils.hideLoading();
                                }
                            });
                            Toast.makeText(ExpenseActivity.this, "Expense Deleted Successfully", Toast.LENGTH_SHORT).show();
                        } catch (Exception err) {
                            Toast.makeText(ExpenseActivity.this, "Unable to delete expense", Toast.LENGTH_SHORT).show();
                            Log.i("deleteEx", err.toString());
                        }

                    }
                });

                delExp.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });


                delExp.show();
                return true;
            }

        });
        expList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Expense exp = expenses.get(position);
                    Intent intent = new Intent(ExpenseActivity.this, ViewExpenseActivity.class);
                    intent.putExtra("expense", exp);
                    myStore.setCurrentExpense(exp);
                    myStore.setExpenseId(exp.getExpenseId());
                    myStore.setUserId(uid);
                    startActivity(intent);
                } catch (Exception err) {
                    Log.i("expenseErr", err.toString());
                }
            }
        });
    }

    public void showExpenseTable() {
        if (expenses.size() == 0) {
            expHint.setVisibility(View.GONE);
            expList.setVisibility(View.GONE);
        } else {
            expHint.setVisibility(View.GONE);
            expList.setVisibility(View.VISIBLE);
            ExpenseAdapter adapter = new ExpenseAdapter(ExpenseActivity.this, R.layout.roww_layout, expenses);
            expList.setAdapter(adapter);
        }
    }

    private void deleteAllExpenseImages(DatabaseReference imagesRef, final String expenseId) {
        imagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean hasImages = dataSnapshot.exists();
                imagesHashMap.clear();
                if (hasImages) {
                    try {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String data = ds.getValue(String.class);
                            imagesHashMap.put(ds.getKey(), data);
                            removeImageFromStore(expenseId, ds.getKey());
                        }
                    } catch (Exception err) {
                        Log.i("imgTest0", err.toString());
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void removeImageFromStore(String expenseId, String imageId) {
        storageReference.child(expenseId).child(imageId).delete();
    }
}