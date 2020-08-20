package com.example.expensetracer;

import android.widget.ListAdapter;

import java.io.Serializable;

public class Expense implements Serializable {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String name;
    private int category;
    private float amount;
    private String cDate;
    private String expenseId;
    private String[] expenseImages;

    public Expense() {

    }

    public Expense(String name, int category, float amount, String cDate, String expenseId, String[] expenseImages) {
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.cDate = cDate;
        this.cDate = cDate;
        this.expenseId = expenseId;
        this.expenseImages = expenseImages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getcDate() {
        return cDate;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public String[] getExpenseImages() {
        return expenseImages;
    }

    public void setcDate(String cDate) {
        this.cDate = cDate;
    }

}

