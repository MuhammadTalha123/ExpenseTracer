package com.example.expensetracer;

import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapter implements ListAdapter {

    ArrayList<String> arrayList;
    Context context;
    StorageReference imageFireStoreRef;

    DatabaseReference imageFirebasDBReg;
    String userId;
    Storage myStore;

    public CustomAdapter(Context context, ArrayList<String> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        this.myStore = Storage.getInstance();
        this.imageFireStoreRef = FirebaseStorage.getInstance().getReference();
        this.imageFirebasDBReg = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final String subjectData = arrayList.get(i);
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.activity_list_item, null);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get user id
                    // get expense id
                    // Get image id

                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {

                    AlertDialog.Builder delImage = new AlertDialog.Builder(context);
                    delImage.setTitle("Deleting Image?");
                    delImage.setMessage("You Will Loss This Image...");
                    delImage.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Get user id
                            // get expense id
                            // Get image id

                            String expenseId = myStore.getExpenseId();
                            String userId = myStore.getUserId();
                            String imageId = myStore.getImageIdFromUrl(subjectData);

                            try {
                                imageFireStoreRef.child(userId).child(expenseId).child(imageId).delete();
                                imageFirebasDBReg.child("users").child(userId).child("expenses").child(expenseId).child("images").child(imageId).removeValue();
                                view.setVisibility(View.GONE);
                                Toast.makeText(context, "Image Deleted Successfully", Toast.LENGTH_LONG).show();
                            } catch (Exception err) {
                                Toast.makeText(context, "Unable to delete image", Toast.LENGTH_SHORT).show();
                                Log.i("deleteImage", err.toString());
                            }

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    delImage.show();

                    return true;
                }
            });
            ImageView image = view.findViewById(R.id.image);
            Picasso.get()
                    .load(subjectData)
                    .into(image);
        }
        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
