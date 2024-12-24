package com.example.asm2_android.Model;

public class BloodTypeDetails {
    private int bloodTypeAPlus;
    private int bloodTypeBPlus;
    private int bloodTypeABPlus;
    private int bloodTypeOPlus;
    private int bloodTypeAMinus;
    private int bloodTypeBMinus;
    private int bloodTypeABMinus;
    private int bloodTypeOMinus;
    private int totalBloodAmount;

    public BloodTypeDetails(int bloodTypeAPlus, int bloodTypeBPlus, int bloodTypeABPlus,
                            int bloodTypeOPlus, int bloodTypeAMinus, int bloodTypeBMinus,
                            int bloodTypeABMinus, int bloodTypeOMinus, int totalBloodAmount) {
        this.bloodTypeAPlus = bloodTypeAPlus;
        this.bloodTypeBPlus = bloodTypeBPlus;
        this.bloodTypeABPlus = bloodTypeABPlus;
        this.bloodTypeOPlus = bloodTypeOPlus;
        this.bloodTypeAMinus = bloodTypeAMinus;
        this.bloodTypeBMinus = bloodTypeBMinus;
        this.bloodTypeABMinus = bloodTypeABMinus;
        this.bloodTypeOMinus = bloodTypeOMinus;
        this.totalBloodAmount = totalBloodAmount;
    }

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
}