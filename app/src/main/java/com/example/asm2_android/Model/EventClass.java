package com.example.asm2_android.Model;

import android.location.Location;

public class EventClass {
    private String siteManagerUsername;
    private String eventName;
    private String siteManagerName;
    private String contact;
    private double latitude;
    private double longitude;
    private String locationName;
    private String dateStart;
    private String eventMission;
    private String[] bloodType;

    public EventClass(String siteManagerUsername ,String eventName, String siteManagerName, String contact, double latitude, double longitude, String locationName, String dateStart, String eventMission, String[] bloodType) {
        this.siteManagerUsername = siteManagerUsername;
        this.eventName = eventName;
        this.siteManagerName = siteManagerName;
        this.contact = contact;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.dateStart = dateStart;
        this.eventMission = eventMission;
        this.bloodType = bloodType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getSiteManagerUsername() {
        return siteManagerUsername;
    }

    public void setSiteManagerUsername(String siteManagerUsername) {
        this.siteManagerUsername = siteManagerUsername;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getSiteManagerName() {
        return siteManagerName;
    }

    public void setSiteManagerName(String siteManagerName) {
        this.siteManagerName = siteManagerName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getEventMission() {
        return eventMission;
    }

    public void setEventMission(String eventMission) {
        this.eventMission = eventMission;
    }

    public String[] getBloodType() {
        return bloodType;
    }

    public void setBloodType(String[] bloodType) {
        this.bloodType = bloodType;
    }
}
