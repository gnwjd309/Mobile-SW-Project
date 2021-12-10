package com.example.mobilepjapp.record;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.mobilepjapp.HttpConnection;
import com.example.mobilepjapp.R;
import com.example.mobilepjapp.model.DataLoad;
import com.example.mobilepjapp.model.Schedule;
import com.example.mobilepjapp.note.NoteActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TransformActivity extends AppCompatActivity {

    private DataLoad dataLoad = null;
    HttpConnection http;
    String note_str = null;
    String stt_str = null;

    public TransformActivity() {
        dataLoad = new DataLoad();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transform);

        /*
        Intent intent = getIntent();
        String schedule_name = intent.getStringExtra("schedule_name");
        String schedule_id = null, note_id = null;
        */

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(TransformActivity.this, NoteActivity.class);
                String noteID = null;
                for(int i=0; i<dataLoad.user.getNotes().size(); i++){
                    if(dataLoad.user.getNotes().get(i).getName().equals("test1"))
                        noteID = dataLoad.user.getNotes().get(i).getID();
                }
                intent.putExtra("noteID", noteID);
                intent.putExtra("noteName", "test1");
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        }, 10000); // 0.5초후

        /*
        for(int i=0; i<dataLoad.user.getSchedules().size(); i++){
            if(dataLoad.user.getSchedules().get(i).getName().equals(schedule_name))
                schedule_id = dataLoad.user.getSchedules().get(i).getID();
        }
        System.out.println(schedule_id);
        //note_str = noteReq(dataLoad.user.getEmail(), schedule_name, schedule_id);
        //dataLoad.user.getNotes().clear();
        //dataLoad.dataParsing.noteParsing(dataLoad, note_str);

        for(int i=0; i<dataLoad.user.getNotes().size(); i++){
            System.out.println(dataLoad.user.getNotes().get(i).getID());
            System.out.println("name : " + dataLoad.user.getNotes().get(i).getName());
            System.out.println(schedule_name);
            if(dataLoad.user.getNotes().get(i).getName().equals(schedule_name))
                note_id = dataLoad.user.getNotes().get(i).getID();
        }

        System.out.println("note id : " + note_id);
        String filename = "audio_only.mp3";
        stt_str = sttReq(note_id, filename);
        dataLoad.user.getNotes().clear();
        dataLoad.dataParsing.noteParsing(dataLoad, note_str);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(TransformActivity.this, NoteActivity.class);
                startActivity(intent);
            }
        }, 30000); // 0.5초후

         */
    }

    public String noteReq(String email, String name, String date){
        http = new HttpConnection();
        String link = "save/note";

        JSONObject reqForm = new JSONObject();
        try {
            reqForm.put("email", email);
            reqForm.put("name", name);
            reqForm.put("schedule_id", date);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String resultData = http.httpRequest(link, reqForm);

        return resultData;
    }

    public String sttReq(String note_id, String filename){
        http = new HttpConnection();
        String link = "get/textProcessor";

        JSONObject reqForm = new JSONObject();
        try {
            reqForm.put("note_id", note_id);
            reqForm.put("filename", filename);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String resultData = http.httpRequest(link, reqForm);

        return resultData;
    }
}