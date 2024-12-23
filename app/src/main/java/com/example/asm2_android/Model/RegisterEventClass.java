package com.example.asm2_android.Model;

public class RegisterEventClass {
    String username;
    private String name;
    private String address;
    private String phone;
    private String dateOfBirth;
    private String gender;
    private String bloodType;
    private String weight;
    private String eventID;
    private String bloodVolumeDonate;

    public RegisterEventClass(String username,String name, String address, String phone, String dateOfBirth,
                              String gender, String bloodType, String weight,String eventID, String bloodVolumeDonate) {
        this.username = username;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bloodType = bloodType;
        this.weight = weight;
        this.eventID = eventID;
        this.bloodVolumeDonate = bloodVolumeDonate;
    }

    public String getBloodVolumeDonate() {
        return bloodVolumeDonate;
    }

    public void setBloodVolumeDonate(String bloodVolumeDonate) {
        this.bloodVolumeDonate = bloodVolumeDonate;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
