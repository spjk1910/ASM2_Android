package com.example.asm2_android.Model;

public class EventClass {
    private String eventName;
    private String siteManagerName;
    private String contact;
    private String location;
    private String dateStart;
    private String eventMission;
    private String[] bloodType;

    public EventClass(String eventName, String siteManagerName, String contact, String location, String dateStart, String eventMission, String[] bloodType) {
        this.eventName = eventName;
        this.siteManagerName = siteManagerName;
        this.contact = contact;
        this.location = location;
        this.dateStart = dateStart;
        this.eventMission = eventMission;
        this.bloodType = bloodType;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
