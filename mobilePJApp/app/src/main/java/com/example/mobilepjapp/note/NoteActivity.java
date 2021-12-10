package com.example.mobilepjapp.note;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mobilepjapp.HttpConnection;
import com.example.mobilepjapp.R;
import com.example.mobilepjapp.calender.CalenderActivity;
import com.example.mobilepjapp.model.DataLoad;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 음성 노트 확인(노트 상세보기) 최초 화면입니다.
 **/
public class NoteActivity extends AppCompatActivity {

    private DataLoad dataLoad = null;

    private View btn_back, btn_search, btn_edit;
    private View btn_memo, btn_summary;
    private View btn_start, btn_pause, btn_stop, btn_restart;

    /* 미디어 플레이어 */
    private MediaPlayer mp;     // 음악 재생을 위한 객체
    private SeekBar seekBar;
    boolean isPlaying = false;  // 재생 중인지 확인
    int pos;

    /* 리스트 뷰 */
    ListView listView;
    private ListItemAdapter adapter;

    /* editText 객체, ArrayList */
    private EditText et;
    private ArrayList<EditText> editTexts = new ArrayList<>();

    /* POP UP 에 필요한 객체 */
    private LinearLayout ln;
    private LinearLayout layout;
    private LayoutInflater LayoutInflater;
    private LinearLayout.LayoutParams LayoutParams;

    /* 동작을 확인하기 위해 넣은 임의의 값입니다. 코드 결합 시 null 로 초기화 필요!!! */
    String noteID = null;
    String noteName = null;
    public static String result_stt = null;
    HttpConnection http = null;

    /**
     * MainActivity 객체 생성 시 DataLoad 객체를 바로 생성해줍니다.
     */
    public NoteActivity() {
        dataLoad = new DataLoad();
        dataLoad.loadSTT();
        dataLoad.setStt();
        dataLoad.dataParsing.getSpeakerNum(dataLoad);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        CalenderActivity.intentMode = 1;

        noteID = getIntent().getStringExtra("noteID");
        noteName = getIntent().getStringExtra("noteName");

        System.out.println(noteName);

        for(int i=0; i<dataLoad.user.getNotes().size(); i++){
            if(dataLoad.user.getNotes().get(i).getName().equals(noteName))
                noteID = dataLoad.user.getNotes().get(i).getID();
        }

        btn_back = findViewById(R.id.btn_back);
        btn_search = findViewById(R.id.btn_search);
        seekBar = findViewById(R.id.seekBar);
        listView = findViewById(R.id.listview);

        TextView tv_noteName = (TextView) findViewById(R.id.tv_noteName);
        tv_noteName.setText(noteName);

        /**
         * adapter 의 경우 DataLoad 클래스에서 생성한 객체를 초기화 해줍니다.
         *
         **/
        adapter = dataLoad.adapter;
        listView.setAdapter(adapter);

        /**
         * SeekBar 이벤트
         **/
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(seekBar.getMax() == progress) {
                    isPlaying = false;
                    mp.stop();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isPlaying = false;
                mp.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isPlaying = true;
                int ttt = seekBar.getProgress();
                mp.seekTo(ttt);
                mp.start();
                new MediaThread().start();
            }
        }); // SeekBar 이벤트 종료
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPlaying = false;
        if(mp != null) {
            mp.release();
        }
    }

    class MediaThread extends Thread {
        @Override
        public void run() {
            while(isPlaying) {
                seekBar.setProgress(mp.getCurrentPosition());
            }
        }
    }

    public void back(View v){
        Intent intent = new Intent(NoteActivity.this, CalenderActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    /**
     * 버튼 클릭 이벤트
     **/
    public void btnClick(View v) {
        /* Edit 버튼 클릭 시 */
        if(v.getId() == R.id.btn_edit) {
            LayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (LinearLayout) LayoutInflater.inflate(R.layout.edit_speaker, null);
            layout.setBackgroundColor(Color.parseColor("#99000000"));
            LayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            addContentView(layout, LayoutParams);

            for(int i=0; i<dataLoad.dataParsing.getSpeakerNum(dataLoad); i++) {
                this.et = new EditText((getApplicationContext()));
                this.et.setLayoutParams(LayoutParams);
                this.et.setHint("화자" + (i+1) + "의 이름을 입력하세요.");
                this.et.setTextSize(17);
                this.et.setWidth(200);
                this.et.setHeight(120);
                this.et.setId(i);
                this.ln = layout.findViewById(R.id.ln);
                this.ln.addView(et);
                this.editTexts.add(et);
            }
        }   // edit 버튼

        /* NoteMemo 버튼 클릭 시 */
        else if(v.getId() == R.id.btn_noteMemo) {
            Intent intent = new Intent(getApplicationContext(), MemoActivity.class);
            intent.putExtra("noteID", noteID);
            intent.putExtra("noteName", noteName);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }

        /* Summary 버튼 클릭 시 */
        else if(v.getId() == R.id.btn_summary) {
            Intent intent = new Intent(getApplicationContext(), SummaryActivity.class);
            intent.putExtra("noteID", noteID);
            intent.putExtra("noteName", noteName);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }

        /* Start 버튼 클릭 시 */
        else if(v.getId() == R.id.btn_start) {
            mp = MediaPlayer.create(
                    getApplicationContext(),
                    R.raw.test);
            mp.setLooping(false);   // true 시 무한 루프
            mp.start();

            int a = mp.getDuration();
            seekBar.setMax(a);
            new MediaThread().start();
            isPlaying = true;
        }

        /* Stop 버튼 클릭 시 */
        else if(v.getId() == R.id.btn_stop) {
            isPlaying = false;
            mp.stop();
            mp.release();
            seekBar.setProgress(0);
        }

        /* Pause 버튼 클릭 시 */
        else if(v.getId() == R.id.btn_pause) {
            pos = mp.getCurrentPosition();
            mp.pause();
            isPlaying = false;
        }

        /* Restart 버튼 클릭 시 */
        else if(v.getId() == R.id.btn_restart){
            mp.seekTo(pos);
            mp.start();
            isPlaying = true;
            new MediaThread().start();
        }

    }   // btnClick 메소드 종료

    public void backPage(View v){
        /* 화자 변경 취소 시 */
        if(v.getId() == R.id.cancel) {
            ((ViewManager) layout.getParent()).removeView(layout);
        }

        /* 화자 변경 확인 시 */
        else if(v.getId() == R.id.ok) {
            ArrayList<String> s = new ArrayList<>();    // editText 에서 받아온 값 배열

            for(int i=0; i<editTexts.size(); i++) {
                s.add(editTexts.get(i).getText().toString());
            }

            for(int i=0; i<dataLoad.stt.getSpeaker().size(); i++) {
                for(int j=0; j<s.size(); j++) {
                    if(dataLoad.stt.getSpeaker().get(i).equals(Integer.toString(j+1))) {
                        dataLoad.stt.getSpeaker().set(i, s.get(j));
                        break;
                    }
                }
            }

            dataLoad.setStt();
            listView.setAdapter(dataLoad.adapter);
            ((ViewManager) layout.getParent()).removeView(layout);
        }
    }

    public String sttReq(String note_id){
        http = new HttpConnection();
        String link = "get/stt";

        JSONObject reqForm = new JSONObject();

        try {
            reqForm.put("note_id", note_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String resultData = http.httpRequest(link, reqForm);

        return resultData;
    }
}   // MainActivity 종료