package com.example.expensetracer;

import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapter implements ListAdapter {

    ArrayList<String> arrayList;
    Context context;

    public CustomAdapter(Context context, ArrayList<String> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
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

        String subjectData = arrayList.get(i);
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.activity_list_item, null);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get user id
                    // get expense id
                    // Get image id
                    Toast.makeText(context, "Image Clicked" + i + "", Toast.LENGTH_SHORT).show();
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    AlertDialog.Builder delImage = new AlertDialog.Builder(context);
                    delImage.setTitle("Deleting Image?");
                    delImage.setMessage("You Will Loss This Image...");
                    delImage.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(context, "Yes", Toast.LENGTH_SHORT).show();
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
