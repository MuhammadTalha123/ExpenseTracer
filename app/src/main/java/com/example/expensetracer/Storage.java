package com.example.expensetracer;

public class Storage {
    private String userId;
    private String expenseId;
    private String imageId;

    public void setUserId(String userIdToSet) {
        this.userId = userIdToSet;
        return;
    }


    public void setExpenseId(String expenseIdToSet) {
        this.expenseId = expenseIdToSet ;
        return;
    }


    public void setImageId(String imageIdToSet) {
        this.imageId = imageIdToSet ;
        return;
    }


    public String getUserId() {return userId;}
    public String getExpenseId() {return expenseId;}
    public String getImageId() {return imageId;}

    private static final Storage holder = new Storage();
    public static Storage getInstance() {return holder;}
}
