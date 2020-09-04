package com.example.expensetracer;

import android.widget.ListAdapter;

import java.io.Serializable;
import java.util.List;

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
    private int amount;
    private String cDate;
    private String expenseId;
    private List<String> expenseImages;
    private String expenseType;

    public Expense() {

    }

    public Expense(String name, int category, int amount, String cDate, String expenseId, List<String> expenseImages, String expenseType) {
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.cDate = cDate;
        this.cDate = cDate;
        this.expenseId = expenseId;
        this.expenseImages = expenseImages;
        this.expenseType = expenseType;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getcDate() {
        return cDate;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public List<String> getExpenseImages() {
        return expenseImages;
    }

    public void setcDate(String cDate) {
        this.cDate = cDate;
    }

}

