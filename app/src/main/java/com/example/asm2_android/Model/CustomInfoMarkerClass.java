package com.example.asm2_android.Model;

public class CustomInfoMarkerClass {
    private String eventName;
    private String bloodType;
    private String startDate;
    private int image;

    public CustomInfoMarkerClass(String eventName, String bloodType, String startDate, int image) {
        this.eventName = eventName;
        this.bloodType = bloodType;
        this.startDate = startDate;
        this.image = image;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
