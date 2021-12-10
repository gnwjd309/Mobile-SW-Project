package com.example.mobilepjapp.list;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilepjapp.HttpConnection;
import com.example.mobilepjapp.MainActivity;
import com.example.mobilepjapp.R;
import com.example.mobilepjapp.SaveSchedule;
import com.example.mobilepjapp.calender.CalenderActivity;
import com.example.mobilepjapp.model.DataLoad;
import com.example.mobilepjapp.model.Schedule;
import com.example.mobilepjapp.model.User;
import com.example.mobilepjapp.note.NoteActivity;
import com.example.mobilepjapp.record.RecordActivity;
import com.example.mobilepjapp.record.TransformActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ListActivity extends AppCompatActivity {

    //String url = "http://192.168.25.4:5000/";
    //String result;

    protected View btn_logout, btn_revoke;
    private MainActivity ma = null;
    private User user = null;

    private String strNick, strProfileImg, strEmail;    // 닉네임, 프로필 이미지, 계정 이메일
    private TextView tv_email, tv_nick;
    private ImageView iv_photo;

    HttpConnection http;
    private DataLoad dataLoad = null;

    ListView listview;
    LinearLayout addLayout, myLayout, rawLayout;
    LayoutInflater addLayoutInflater, myLayoutInflater, rawLayoutInflater;
    LinearLayout.LayoutParams addLayoutParams, myLayoutParams, rawLayoutParams;

    ListAdapter adapter;

    // 일정 추가/상세보기 팝업
    EditText scheduleName_et;
    Button scheduleDate_et;
    //Button scheduleTime_et;
    EditText scheduleMemo_et;
    Button record_Btn;
    ImageButton saveSchedule;

    AlertDialog dialog;
    ImageView recordImage = null;
    String selectTitle = null, selectDate = null, selectMemo = null;

    public ListActivity() {
        dataLoad = new DataLoad();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        CalenderActivity.intentMode = 1;

        adapter = new ListAdapter(this, CalenderActivity.setSchedule);
        listview = (ListView) findViewById(R.id.listView);

        adapter.notifyDataSetChanged();
        listview.setAdapter(adapter);

        LinearLayout clickLayout = (LinearLayout) listview.findViewById(R.id.clickLayout);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectTitle = CalenderActivity.setSchedule.get(position).getScheduleName();
                System.out.println("select : " + selectTitle);
                selectDate = CalenderActivity.setSchedule.get(position).getScheduleDate();
                selectMemo = CalenderActivity.setSchedule.get(position).getScheduleMemo();
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
    public void saveSchedule(View v){
        String scheduleName = null, scheduleDate = null, scheduleMemo = null;

        scheduleName = scheduleName_et.getText().toString();
        scheduleDate = scheduleDate_et.getText().toString();
        scheduleMemo = scheduleMemo_et.getText().toString();

        //schedule = dataLoad.user.getSchedules();

        if(scheduleName.equals(""))
            Toast.makeText(getApplicationContext(), "Enter schedule name", Toast.LENGTH_SHORT).show();
        if(scheduleDate.equals(""))
            Toast.makeText(getApplicationContext(), "Enter schedule date", Toast.LENGTH_SHORT).show();
        if(scheduleMemo.equals(""))
            Toast.makeText(getApplicationContext(), "Enter schedule memo", Toast.LENGTH_SHORT).show();
        if(scheduleName.isEmpty() == false && scheduleDate.isEmpty() == false && scheduleMemo.isEmpty() == false){
            CalenderActivity.setSchedule.add(new SaveSchedule(scheduleName, scheduleDate, scheduleMemo));

            adapter.notifyDataSetChanged();
            listview.setAdapter(adapter);

            ((ViewManager) addLayout.getParent()).removeView(addLayout);
        }

        save();
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
                new DatePickerDialog(ListActivity.this, myDatePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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
    public void changeCalender(View v){
        Intent intent = new Intent(ListActivity.this, CalenderActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    /*
    public String saveScheduleReq(String email, String name, String date, String memo){
        http = new HttpConnection();
        String link = "save/schedule";

        JSONObject reqForm = new JSONObject();

        try {
            reqForm.put("email", email);
            reqForm.put("name", name);
            reqForm.put("date", date);
            reqForm.put("memo", memo);
            if(TextUtils.isEmpty(memo))   // 메모가 비어있
                reqForm.put("mode", 1);
            else reqForm.put("mode", 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String resultData = http.httpRequest(link, reqForm);

        return resultData;
    }

    public String getScheduleReq(String email) {
        String link = "get/schedule";

        url = url + link;

        JSONObject reqForm = new JSONObject();
        try {
            reqForm.put("user_email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), reqForm.toString());

        result = null;
        postRequest(url, body);

        int count = 0;
        while(result==null){
            count++;
        }
        System.out.println(result);
        return result;
    }

    public void postRequest(String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                e.printStackTrace();
                result = "Failure";
            }

            @Override
            public void onResponse(Call call, final Response response) {
                try {
                    final String responseString = response.body().string().trim();
                    if (responseString.equals("Failure")) {
                        result = "Failure";
                    } else {   // 성공했을 때
                        System.out.println("Success : " + responseString);
                        result = responseString;
                        System.out.println("Success2 : " + result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    */

    public void save(){
        String query = "";
        for(int i=0; i<CalenderActivity.setSchedule.size(); i++){
            if(query.length() > 0){
                query = query + "!!!" + Integer.toString(i+1) + "/" + CalenderActivity.setSchedule.get(i).getScheduleName() + "/" + CalenderActivity.setSchedule.get(i).getScheduleDate() + "/" + CalenderActivity.setSchedule.get(i).getScheduleMemo();
            }
            else{
                query = Integer.toString(i+1) + "/" + CalenderActivity.setSchedule.get(i).getScheduleName() + "/" + CalenderActivity.setSchedule.get(i).getScheduleDate() + "/" + CalenderActivity.setSchedule.get(i).getScheduleMemo();
            }
        }

        System.out.println(query);
        String result = saveScheduleReq(dataLoad.user.getEmail(), query);
        System.out.println(result);
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
        LayoutInflater inflater = LayoutInflater.from(ListActivity.this);
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
                new DatePickerDialog(getApplicationContext(), myDatePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ImageButton deleteBtn = view.findViewById(R.id.deleteSchedule);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0; i<CalenderActivity.setSchedule.size(); i++){
                    if(CalenderActivity.setSchedule.get(i).getScheduleName().equals(selectTitle)) {
                        if (CalenderActivity.setSchedule.get(i).getScheduleDate().equals(selectDate))
                            CalenderActivity.setSchedule.remove(i);
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

                if(scheduleName.isEmpty() == false && scheduleDate.isEmpty() == true && scheduleMemo.isEmpty() == true){
                    for(int i=0; i<CalenderActivity.setSchedule.size(); i++){
                        if(CalenderActivity.setSchedule.get(i).getScheduleName().equals(selectTitle)){
                            CalenderActivity.setSchedule.set(i, new SaveSchedule(scheduleName, selectDate, selectMemo));
                        }
                    }
                }
                else if(scheduleName.isEmpty() == false && scheduleDate.isEmpty() == false && scheduleMemo.isEmpty() == true){
                    for(int i=0; i<CalenderActivity.setSchedule.size(); i++){
                        if(CalenderActivity.setSchedule.get(i).getScheduleName().equals(selectTitle)){
                            CalenderActivity.setSchedule.set(i, new SaveSchedule(scheduleName, scheduleDate, selectMemo));
                        }
                    }
                }
                else if(scheduleName.isEmpty() == false && scheduleDate.isEmpty() == false && scheduleMemo.isEmpty() == false){
                    for(int i=0; i<CalenderActivity.setSchedule.size(); i++){
                        if(CalenderActivity.setSchedule.get(i).getScheduleDate().equals(selectDate)){
                            CalenderActivity.setSchedule.set(i, new SaveSchedule(scheduleName, scheduleDate, scheduleMemo));
                        }
                    }
                }
                else if(scheduleName.isEmpty() == true && scheduleDate.isEmpty() == true && scheduleMemo.isEmpty() == true){
                    for(int i=0; i<CalenderActivity.setSchedule.size(); i++){
                        if(CalenderActivity.setSchedule.get(i).getScheduleDate().equals(selectDate)){
                            CalenderActivity.setSchedule.set(i, new SaveSchedule(selectTitle, selectDate, selectMemo));
                        }
                    }
                }
                else if(scheduleName.isEmpty() == true && scheduleDate.isEmpty() == false && scheduleMemo.isEmpty() == true){
                    for(int i=0; i<CalenderActivity.setSchedule.size(); i++){
                        if(CalenderActivity.setSchedule.get(i).getScheduleDate().equals(selectDate)){
                            CalenderActivity.setSchedule.set(i, new SaveSchedule(selectTitle, scheduleDate, selectMemo));
                        }
                    }
                }
                else if(scheduleName.isEmpty() == true && scheduleDate.isEmpty() == true && scheduleMemo.isEmpty() == false){
                    for(int i=0; i<CalenderActivity.setSchedule.size(); i++){
                        if(CalenderActivity.setSchedule.get(i).getScheduleDate().equals(selectDate)){
                            CalenderActivity.setSchedule.set(i, new SaveSchedule(selectTitle, selectDate, scheduleMemo));
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
            if(CalenderActivity.setSchedule.get(position).getScheduleName().equals(dataLoad.user.getNotes().get(i).getName())){
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
                Intent intent = new Intent(view.getContext(), TransformActivity.class);
                intent.putExtra("schedule_name", selectTitle);
                view.getContext().startActivity(intent);
            }
        });

        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
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