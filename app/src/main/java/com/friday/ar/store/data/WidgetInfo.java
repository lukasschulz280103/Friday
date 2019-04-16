package com.friday.ar.store.data;


import android.media.Image;

import java.util.ArrayList;
import java.util.Map;

public class WidgetInfo {
    private int Id;
    private String title;
    private String authorName;
    private String thumbnailSrc;
    private TYPE extra_type;
    private String downloads;
    private String category;
    private String videoUrl;
    private ArrayList<Image> screenshots;
    private double rating;
    private double price;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getDownloads() {
        return downloads;
    }

    public void setDownloads(String downloads) {
        this.downloads = downloads;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public ArrayList<Image> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(ArrayList<Image> screenshots) {
        this.screenshots = screenshots;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getThumbnailSrc() {
        return thumbnailSrc;
    }

    public void setThumbnailSrc(String thumbnailSrc) {
        this.thumbnailSrc = thumbnailSrc;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public TYPE getExtra_type() {
        return extra_type;
    }

    public void setExtra_type(TYPE extra_type) {
        this.extra_type = extra_type;
    }

    enum TYPE {
        HOT,
        SALE,
        FREE,
        NEW,
        CHOSEN,
    }

    public static class ExtraInfo {
        Map<String, Object> mData;
        boolean hasExtra;
        boolean isFreeLimited;
        String extraType;

        public ExtraInfo(Map<String, Object> data) {
            this.mData = data;
            processData();
        }

        void processData() {
            hasExtra = (Boolean) mData.get("hasExtra");
            isFreeLimited = (Boolean) mData.get("isFreeLimited");
        }

        public boolean hasExtra() {
            return hasExtra;
        }

        public boolean isFreeLimited() {
            return isFreeLimited;
        }

        public String getExtraType() {
            return extraType;
        }
    }
}
