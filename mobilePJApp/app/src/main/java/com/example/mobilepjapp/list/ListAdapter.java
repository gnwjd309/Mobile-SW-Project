package com.example.mobilepjapp.list;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilepjapp.HttpConnection;
import com.example.mobilepjapp.R;
import com.example.mobilepjapp.SaveSchedule;
import com.example.mobilepjapp.calender.CalenderActivity;
import com.example.mobilepjapp.model.DataLoad;
import com.example.mobilepjapp.note.NoteActivity;
import com.example.mobilepjapp.record.TransformActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ListAdapter extends BaseAdapter {
    ArrayList<SaveSchedule> schedules;
    LayoutInflater layoutInflater;
    Context context = null;
    String selectTitle = null;
    String selectDate = null;
    String selectMemo = null;

    AlertDialog dialog;
    HttpConnection http = null;
    DataLoad dataLoad = null;

    ImageView recordImage = null;

    // 생성자 생성
    public ListAdapter(Context context, ArrayList<SaveSchedule> schedules) {
        this.context = context;
        this.schedules = schedules;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return schedules.size();
    }

    @Override
    public Object getItem(int position) {
        return schedules.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_schedule_list, parent, false);
        }

        TranslateAnimation translateAnimation = new TranslateAnimation(300, 0, 0, 0);
        Animation alphaAnimation = new AlphaAnimation(0, 1);
        translateAnimation.setDuration(500);
        alphaAnimation.setDuration(1300);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(translateAnimation);
        animation.addAnimation(alphaAnimation);
        convertView.setAnimation(animation);

        http = new HttpConnection();
        dataLoad = new DataLoad();

        TextView schedulelsit_tv = (TextView) convertView.findViewById(R.id.schedulelist_tv);
        System.out.println("position : " + position);
        selectTitle = schedules.get(position).getScheduleName();
        schedulelsit_tv.setText(selectTitle);

        recordImage = (ImageView) convertView.findViewById(R.id.recordImage);

        for(int i=0; i<dataLoad.user.getNotes().size(); i++){
            if(CalenderActivity.setSchedule.get(position).getScheduleName().equals(dataLoad.user.getNotes().get(i).getName())){
                recordImage.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

    public String selectTitle(){
        return selectTitle;
    }
    public String selectDate(){
        return selectDate;
    }
    public String selectMemo(){
        return selectMemo;
    }
}