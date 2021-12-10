package com.example.mobilepjapp.calender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mobilepjapp.HttpConnection;
import com.example.mobilepjapp.MainActivity;
import com.example.mobilepjapp.R;
import com.example.mobilepjapp.SaveSchedule;
import com.example.mobilepjapp.list.ListActivity;
import com.example.mobilepjapp.model.DataLoad;
import com.example.mobilepjapp.model.Schedule;
import com.example.mobilepjapp.model.User;
import com.example.mobilepjapp.note.NoteActivity;
import com.example.mobilepjapp.record.RecordActivity;
import com.example.mobilepjapp.record.TransformActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.sdk.user.UserApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CalenderActivity extends AppCompatActivity {

    protected View btn_logout, btn_revoke;
    private MainActivity ma = null;
    private User user = null;

    private String strNick, strProfileImg, strEmail;    // 닉네임, 프로필 이미지, 계정 이메일
    private TextView tv_email, tv_nick;
    private ImageView iv_photo;

    HttpConnection http;
    private DataLoad dataLoad = null;

    CalendarView calendarView;
    ListView listview;
    LinearLayout addLayout, myLayout, rawLayout;
    LayoutInflater addLayoutInflater, myLayoutInflater, rawLayoutInflater;
    LinearLayout.LayoutParams addLayoutParams, myLayoutParams, rawLayoutParams;

    CalenderListAdapter adapter;

    // 일정 추가/상세보기 팝업
    EditText scheduleName_et;
    Button scheduleDate_et;
    EditText scheduleMemo_et;
    Button record_Btn;
    ImageButton saveSchedule;

    String date = null;

    AlertDialog dialog;
    ImageView recordImage = null;
    String selectTitle = null, selectDate = null, selectMemo = null;

    ArrayList<Schedule> schedule = new ArrayList<Schedule>();
    public static ArrayList<SaveSchedule> setSchedule = new ArrayList<SaveSchedule>();
    ArrayList<SaveSchedule> viewSchedule = new ArrayList<SaveSchedule>();
    public static int intentMode = 0;

    public CalenderActivity() {
        dataLoad = new DataLoad();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        calendarView = findViewById(R.id.calenderView);
        listview = (ListView) findViewById(R.id.listView);

        Intent intent = getIntent();
        strNick = intent.getStringExtra("nickname");
        strProfileImg = intent.getStringExtra("photoUrl");
        strEmail = intent.getStringExtra("email");

        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        date = simpleDate.format(mDate);

        // schedule 설
        schedule = dataLoad.user.getSchedules();
        adapter = new CalenderListAdapter(this, viewSchedule);

        if(intentMode == 0){
            for(int i=0; i<schedule.size(); i++) {
                setSchedule.add(new SaveSchedule(schedule.get(i).getName(), schedule.get(i).getDate(), schedule.get(i).getMemo()));
            }
        }

        if(viewSchedule.size() > 0)
            viewSchedule.clear();

        for(int i=0; i<setSchedule.size(); i++) {
            if(setSchedule.get(i).getScheduleDate().equals(date)) {
                viewSchedule.add(new SaveSchedule(setSchedule.get(i).getScheduleName(), setSchedule.get(i).getScheduleDate(), setSchedule.get(i).getScheduleMemo()));
            }
        }
        adapter.notifyDataSetChanged();
        listview.setAdapter(adapter);

        // 캘린더뷰에서 날짜 선택 시 리스너
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {

                if(dayOfMonth<10)
                    date = year + "-" + (month+1) + "-0" + dayOfMonth;
                else date = year + "-" + (month+1) + "-" + dayOfMonth;

                if(viewSchedule.size() > 0)
                    viewSchedule.clear();

                for(int i=0; i<setSchedule.size(); i++) {
                    System.out.println(setSchedule.get(i).getScheduleDate());
                    if(setSchedule.get(i).getScheduleDate().equals(date)) {
                        viewSchedule.add(new SaveSchedule(setSchedule.get(i).getScheduleName(), setSchedule.get(i).getScheduleDate(), setSchedule.get(i).getScheduleMemo()));
                    }
                }
                adapter.notifyDataSetChanged();
                listview.setAdapter(adapter);
            }
        });

        LinearLayout clickLayout = (LinearLayout) listview.findViewById(R.id.clickLayout);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectTitle = viewSchedule.get(position).getScheduleName();
                System.out.println("select : " + selectTitle);
                selectDate = viewSchedule.get(position).getScheduleDate();
                selectMemo = viewSchedule.get(position).getScheduleMemo();
                popupImgXml(position);
            }
        });
    }

    // 메인에서 + 버튼을 누르면 실행되는 일정 추가에 대한 팝업
    public void popup_scheduleAdd(View v){
        addLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addLayout = (LinearLayout) addLayoutInflater.inflate(R.layout.activity_schedule_add, null);
        //Layout.setBackgroundColor(Color.parseColor("#99000000"));
        addLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(addLayout, addLayoutParams);

        popupMenu(addLayout);
    }

    // 일정 추가 시 저장 버튼
    // 수정했을 때 구현하
    public void saveSchedule(View v){
        String scheduleName = null, scheduleDate = null, scheduleMemo = null;

        scheduleName = scheduleName_et.getText().toString();
        scheduleDate = scheduleDate_et.getText().toString();
        scheduleMemo = scheduleMemo_et.getText().toString();

        if(scheduleName.equals(""))
            Toast.makeText(getApplicationContext(), "Enter schedule name", Toast.LENGTH_SHORT).show();
        if(scheduleDate.equals(""))
            Toast.makeText(getApplicationContext(), "Enter schedule date", Toast.LENGTH_SHORT).show();
        if(scheduleMemo.equals(""))
            Toast.makeText(getApplicationContext(), "Enter schedule memo", Toast.LENGTH_SHORT).show();
        if(scheduleName.isEmpty() == false && scheduleDate.isEmpty() == false && scheduleMemo.isEmpty() == false){
            setSchedule.add(new SaveSchedule(scheduleName, scheduleDate, scheduleMemo));

            System.out.println("name : " + scheduleName);
            if(viewSchedule.size() > 0)
                viewSchedule.clear();

            for(int i=0; i<setSchedule.size(); i++) {
                if(setSchedule.get(i).getScheduleDate().equals(date)) {
                    viewSchedule.add(new SaveSchedule(setSchedule.get(i).getScheduleName(), setSchedule.get(i).getScheduleDate(), setSchedule.get(i).getScheduleMemo()));
                }
            }
            adapter.notifyDataSetChanged();
            listview.setAdapter(adapter);

            ((ViewManager) addLayout.getParent()).removeView(addLayout);
            save();
        }
    }

    public void save(){
        String query = "";
        for(int i=0; i<setSchedule.size(); i++){
            if(query.length() > 0){
                query = query + "!!!" + Integer.toString(i+1) + "/" + setSchedule.get(i).getScheduleName() + "/" + setSchedule.get(i).getScheduleDate() + "/" + setSchedule.get(i).getScheduleMemo();
            }
            else{
                query = Integer.toString(i+1) + "/" + setSchedule.get(i).getScheduleName() + "/" + setSchedule.get(i).getScheduleDate() + "/" + setSchedule.get(i).getScheduleMemo();
            }
            System.out.println(setSchedule.get(i).getScheduleMemo());
        }

        System.out.println("333");
        System.out.println(query);
        String result = saveScheduleReq(dataLoad.user.getEmail(), query);
        System.out.println(result);
    }

    public void popupMenu(LinearLayout layout){
        Calendar myCalendar = Calendar.getInstance();

        scheduleName_et = layout.findViewById(R.id.scheduleName_et);
        scheduleDate_et = layout.findViewById(R.id.scheduleDate_et);
        //scheduleTime_et = layout.findViewById(R.id.scheduleTime_et);
        scheduleMemo_et = layout.findViewById(R.id.scheduleMemo_et);
        record_Btn = layout.findViewById(R.id.record_Btn);
        saveSchedule = layout.findViewById(R.id.saveSchedule);

        DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "yyyy-MM-dd";    // 출력형식   2021/11/20
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

                scheduleDate_et.setText(sdf.format(myCalendar.getTime()));
            }
        };

        scheduleDate_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CalenderActivity.this, myDatePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void test(View v){
        System.out.println("test");
    }

    // 팝업창 제거
    public void backPage(View v){
        ((ViewManager) addLayout.getParent()).removeView(addLayout);
    }

    public void backPage2(View v){
        ((ViewManager) myLayout.getParent()).removeView(myLayout);
    }

    public void backPage3(View v){
        ((ViewManager) rawLayout.getParent()).removeView(rawLayout);
    }

    // 캘린 뷰로 변경
    public void changeList(View v){
        Intent intent = new Intent(CalenderActivity.this, ListActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public String saveScheduleReq(String email, String query){
        http = new HttpConnection();
        String link = "save/schedule";

        JSONObject reqForm = new JSONObject();

        try {
            reqForm.put("email", email);
            reqForm.put("query", query);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String resultData = http.httpRequest(link, reqForm);

        return resultData;
    }

    public void popupImgXml(int position) {
        //일단 res에 popupimg.xml 만든다
        //그 다음 화면을 inflate 시켜 setView 한다

        //팝업창에 xml 붙이기///////////////
        LayoutInflater inflater = LayoutInflater.from(CalenderActivity.this);
        View view = inflater.inflate(R.layout.activity_schedule_detail, null);

        Calendar myCalendar = Calendar.getInstance();

        EditText scheduleName_et = view.findViewById(R.id.scheduleName_et);
        Button scheduleDate_et = view.findViewById(R.id.scheduleDate_et);
        EditText scheduleMemo_et = view.findViewById(R.id.scheduleMemo_et);

        scheduleName_et.setHint(selectTitle);
        scheduleDate_et.setText(selectDate);
        if(TextUtils.isEmpty(selectMemo) == false){
            scheduleMemo_et.setHint(selectMemo);
            scheduleMemo_et.setHintTextColor(Color.BLACK);
        }

        DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "yyyy-MM-dd";    // 출력형식   2021/11/20
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
                scheduleDate_et.setText(sdf.format(myCalendar.getTime()));
            }
        };

        scheduleDate_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CalenderActivity.this, myDatePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ImageButton deleteBtn = view.findViewById(R.id.deleteSchedule);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0; i<CalenderActivity.setSchedule.size(); i++){
                    if(CalenderActivity.setSchedule.get(i).getScheduleName().equals(selectTitle)
                                && CalenderActivity.setSchedule.get(i).getScheduleDate().equals(selectDate)) {
                        System.out.println(CalenderActivity.setSchedule.get(i).getScheduleName());
                        System.out.println(CalenderActivity.setSchedule.get(i).getScheduleDate());
                        CalenderActivity.setSchedule.remove(i);
                    }
                }

                if(viewSchedule.size() > 0)
                    viewSchedule.clear();

                for(int i=0; i<CalenderActivity.setSchedule.size(); i++) {
                    if(CalenderActivity.setSchedule.get(i).getScheduleDate().equals(selectDate)) {
                        viewSchedule.add(new SaveSchedule(CalenderActivity.setSchedule.get(i).getScheduleName(), CalenderActivity.setSchedule.get(i).getScheduleDate(), CalenderActivity.setSchedule.get(i).getScheduleMemo()));
                    }
                }

                Toast.makeText(getApplicationContext(), "Drop schedule", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
                listview.setAdapter(adapter);
                dialog.dismiss();

                save();
            }
        });

        ImageButton saveBtn = view.findViewById(R.id.saveSchedule);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String scheduleName = null, scheduleDate = null, scheduleMemo = null;

                scheduleName = scheduleName_et.getText().toString();
                scheduleDate = scheduleDate_et.getText().toString();
                scheduleMemo = scheduleMemo_et.getText().toString();


                System.out.println(scheduleMemo);

                if(scheduleName.isEmpty() == false && scheduleDate.isEmpty() == true && scheduleMemo.isEmpty() == true){
                    for(int i=0; i<setSchedule.size(); i++){
                        if(setSchedule.get(i).getScheduleName().equals(selectTitle)){
                            setSchedule.set(i, new SaveSchedule(scheduleName, selectDate, selectMemo));
                        }
                    }
                }
                else if(scheduleName.isEmpty() == false && scheduleDate.isEmpty() == false && scheduleMemo.isEmpty() == true){
                    for(int i=0; i<setSchedule.size(); i++){
                        if(setSchedule.get(i).getScheduleName().equals(selectTitle)){
                            setSchedule.set(i, new SaveSchedule(scheduleName, scheduleDate, selectMemo));
                        }
                    }
                }
                else if(scheduleName.isEmpty() == false && scheduleDate.isEmpty() == false && scheduleMemo.isEmpty() == false){
                    for(int i=0; i<setSchedule.size(); i++){
                        if(setSchedule.get(i).getScheduleDate().equals(selectDate)){
                            setSchedule.set(i, new SaveSchedule(scheduleName, scheduleDate, scheduleMemo));
                        }
                    }
                }
                else if(scheduleName.isEmpty() == true && scheduleDate.isEmpty() == true && scheduleMemo.isEmpty() == true){
                    for(int i=0; i<setSchedule.size(); i++){
                        if(setSchedule.get(i).getScheduleDate().equals(selectDate)){
                            setSchedule.set(i, new SaveSchedule(selectTitle, selectDate, selectMemo));
                        }
                    }
                }
                else if(scheduleName.isEmpty() == true && scheduleDate.isEmpty() == false && scheduleMemo.isEmpty() == true){
                    for(int i=0; i<setSchedule.size(); i++){
                        if(setSchedule.get(i).getScheduleDate().equals(selectDate)){
                            setSchedule.set(i, new SaveSchedule(selectTitle, scheduleDate, selectMemo));
                        }
                    }
                }
                else if(scheduleName.isEmpty() == true && scheduleDate.isEmpty() == true && scheduleMemo.isEmpty() == false){
                    for(int i=0; i<setSchedule.size(); i++){
                        if(setSchedule.get(i).getScheduleDate().equals(selectDate)){
                            setSchedule.set(i, new SaveSchedule(selectTitle, selectDate, scheduleMemo));
                        }
                    }
                }
                else if(scheduleName.isEmpty() == false && scheduleDate.isEmpty() == true && scheduleMemo.isEmpty() == false){
                    for(int i=0; i<setSchedule.size(); i++){
                        if(setSchedule.get(i).getScheduleDate().equals(selectDate)){
                            setSchedule.set(i, new SaveSchedule(scheduleName, selectDate, scheduleMemo));
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                listview.setAdapter(adapter);

                dialog.dismiss();

                save();
            }
        });

        Button noteBtn = view.findViewById(R.id.noteDetail);

        for(int i=0; i<dataLoad.user.getNotes().size(); i++){
            if(selectTitle.equals(dataLoad.user.getNotes().get(i).getName())){
                noteBtn.setVisibility(View.VISIBLE);
            }
        }

        noteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NoteActivity.class);
                String noteID = null;
                for(int i=0; i<dataLoad.user.getNotes().size(); i++){
                    if(dataLoad.user.getNotes().get(i).getName().equals(selectTitle))
                        noteID = dataLoad.user.getNotes().get(i).getID();
                }
                intent.putExtra("noteID", noteID);
                intent.putExtra("noteName", selectTitle);
                view.getContext().startActivity(intent);
            }
        });

        Button recordBtn = view.findViewById(R.id.record_Btn);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RecordActivity.class);
                intent.putExtra("schedule_name", selectTitle);
                view.getContext().startActivity(intent);
            }
        });

        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(CalenderActivity.this);
        builder.setView(view);

        dialog = builder.create();
        dialog.show();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void myPage(View v){
        myLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myLayout = (LinearLayout) myLayoutInflater.inflate(R.layout.activity_mypage, null);
        //Layout.setBackgroundColor(Color.parseColor("#99000000"));
        myLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(myLayout, myLayoutParams);

        /*
        btn_logout = findViewById(R.id.btn_Logout);  // 카카오 로그아웃 버튼
        btn_revoke = findViewById(R.id.btn_Revoke);

        tv_nick = findViewById(R.id.tv_nickname);
        tv_email = findViewById(R.id.tv_email);
        iv_photo = findViewById(R.id.iv_photo);

        user = new User(strNick, strEmail, strProfileImg);

        tv_nick.setText(user.getNickname());
        tv_email.setText(user.getEmail());
        Glide.with(this).load(user.getPhotoUrl()).into(iv_photo);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        ma.updateKaKaoLoginUi();
                        return null;
                    }
                });

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_revoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new androidx.appcompat.app.AlertDialog.Builder(CalenderActivity.this)
                        .setMessage("탈퇴하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(ma.state.equals("G")){
                                    ma.mAuth.getCurrentUser().delete();
                                    Log.e("[구글]","연결끊기");
                                }

                                else if(ma.state.equals("K")){
                                    UserApiClient.getInstance().unlink(throwable -> {
                                        if(throwable != null) {
                                            Log.e("[카카오]","연결끊기 실패", throwable);
                                        } else {
                                            Log.i("카카오 로그아웃", "연결끊기 성공. SDK에서 토큰 삭제됨");
                                        }
                                        return null;
                                    });
                                }

                                dialog.dismiss();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        */

    }

    public void raw(View v){
        rawLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rawLayout = (LinearLayout) rawLayoutInflater.inflate(R.layout.activity_raw, null);
        //Layout.setBackgroundColor(Color.parseColor("#99000000"));
        rawLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(rawLayout, rawLayoutParams);
    }
}