package com.example.asm2_android.Model;

public class NotificationClass {
    private String time;
    private String content;
    private String eventID;

    public NotificationClass(String time, String content, String eventID) {
        this.time = time;
        this.content = content;
        this.eventID = eventID;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
