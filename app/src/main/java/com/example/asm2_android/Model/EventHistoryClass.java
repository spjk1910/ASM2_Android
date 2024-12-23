package com.example.asm2_android.Model;

import java.util.Calendar;

public class EventHistoryClass {
    private String name;
    private String address;
    private String dateOfBirth;
    private String gender;
    private String bloodType;
    private String getBloodVolumeDonate;
    private String location;
    private String eventDate;
    private Double latitude;
    private Double longitude;
    private String eventID;

    public EventHistoryClass(String name, String address, String dateOfBirth, String gender, String bloodType,
                             String getBloodVolumeDonate, String location, String eventDate, Double latitude, Double longitude, String eventID) {
        this.name = name;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bloodType = bloodType;
        this.getBloodVolumeDonate = getBloodVolumeDonate;
        this.location = location;
        this.eventDate = eventDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.eventID = eventID;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getGenderAge() {
        return gender+", " + calculateAge(dateOfBirth)+" years old";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getGetBloodVolumeDonate() {
        return getBloodVolumeDonate;
    }

    public void setGetBloodVolumeDonate(String getBloodVolumeDonate) {
        this.getBloodVolumeDonate = getBloodVolumeDonate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
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


    private int calculateAge(String dob) {
        String[] birthday = dob.split("\\.");
        String birthMonthStr = birthday[0];

        int birthDay = Integer.parseInt(birthday[1]);
        int birthYear = Integer.parseInt(birthday[2]);
        int birthMonth = getMonthNumber(birthMonthStr);

        Calendar birthDate = Calendar.getInstance();
        birthDate.set(birthYear, birthMonth - 1, birthDay);

        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
        if (today.get(Calendar.MONTH) < birthDate.get(Calendar.MONTH) ||
                (today.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return age;
    }

    private int getMonthNumber(String monthName) {
        switch (monthName) {
            case "Jan": return 1;
            case "Feb": return 2;
            case "Mar": return 3;
            case "Apr": return 4;
            case "May": return 5;
            case "Jun": return 6;
            case "Jul": return 7;
            case "Aug": return 8;
            case "Sep": return 9;
            case "Oct": return 10;
            case "Nov": return 11;
            case "Dec": return 12;
            default: return -1;
        }
    }
}
