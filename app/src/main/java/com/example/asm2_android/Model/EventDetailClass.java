package com.example.asm2_android.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class EventDetailClass implements Parcelable {
    private int bloodTypeAPlus;
    private int bloodTypeBPlus;
    private int bloodTypeABPlus;
    private int bloodTypeOPlus;
    private int bloodTypeAMinus;
    private int bloodTypeBMinus;
    private int bloodTypeABMinus;
    private int bloodTypeOMinus;
    private String contactNumber;
    private int totalBloodAmount;
    private String eventName;
    private String siteManagerName;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private String locationAddress;
    private String eventDate;
    private String eventID;
    private String eventMission;

    public EventDetailClass(int bloodTypeAPlus, int bloodTypeBPlus, int bloodTypeABPlus, int bloodTypeOPlus,
                            int bloodTypeAMinus, int bloodTypeBMinus, int bloodTypeABMinus, int bloodTypeOMinus,
                            int totalBloodAmount, String eventName, String siteManagerName, String locationName,
                            Double latitude, Double longitude, String locationAddress, String eventDate,
                            String eventID, String contactNumber, String eventMission) {
        this.bloodTypeAPlus = bloodTypeAPlus;
        this.bloodTypeBPlus = bloodTypeBPlus;
        this.bloodTypeABPlus = bloodTypeABPlus;
        this.bloodTypeOPlus = bloodTypeOPlus;
        this.bloodTypeAMinus = bloodTypeAMinus;
        this.bloodTypeBMinus = bloodTypeBMinus;
        this.bloodTypeABMinus = bloodTypeABMinus;
        this.bloodTypeOMinus = bloodTypeOMinus;
        this.totalBloodAmount = totalBloodAmount;
        this.eventName = eventName;
        this.siteManagerName = siteManagerName;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationAddress = locationAddress;
        this.eventDate = eventDate;
        this.eventID = eventID;
        this.contactNumber = contactNumber;
        this.eventMission = eventMission;
    }

    protected EventDetailClass(Parcel in) {
        bloodTypeAPlus = in.readInt();
        bloodTypeBPlus = in.readInt();
        bloodTypeABPlus = in.readInt();
        bloodTypeOPlus = in.readInt();
        bloodTypeAMinus = in.readInt();
        bloodTypeBMinus = in.readInt();
        bloodTypeABMinus = in.readInt();
        bloodTypeOMinus = in.readInt();
        totalBloodAmount = in.readInt();
        eventName = in.readString();
        siteManagerName = in.readString();
        locationName = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        locationAddress = in.readString();
        eventDate = in.readString();
        eventID = in.readString();
        contactNumber = in.readString();
        eventMission = in.readString();
    }

    public static final Creator<EventDetailClass> CREATOR = new Creator<EventDetailClass>() {
        @Override
        public EventDetailClass createFromParcel(Parcel in) {
            return new EventDetailClass(in);
        }

        @Override
        public EventDetailClass[] newArray(int size) {
            return new EventDetailClass[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bloodTypeAPlus);
        dest.writeInt(bloodTypeBPlus);
        dest.writeInt(bloodTypeABPlus);
        dest.writeInt(bloodTypeOPlus);
        dest.writeInt(bloodTypeAMinus);
        dest.writeInt(bloodTypeBMinus);
        dest.writeInt(bloodTypeABMinus);
        dest.writeInt(bloodTypeOMinus);
        dest.writeInt(totalBloodAmount);
        dest.writeString(eventName);
        dest.writeString(siteManagerName);
        dest.writeString(locationName);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(locationAddress);
        dest.writeString(eventDate);
        dest.writeString(eventID);
        dest.writeString(contactNumber);
        dest.writeString(eventMission);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and setters for all fields
    public int getBloodTypeAPlus() {
        return bloodTypeAPlus;
    }

    public void setBloodTypeAPlus(int bloodTypeAPlus) {
        this.bloodTypeAPlus = bloodTypeAPlus;
    }

    public int getBloodTypeBPlus() {
        return bloodTypeBPlus;
    }

    public void setBloodTypeBPlus(int bloodTypeBPlus) {
        this.bloodTypeBPlus = bloodTypeBPlus;
    }

    public int getBloodTypeABPlus() {
        return bloodTypeABPlus;
    }

    public void setBloodTypeABPlus(int bloodTypeABPlus) {
        this.bloodTypeABPlus = bloodTypeABPlus;
    }

    public int getBloodTypeOPlus() {
        return bloodTypeOPlus;
    }

    public void setBloodTypeOPlus(int bloodTypeOPlus) {
        this.bloodTypeOPlus = bloodTypeOPlus;
    }

    public int getBloodTypeAMinus() {
        return bloodTypeAMinus;
    }

    public void setBloodTypeAMinus(int bloodTypeAMinus) {
        this.bloodTypeAMinus = bloodTypeAMinus;
    }

    public int getBloodTypeBMinus() {
        return bloodTypeBMinus;
    }

    public void setBloodTypeBMinus(int bloodTypeBMinus) {
        this.bloodTypeBMinus = bloodTypeBMinus;
    }

    public int getBloodTypeABMinus() {
        return bloodTypeABMinus;
    }

    public void setBloodTypeABMinus(int bloodTypeABMinus) {
        this.bloodTypeABMinus = bloodTypeABMinus;
    }

    public int getBloodTypeOMinus() {
        return bloodTypeOMinus;
    }

    public void setBloodTypeOMinus(int bloodTypeOMinus) {
        this.bloodTypeOMinus = bloodTypeOMinus;
    }

    public int getTotalBloodAmount() {
        return totalBloodAmount;
    }

    public void setTotalBloodAmount(int totalBloodAmount) {
        this.totalBloodAmount = totalBloodAmount;
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

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEventMission() {
        return eventMission;
    }

    public void setEventMission(String eventMission) {
        this.eventMission = eventMission;
    }
}
