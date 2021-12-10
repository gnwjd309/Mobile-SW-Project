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
 * 음성 노트 메모를 출력하는 화면입니다.
 **/
public class MemoActivity extends AppCompatActivity {

    private DataLoad dataLoad = null;

    private TextView tv_memo, tv_noteName;
    private View btn_back;
    private View btn_summary, btn_note;
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

    public MemoActivity() {
        dataLoad = new DataLoad();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        CalenderActivity.intentMode = 1;

        tv_memo = findViewById(R.id.tv_memo);
        tv_noteName = findViewById(R.id.tv_noteName);
        btn_back = findViewById(R.id.btn_back);
        seekBar = findViewById(R.id.seekBar);

        /* Intent 로 noteID, noteName 을 받아옴 */
        noteName = getIntent().getStringExtra("noteName");
        noteID = getIntent().getStringExtra("noteID");
        setNoteName(noteName);
        setNoteMemo(noteID);

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

    // 노트메모를 받아서 set
    public void setNoteMemo(String noteID) {
        String noteMemo = null;

        for(int i=0; i<dataLoad.user.getNotes().size(); i++) {
            if(dataLoad.user.getNotes().get(i).getID().equals(noteID)) {
                noteMemo = dataLoad.user.getNotes().get(i).getMemo();
            }
        }

        this.tv_memo.setText(noteMemo);
    }

    public void back(View v){
        Intent intent = new Intent(MemoActivity.this, CalenderActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    /**
     * 버튼 클릭 이벤트
     **/
    public void btnClick(View v) {
        /* NoteView 버튼 클릭 시 */
        if(v.getId() == R.id.btn_noteView) {
            Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
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
    }
}