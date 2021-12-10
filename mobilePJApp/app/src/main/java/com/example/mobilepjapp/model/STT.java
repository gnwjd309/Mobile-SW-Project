package com.example.mobilepjapp.model;

import java.util.ArrayList;

public class STT {
    private String noteID;
    private ArrayList<String> speaker;
    private ArrayList<String> sentence;

    public STT(User user, ArrayList<String> speaker, ArrayList<String> sentence) {
        this.speaker = speaker;
        this.sentence = sentence;
    }

    public String getNoteID() {
        return noteID;
    }

    public ArrayList<String> getSpeaker() {
        return speaker;
    }

    public void setSpeaker(ArrayList<String> speaker) {
        this.speaker = speaker;
    }

    public ArrayList<String> getSentence() {
        return sentence;
    }

    public void setSentence(ArrayList<String> sentence) {
        this.sentence = sentence;
    }
}