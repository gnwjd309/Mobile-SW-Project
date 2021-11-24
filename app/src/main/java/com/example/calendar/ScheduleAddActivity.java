package com.example.calendar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ScheduleAddActivity extends AppCompatActivity {

    LayoutInflater LayoutInflater;
    LinearLayout Layout;
    LinearLayout.LayoutParams LayoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_add);
    }

    public void popup(View v){
        LayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Layout = (LinearLayout) LayoutInflater.inflate(R.layout.activity_schedule_add, null);
        Layout.setBackgroundColor(Color.parseColor("#99000000"));
        LayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(Layout, LayoutParams);
        popup_scheduleAdd();
    }

    // 일정 추가 팝업
    public void popup_scheduleAdd(){
        //LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        Calendar myCalendar = Calendar.getInstance();

        // schedule_add_view 파일의 정보를 메모리에 올리기
        View view = LayoutInflater.inflate(R.layout.activity_schedule_add, null);
        EditText scheduleID = view.findViewById(R.id.scheduleID);
        EditText scheduleDate = (EditText) view.findViewById(R.id.scheduleDate);
        EditText scheduleTime = (EditText) view.findViewById(R.id.scheduleTime);
        EditText scheduleMemo = view.findViewById(R.id.scheduleMemo);

        DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "yyyy/MM/dd";    // 출력형식   2021/11/20
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

                scheduleDate.setText(sdf.format(myCalendar.getTime()));
            }
        };

        scheduleDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ScheduleAddActivity.this, myDatePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        scheduleTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ScheduleAddActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String state = "AM";
                        // 선택한 시간이 12를 넘을경우 "PM"으로 변경 및 -12시간하여 출력 (ex : PM 6시 30분)
                        if (selectedHour > 12) {
                            selectedHour -= 12;
                            state = "PM";
                        }
                        // EditText에 출력할 형식 지정
                        scheduleTime.setText(state + " " + selectedHour + "시 " + selectedMinute + "분");
                    }
                }, hour, minute, false); // true의 경우 24시간 형식의 TimePicker 출현
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        new AlertDialog.Builder(this)
                .setTitle("일정추가")
                .setIcon(R.mipmap.ic_launcher)
                .setView(view)
                .setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Toast.makeText(ScheduleAddActivity.this, "일정이 저장되었습니다.", Toast.LENGTH_SHORT).show();

                        // 일정 저장
                        if(scheduleID != null) {

                        } else {
                            Toast.makeText(ScheduleAddActivity.this, "일정 제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                }).show();
    }

    // popup 안의 back 버튼을 누르면 실행
    // 현재 레이아웃 위에 올렸던 popup 레이아웃을 지움 -> 현재 레이아웃으로 돌아옴
    // popup 안에 만든 버튼의 실행 함수는 다 popup을 띄우는 레이아웃에 작성해야 함
    // 따로 popup activity를 연결하는 자바 클래스를 생성하지 않음
    public void backPage(View v){
        ((ViewManager) Layout.getParent()).removeView(Layout);
    }

}