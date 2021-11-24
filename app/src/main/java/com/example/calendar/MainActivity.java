package com.example.calendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    String scheduleID;
    String scheduleDate;
    String scheduleTime;
    String scheduleMemo;

    String date = null;

    public CalendarView calendarView;
    public TextView diaryTextView;

    ListView listview;
    LinearLayout Layout;
    LayoutInflater LayoutInflater;
    LinearLayout.LayoutParams LayoutParams;

    ArrayList<ScheduleDTO> arrayList;
    ScheduleListAdapter arrayAdapter;
    ArrayList<String> items = new ArrayList<String>();
    ArrayAdapter adapter;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calenderView);
        diaryTextView = findViewById(R.id.diaryTextView);

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, items) ;
        listview = (ListView) findViewById(R.id.listView);
        listview.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    // 메인에서 + 버튼을 누르면 실행되는 일정 추가에 대한 팝업
    public void popup_scheduleAdd(View v){
        LayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Layout = (LinearLayout) LayoutInflater.inflate(R.layout.activity_schedule_add, null);
        //Layout.setBackgroundColor(Color.parseColor("#99000000"));
        LayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(Layout, LayoutParams);

        Calendar myCalendar = Calendar.getInstance();

        EditText scheduleID_et = Layout.findViewById(R.id.scheduleID_et);
        EditText scheduleDate_et = (EditText) Layout.findViewById(R.id.scheduleDate_et);
        EditText scheduleTime_et = (EditText) Layout.findViewById(R.id.scheduleTime_et);
        EditText scheduleMemo_et = Layout.findViewById(R.id.scheduleMemo_et);
        Button record_Btn = Layout.findViewById(R.id.record_Btn);
        Button save_Btn = Layout.findViewById(R.id.save_Btn);

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

                scheduleDate_et.setText(sdf.format(myCalendar.getTime()));
            }
        };

        scheduleDate_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this, myDatePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        scheduleTime_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String state = "AM";
                        // 선택한 시간이 12를 넘을경우 "PM"으로 변경 및 -12시간하여 출력 (ex : PM 6시 30분)
                        if (selectedHour > 12) {
                            selectedHour -= 12;
                            state = "PM";
                        }
                        // EditText에 출력할 형식 지정
                        scheduleTime_et.setText(state + " " + selectedHour + "시 " + selectedMinute + "분");
                    }
                }, hour, minute, false); // true의 경우 24시간 형식의 TimePicker 출현
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        // 저장 버튼 클릭 리스너
        save_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 팝업에서 입력 받은 값 저장
                //arrayList.add(new ScheduleDTO("회의"));

                scheduleID = scheduleID_et.getText().toString();
                scheduleTime = scheduleTime_et.getText().toString();
                scheduleMemo = scheduleMemo_et.getText().toString();
                Toast.makeText(MainActivity.this, scheduleID + "일정이 저장되었습니다.", Toast.LENGTH_SHORT);
                ((ViewManager) Layout.getParent()).removeView(Layout);
            }
        });

    }

    // 리스트뷰 클릭 시 일정 상세보기 팝업
    public void popup_scheduleDetail(View v) {
        LayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Layout = (LinearLayout) LayoutInflater.inflate(R.layout.activity_schedule_detail, null);
        //Layout.setBackgroundColor(Color.parseColor("#99000000"));
        LayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(Layout, LayoutParams);

        arrayList = new ArrayList<>();

        // arrayList에 항목 추가

        arrayAdapter = new ScheduleListAdapter(arrayList);

        listview = (ListView) findViewById((R.id.listView));
        listview.setAdapter(arrayAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            }
        });
    }

    // popup 안의 녹음 추가하기 버튼을 누르면 실행
     public void record() {

     }

   // popup 안의 취소 버튼을 누르면 실행
    // 현재 레이아웃 위에 올렸던 popup 레이아웃을 지움 -> 현재 레이아웃으로 돌아옴
    // popup 안에 만든 버튼의 실행 함수는 다 popup을 띄우는 레이아웃에 작성해야 함
    // 따로 popup activity를 연결하는 자바 클래스를 생성하지 않음
    public void backPage(View v){
        ((ViewManager) Layout.getParent()).removeView(Layout);
    }

}
