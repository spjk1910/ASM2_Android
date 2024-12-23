package com.example.asm2_android.Model;

public enum BloodVolumeEnum {
    VOLUME(0),
    VOLUME_250(250),
    VOLUME_350(350),
    VOLUME_450(450);

    private final int volume;

    BloodVolumeEnum(int volume) {
        this.volume = volume;
    }

    public int getVolume() {
        return volume;
    }
}
