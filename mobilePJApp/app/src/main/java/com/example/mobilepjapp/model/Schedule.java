package com.example.mobilepjapp.model;

public class Schedule {
    private String id;
    private String name;
    private String date;
    private String memo;

    public Schedule(User user, String id, String name, String date, String memo) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.memo = memo;

        user.getSchedules().add(this);
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
