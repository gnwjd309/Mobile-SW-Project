package com.example.mobilepjapp.model;

import java.util.ArrayList;

public class DataParsing {
    private String data;
    private String stt;

    public String[] stringsParsing(DataLoad dl, String result) {
        String[] msg = result.split("///");
        return msg;
    }

    public void scheduleParsing(DataLoad dl, String str) {
        String[] msg = str.split("!!!");
        for(int i=0; i<msg.length; i++) {
            String[] m = msg[i].split("/");
            if(m.length == 3) {
                dl.schedule = new Schedule(dl.user, m[0], m[1], m[2], "");
            } else if(m.length == 4) {
                dl.schedule = new Schedule(dl.user, m[0], m[1], m[2], m[3]);
            }
        }
        System.out.println("parsing schedule");
    }

    public void noteParsing(DataLoad dl, String str) {
        String[] msg = str.split("!!!");
        for(int i=0; i<msg.length; i++) {
            String[] m = msg[i].split("/");

            String[] mm1 = m[2].split("&&");
            String[] mm2 = m[3].split("&&");

            if(m.length == 4) {
                dl.note = new Note(dl.user, m[0], m[1], mm1, mm2, "");
            } else if(m.length == 5) {
                dl.note = new Note(dl.user, m[0], m[1], mm1, mm2, m[4]);
            }
        }
    }

    public void sttParsing(DataLoad dl, String str) {
        System.out.println("str : " + str);
        String[] msg = str.split("///");

        String[] m1 = msg[0].split("/");
        ArrayList<String> mm1 = new ArrayList<>();
        for(String item : m1) {
            mm1.add(item);
        }
        String[] m2 = msg[1].split("/");
        ArrayList<String> mm2 = new ArrayList<>();
        for(String item : m2) {
            mm2.add(item);
        }
        dl.stt = new STT(dl.user, mm2, mm1);
    }

    public int getSpeakerNum(DataLoad dl) {
        int count = 0;
        boolean b;

        for(int i=0; i<dl.stt.getSpeaker().size(); i++) {
            b = false;
            for(int j=i+1; j<dl.stt.getSpeaker().size(); j++) {
                if(dl.stt.getSpeaker().get(i).equals(dl.stt.getSpeaker().get(j))) {
                    b = true;
                    break;
                }
            }
            if( b == false ) {
                count++;
            }
        }
        return count;
    }
}