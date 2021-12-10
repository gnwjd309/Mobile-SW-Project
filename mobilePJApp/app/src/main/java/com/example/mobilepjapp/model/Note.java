package com.example.mobilepjapp.model;

public class Note {
    private String id;
    private String name;
    private String[] keyword = new String[3];
    private String[] summary = new String[3];
    private String memo;
    private STT stt;

    public Note(User user, String id, String name, String[] keyword, String[] summary, String memo) {
        this.id = id;
        this.name = name;
        this.keyword = keyword;
        this.summary = summary;
        this.memo = memo;

        user.getNotes().add(this);
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

    public String[] getKeyword() {
        return keyword;
    }

    public void setKeyword(String[] keyword) {
        this.keyword = keyword;
    }

    public String[] getSummary() {
        return summary;
    }

    public void setSummary(String[] summary) {
        this.summary = summary;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public STT getStt() {
        return stt;
    }

    public void setStt(STT stt) {
        this.stt = stt;
    }
}