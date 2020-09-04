package com.example.expensetracer;

import java.util.HashMap;

public class Storage {
    private String userId;
    private String expenseId;
    private String imageId;
    private HashMap<String, String> imagesHashMap;

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

    public void setImagesHashMap(HashMap<String, String> imagesHashMapToSet) {
        this.imagesHashMap = imagesHashMapToSet;
        return;
    }


    public String getUserId() {return userId;}
    public String getExpenseId() {return expenseId;}
    public String getImageId() {return imageId;}
    public HashMap<String, String> getImageHashMap() {return imagesHashMap;}

    public String getImageIdFromUrl(String imageUrl) {
        return imagesHashMap.get(imageUrl);
    }

    private static final Storage holder = new Storage();
    public static Storage getInstance() {return holder;}
}
