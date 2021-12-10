package com.example.mobilepjapp.model;

import java.util.ArrayList;

public class User {
    private String nickname;
    private String email;
    private String photoUrl;
    private ArrayList<Schedule> schedules = new ArrayList<Schedule>();
    private ArrayList<Note> notes = new ArrayList<Note>();

    public User(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
        //this.photoUrl = photoUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoUrl() {
        return this.photoUrl;
    }

    public ArrayList<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(ArrayList<Schedule> schedules) {
        this.schedules = schedules;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }
}
