package com.subha.sanskritisafar;

import java.util.ArrayList;
import java.util.Map;

public class Recommendation {
    private String id;
    private String title;
    private String short_description;
    private String long_description;
    private String imageUrl;
    private String imageUrl360;
    private Map<String, ArrayList<String>> objects;

    // Empty constructor for Firebase
    public Recommendation() {}

    public Recommendation(String id, String title, String short_description, String long_description, String imageUrl, String imageUrl360, Map<String, ArrayList<String>> objects) {
        this.id = id;
        this.title = title;
        this.short_description = short_description;
        this.long_description = long_description;
        this.imageUrl = imageUrl;
        this.imageUrl360 = imageUrl360;
        this.objects = objects;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getShort_description() {
        return short_description;
    }

    public String getLong_description() {
        return long_description;
    }
    public String getImageUrl360() {
        return imageUrl360;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Map<String, ArrayList<String>> getObjects() {
        return objects;
    }
}

