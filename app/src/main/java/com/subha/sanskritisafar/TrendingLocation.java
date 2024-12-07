package com.subha.sanskritisafar;


public class TrendingLocation {
    private String id;
    private String title;
    private String description;
    private String imageUrl;

    // Constructor
    public TrendingLocation(String id, String title, String description, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
