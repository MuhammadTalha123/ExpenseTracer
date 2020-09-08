package com.example.expensetracer;

import android.app.ProgressDialog;
import android.content.Context;

public class Utils {
    private ProgressDialog loadingDialog;

    public void showLoading(Context currentContext){
        this.loadingDialog = new ProgressDialog(currentContext);
        this.loadingDialog.setMax(100);
        // Setting Title
        this.loadingDialog.setTitle("Please wait");
        // Setting Message
        this.loadingDialog.setMessage("Loading...");
        // Progress Dialog Style Horizontal
        this.loadingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // Progress Dialog Style Spinner
        this.loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.loadingDialog.show();
    }

    public void hideLoading(){
        this.loadingDialog.dismiss();
    }

    private static final Utils instance = new Utils();
    public static Utils getInstance() {return instance;}
}
