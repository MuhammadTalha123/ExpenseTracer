package com.example.expensetracer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;
import java.util.List;

public class ExpenseAdapter extends ArrayAdapter<Expense> {

    Context mContext;
    int mResource;
    List<Expense> mObjects;
    DecimalFormat df = new DecimalFormat("###.#");
    public ExpenseAdapter(Context context, int resource, List<Expense> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mObjects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource,parent,false);
            holder = new ViewHolder();
            holder.nameText = (TextView) convertView.findViewById(R.id.expName);
            holder.amountText = (TextView) convertView.findViewById(R.id.expVal);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        Expense exp = mObjects.get(position);
        holder.nameText.setText(exp.getName());
        holder.amountText.setText("$" + String.valueOf(df.format(exp.getAmount())));
        return convertView;
    }
}

class ViewHolder {
    TextView nameText;
    TextView amountText;
}
