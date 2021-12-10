package com.example.mobilepjapp.note;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mobilepjapp.R;
import com.example.mobilepjapp.calender.CalenderActivity;
import com.example.mobilepjapp.model.DataLoad;

import java.util.ArrayList;

/**
 * 음성 노트의 요약(키워드, 요약문)을 출력하는 화면입니다.
 **/
public class SummaryActivity extends AppCompatActivity {

    private DataLoad dataLoad = null;

    private TextView tv_noteName;
    private TextView[] key = new TextView[3];
    private TextView[] sum = new TextView[3];
    private View btn_back;
    private View btn_memo, btn_note;
    private View btn_start, btn_pause, btn_stop, btn_restart;

    /**
     * 미디어 플레이어
     **/
    private MediaPlayer mp;             // 음악 재생을 위한 객체
    private SeekBar seekBar;
    boolean isPlaying = false;  // 재생 중인지 확인
    int pos;

    /* editText 객체, ArrayList */
    private EditText et;
    private ArrayList<EditText> editTexts = new ArrayList<>();

    private String noteID = null;
    private String noteName = null;

    public SummaryActivity() {
        dataLoad = new DataLoad();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        CalenderActivity.intentMode = 1;

        btn_back = findViewById(R.id.btn_back);
        seekBar = findViewById(R.id.seekBar);
        tv_noteName = findViewById(R.id.tv_noteName);

        key[0] = findViewById(R.id.tv_key1);
        key[1] = findViewById(R.id.tv_key2);
        key[2] = findViewById(R.id.tv_key3);
        sum[0] = findViewById(R.id.tv_sum1);
        sum[1] = findViewById(R.id.tv_sum2);
        sum[2] = findViewById(R.id.tv_sum3);

        /* Intent 로 noteID, noteName 을 받아옴 */
        noteID = getIntent().getStringExtra("noteID");
        noteName = getIntent().getStringExtra("noteName");
        setNoteName(noteName);
        setKeyword(noteID);
        setSummary(noteID);

        /**
         * SeekBar 이벤트
         */
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
        });
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

    // 노트이름을 받아서 set
    public void setNoteName(String noteName) {
        this.tv_noteName.setText(noteName);
    }

    public void setKeyword(String noteID) {
        String[] keywords;

        for(int i=0; i<dataLoad.user.getNotes().size(); i++) {
            if(dataLoad.user.getNotes().get(i).getID().equals(noteID)) {
                keywords = new String[dataLoad.user.getNotes().get(i).getKeyword().length];
                for(int j=0; j<dataLoad.user.getNotes().get(i).getKeyword().length; j++) {
                    keywords[j] = dataLoad.user.getNotes().get(i).getKeyword()[j];
                    key[j].setText((j+1) + ": " + keywords[j]);
                }
            }
        }
    }

    public void setSummary(String noteID) {
        String[] summary;

        for(int i=0; i<dataLoad.user.getNotes().size(); i++) {
            if(dataLoad.user.getNotes().get(i).getID().equals(noteID)) {
                summary = new String[dataLoad.user.getNotes().get(i).getSummary().length];
                for(int j=0; j<dataLoad.user.getNotes().get(i).getSummary().length; j++) {
                    summary[j] = dataLoad.user.getNotes().get(i).getSummary()[j];
                    sum[j].setText((j+1) + ": " + summary[j]);
                }
            }
        }
    }


    public void back(View v){
        Intent intent = new Intent(SummaryActivity.this, CalenderActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    /**
     * 버튼 클릭 이벤트
     **/
    public void btnClick(View v) {
        /* NoteMemo 버튼 클릭 시 */
        if(v.getId() == R.id.btn_noteMemo) {
            Intent intent = new Intent(getApplicationContext(), MemoActivity.class);
            intent.putExtra("noteID", noteID);
            intent.putExtra("noteName", noteName);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }

        /* NoteView 버튼 클릭 시 */
        else if(v.getId() == R.id.btn_noteView) {
            Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
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
    }
}